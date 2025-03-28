package ru.cmc.msu.web_prak_2025.Controllers;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.Account;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountDAO accountDAO;
    private final BankBranchDAO bankBranchDAO;
    private final ClientDAO clientDAO;

    @Autowired
    public AccountController(AccountDAO accountDAO, BankBranchDAO bankBranchDAO, ClientDAO clientDAO) {
        this.accountDAO = accountDAO;
        this.bankBranchDAO = bankBranchDAO;
        this.clientDAO = clientDAO;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAll() {
        List<Account> clients = (List<Account>) accountDAO.getAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> client = Optional.ofNullable(accountDAO.getById(id));
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<String> getAccountDetails(@PathVariable Long id) {
        Optional<String> accountDetails = accountDAO.getAccountDetails(id);
        return accountDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Account>> getAccountsByFilter(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Account.AccountType accountType,
            @RequestParam(required = false) Date openingDate) {

        List<Account> accounts;
        AccountDAO.Filter f = new AccountDAO.Filter(clientId, accountType, openingDate);
        accounts = accountDAO.getByFilter(f);

        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        Optional<Account> account = Optional.ofNullable(accountDAO.getById(id));
        if (account.isPresent()) {
            accountDAO.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add_new_account")
    @Transactional
    public ResponseEntity<Void> createAccount(@RequestBody AccountDAO.AccountAllInfo accountInfo) {
        try {
            AccountDAO.AccountMainInfo mainInfo = accountInfo.getMainInfo();
            Account account = new Account(clientDAO.getById(mainInfo.getClientId()), mainInfo.getStatus(), mainInfo.getCurrency(), mainInfo.getBalance(), mainInfo.getType(), bankBranchDAO.getById(mainInfo.getBankBranchId()), mainInfo.getOpeningDate());
            accountDAO.save(account);

            AccountDAO.AccountAdditionalInfo additionalInfo = accountInfo.getAdditionalInfo();
            switch (account.getAccountType()) {
                case CHECKING:
                    accountDAO.createCheckingAccount(additionalInfo, mainInfo.getId());
                    break;
                case SAVINGS:
                    accountDAO.createSavingsAccount(additionalInfo, mainInfo.getId());
                    break;
                case CREDIT:
                    accountDAO.createCreditAccount(additionalInfo, mainInfo.getId());
                    break;
                case DEPOSIT:
                    accountDAO.createDepositAccount(additionalInfo, mainInfo.getId());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported account type: " + account.getAccountType());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accounts/{id}/transaction")
    public ResponseEntity<Void> performTransaction(@PathVariable Long id, @RequestParam float amount, @RequestParam float rate) {
        try {
            accountDAO.performTransaction(id, amount, rate);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}