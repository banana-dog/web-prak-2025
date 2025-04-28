package ru.cmc.msu.web_prak_2025.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.Account;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;
import ru.cmc.msu.web_prak_2025.models.Client.ClientType;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientViewController {
    private final ClientDAO clientDAO;
    private final AccountDAO accountDAO;
    private final BankBranchDAO bankBranchDAO;
    private final ObjectMapper objectMapper;
    private final AccountViewController accountViewController;

    @Autowired
    public ClientViewController(ClientDAO clientDAO, AccountDAO accountDAO, BankBranchDAO bankBranchDAO,ObjectMapper objectMapper, AccountViewController accountViewController) {
        this.clientDAO = clientDAO;
        this.accountDAO = accountDAO;
        this.bankBranchDAO = bankBranchDAO;
        this.objectMapper = objectMapper;
        this.accountViewController = accountViewController;
    }

    @GetMapping("/{id}")
    public String showClientDetails(@PathVariable Long id, Model model) {
        try {
            Client client = clientDAO.getById(id);
            if (client == null) {
                return "redirect:/error";
            }

            // Добавляем основные данные клиента
            model.addAttribute("firstName", client.getFirstName());
            model.addAttribute("lastName", client.getLastName());
            model.addAttribute("contacts", client.getContacts());
            model.addAttribute("type", client.getType());
            model.addAttribute("clientId", client.getId());

            // Получаем и парсим детали счетов
            String clientDetailsJson = clientDAO.getClientDetails(id)
                    .orElseThrow(() -> new RuntimeException("Client details not found"));
            JsonNode clientDetails = objectMapper.readTree(clientDetailsJson);
            model.addAttribute("accounts", clientDetails.get("accounts"));
            List<BankBranch> bankBranches = (List<BankBranch>) bankBranchDAO.getAll();
            model.addAttribute("bankBranches", bankBranches);

            return "client_details";
        } catch (Exception e) {
            e.printStackTrace(); // для отладки
            return "redirect:/error";
        }
    }

    @GetMapping("")
    public String showClientsPage(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String type,
            Model model) {

        if (firstName != null || lastName != null || type != null) {
            ClientType clientType = null;
            if (type != null && !type.isEmpty()) {
                try {
                    clientType = ClientType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    // Handle invalid type
                }
            }
            ClientDAO.Filter filter = new ClientDAO.Filter(firstName, lastName, clientType);
            List<Client> clients = clientDAO.getByFilter(filter);
            model.addAttribute("clients", clients);
        } else {
            Collection<Client> clients = clientDAO.getAll();
            model.addAttribute("clients", clients);
        }

        // Add filter values to model to preserve form state
        model.addAttribute("filterFirstName", firstName);
        model.addAttribute("filterLastName", lastName);
        model.addAttribute("filterType", type);

        return "clients";
    }


    @PostMapping("/add")
    public String addClient(@ModelAttribute("clientInfo") ClientInfo clientInfo) {
        try {
            ClientType clientType = ClientType.valueOf(clientInfo.getType());
            Client newClient = new Client(
                    clientInfo.getFirstName(),
                    clientInfo.getLastName(),
                    clientInfo.getContacts(),
                    clientType
            );
            clientDAO.save(newClient);
            return "redirect:/clients";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/{id}/edit")
    public String editClient(@PathVariable Long id, @ModelAttribute("clientInfo") ClientInfo clientInfo) {
        try {
            Client client = clientDAO.getById(id);
            if (client != null) {
                if (clientInfo.getFirstName() != null) {
                    client.setFirstName(clientInfo.getFirstName());
                }
                if (clientInfo.getLastName() != null) {
                    client.setLastName(clientInfo.getLastName());
                }
                if (clientInfo.getContacts() != null) {
                    client.setContacts(clientInfo.getContacts());
                }
                if (clientInfo.getType() != null) {
                    client.setType(ClientType.valueOf(clientInfo.getType()));
                }
                clientDAO.update(client);
            }
            return "redirect:/clients";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Long id) {
        try {
            clientDAO.deleteById(id);
            return "redirect:/clients";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    @GetMapping("/accounts/{accountId}")
    public String showAccountDetails(@PathVariable Long accountId, Model model) {
        try {
            String accountDetails = accountDAO.getAccountDetails(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            model.addAttribute("accountDetails", accountDetails);
            return "account_details";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }


    @GetMapping("/{clientId}/accounts/new")
    public String showNewAccountForm(
            @PathVariable Long clientId,
            @RequestParam String type,
            Model model) {

        model.addAttribute("clientId", clientId);
        model.addAttribute("accountType", type);
        model.addAttribute("bankBranches", bankBranchDAO.getAll());
        return "new_account_" + type.toLowerCase();
    }

    @PostMapping("/{clientId}/accounts/new")
    public String createAccount(
            @PathVariable Long clientId,
            @RequestParam String type,
            @ModelAttribute AccountForm form,
            RedirectAttributes redirectAttributes) {

        try {
            // Prepare main account info
            AccountDAO.AccountMainInfo mainInfo = new AccountDAO.AccountMainInfo();
            mainInfo.setClientId(clientId);
            mainInfo.setType(Account.AccountType.valueOf(type));
            mainInfo.setCurrency(form.getCurrency());
            mainInfo.setBalance(form.getBalance());
            mainInfo.setBankBranchId(form.getBankBranchId());
            mainInfo.setOpeningDate(new Date()); // or form.getOpeningDate() if you have it
            mainInfo.setStatus(Account.Status.ACTIVE);

            // Prepare additional info based on account type
            AccountDAO.AccountAdditionalInfo additionalInfo = new AccountDAO.AccountAdditionalInfo();

            switch (Account.AccountType.valueOf(type)) {
                case CHECKING:
                    additionalInfo.setOverdraftLimit(form.getOverdraftLimit());
                    break;
                case SAVINGS:
                    additionalInfo.setInterestRate(form.getInterestRate());
                    additionalInfo.setInterestPayoutInterval(form.getInterestPayoutInterval());
                    additionalInfo.setWithdrawalLimit(form.getWithdrawalLimit());
                    break;
                case CREDIT:
                    additionalInfo.setInterestRate(form.getInterestRate());
                    additionalInfo.setMaxCredit(form.getMaxCredit());
                    additionalInfo.setCurrentDebt(0.0f);
                    additionalInfo.setRepaymentRestriction(form.getRepaymentRestriction());
                    additionalInfo.setInterestPayoutInterval(form.getInterestPayoutInterval());
                    additionalInfo.setPaymentMethod(form.getPaymentMethod());
                    break;
                case DEPOSIT:
                    additionalInfo.setInterestRate(form.getInterestRate());
                    additionalInfo.setMaturityDate(form.getMaturityDate());
                    additionalInfo.setPaymentMethod(form.getPaymentMethod());
                    break;
            }

            // Create AccountAllInfo and call AccountController
            AccountDAO.AccountAllInfo accountAllInfo = new AccountDAO.AccountAllInfo();
            accountAllInfo.setMainInfo(mainInfo);
            accountAllInfo.setAdditionalInfo(additionalInfo);

            // You'll need to autowire AccountController or refactor to use AccountDAO directly
            accountViewController.createAccount(accountAllInfo);

            return "redirect:/clients/" + clientId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании счета: " + e.getMessage());
            return "redirect:/clients/" + clientId + "/accounts/new?type=" + type;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ClientInfo {
        private String firstName;
        private String lastName;
        private String contacts;
        private String type;
    }
}