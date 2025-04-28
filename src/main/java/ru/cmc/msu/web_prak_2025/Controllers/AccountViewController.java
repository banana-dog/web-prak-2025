package ru.cmc.msu.web_prak_2025.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountViewController {
    private final AccountDAO accountDAO;
    private final BankBranchDAO bankBranchDAO;
    private final ClientDAO clientDAO;

    @Autowired
    public AccountViewController(AccountDAO accountDAO, BankBranchDAO bankBranchDAO, ClientDAO clientDAO) {
        this.accountDAO = accountDAO;
        this.bankBranchDAO = bankBranchDAO;
        this.clientDAO = clientDAO;
    }

    @GetMapping
    public String showAccountsPage(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Account.AccountType accountType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date openingDate,
            @RequestParam(required = false) String firstName,    // Add this
            @RequestParam(required = false) String lastName,     // Add this
            Model model) {

        AccountDAO.Filter filter = AccountDAO.Filter.builder()
                .clientId(clientId)
                .accountType(accountType)
                .openingDate(openingDate)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        List<Account> accounts = accountDAO.getByFilter(filter);
        model.addAttribute("accounts", accounts);
        return "accounts_list";
    }

    @GetMapping("/{id}")
    public String showAccountDetails(@PathVariable Long id, Model model) {
        try {
            Account account = accountDAO.getById(id);
            if (account == null) {
                return "redirect:/error";
            }

            String accountDetailsJson = accountDAO.getAccountDetails(id)
                    .orElseThrow(() -> new RuntimeException("Account details not found"));

            System.out.println("Account Details JSON: " + accountDetailsJson);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode accountDetails = objectMapper.readTree(accountDetailsJson);

            model.addAttribute("account", account);
            model.addAttribute("details", accountDetails);

            return "account_details";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @PostMapping("/{id}/transaction")
    public String performTransaction(@PathVariable Long id,
                                     @RequestParam float amount,
                                     @RequestParam float rate) {
        try {
            Account account = accountDAO.getById(id);
            if (account == null) {
                return "redirect:/error";
            }

            if (account.getAccountStatus() == Account.Status.SUSPENDED) {
                return "redirect:/accounts/" + id;
            }

            accountDAO.performTransaction(id, amount, rate);
            return "redirect:/accounts/" + id;
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAccount(@PathVariable Long id) {
        try {
            accountDAO.deleteById(id);
            return "redirect:/accounts";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/{id}/status")
    public String changeAccountStatus(@PathVariable Long id,
                                      @RequestParam Account.Status newStatus) {
        try {
            Account account = accountDAO.getById(id);
            if (account != null) {
                account.setAccountStatus(newStatus);
                accountDAO.update(account);
            }
            return "redirect:/accounts/" + id;
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @PostMapping("/add_new_account")
    @Transactional
    public ResponseEntity<Void> createAccount(@RequestBody AccountDAO.AccountAllInfo accountInfo) {
        try {
            AccountDAO.AccountMainInfo mainInfo = accountInfo.getMainInfo();
            AccountDAO.AccountAdditionalInfo additionalInfo = accountInfo.getAdditionalInfo();
            if (mainInfo.getClientId() == null || clientDAO.getById(mainInfo.getClientId()) == null) {
                throw new IllegalArgumentException("Invalid client ID");
            }
            if (mainInfo.getBankBranchId() == null || bankBranchDAO.getById(mainInfo.getBankBranchId()) == null) {
                throw new IllegalArgumentException("Invalid bank branch ID");
            }
            Account account = new Account();
            account.setClientId(clientDAO.getById(mainInfo.getClientId()));
            account.setAccountStatus(Account.Status.valueOf(String.valueOf(mainInfo.getStatus())));
            account.setAccountCurrency(Account.Currency.valueOf(String.valueOf(mainInfo.getCurrency())));
            account.setCurrentBalance(mainInfo.getBalance());
            account.setAccountType(Account.AccountType.valueOf(String.valueOf(mainInfo.getType())));
            account.setBranchId(bankBranchDAO.getById(mainInfo.getBankBranchId()));
            account.setOpeningDate(mainInfo.getOpeningDate());

            accountDAO.save(account);

            switch (account.getAccountType()) {
                case CHECKING:
                    CheckingAccount checkingAccount = new CheckingAccount();
                    checkingAccount.setAccount(account);
                    checkingAccount.setOverdraftLimit(additionalInfo.getOverdraftLimit());
                    account.setCheckingAccount(checkingAccount);
                    break;

                case SAVINGS:
                    SavingsAccount savingsAccount = new SavingsAccount();
                    savingsAccount.setAccount(account);
                    savingsAccount.setInterestRate(additionalInfo.getInterestRate());
                    savingsAccount.setInterestPayoutInterval(additionalInfo.getInterestPayoutInterval()); // Добавьте эту строку
                    savingsAccount.setWithdrawalLimit(additionalInfo.getWithdrawalLimit());
                    account.setSavingsAccount(savingsAccount);
                    break;

                case CREDIT:
                    CreditAccount creditAccount = new CreditAccount();
                    creditAccount.setAccount(account);
                    creditAccount.setInterestRate(additionalInfo.getInterestRate());
                    creditAccount.setMaxCredit(additionalInfo.getMaxCredit());
                    creditAccount.setCurrentDebt(additionalInfo.getCurrentDebt());
                    creditAccount.setRepaymentRestriction(additionalInfo.getRepaymentRestriction());
                    creditAccount.setInterestPayoutInterval(additionalInfo.getInterestPayoutInterval());

                    Account.PaymentMethod paymentMethodStr = additionalInfo.getPaymentMethod();
                    if (paymentMethodStr == null) {
                        paymentMethodStr = Account.PaymentMethod.MANUAL; // значение по умолчанию
                    }
                    creditAccount.setPaymentMethod(paymentMethodStr);

                    account.setCreditAccount(creditAccount);
                    break;

                case DEPOSIT:
                    DepositAccount depositAccount = new DepositAccount();
                    depositAccount.setAccount(account);
                    depositAccount.setInterestRate(additionalInfo.getInterestRate());
                    Account.PaymentMethod paymentMethod = additionalInfo.getPaymentMethod();
                    if (paymentMethod == null) {
                        paymentMethod = Account.PaymentMethod.MANUAL;
                    }
                    depositAccount.setPaymentMethod(paymentMethod);
                    depositAccount.setMaturityDate(additionalInfo.getMaturityDate());
                    account.setDepositAccount(depositAccount);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported account type: " + account.getAccountType());
            }

            accountDAO.update(account);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}