package ru.cmc.msu.web_prak_2025.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.Collection;

@Controller
@RequestMapping("/clients")
public class ClientViewController {
    private final ClientDAO clientDAO;
    private final AccountDAO accountDAO;

    @Autowired
    public ClientViewController(ClientDAO clientDAO, AccountDAO accountDAO) {
        this.clientDAO = clientDAO;
        this.accountDAO = accountDAO;
    }

    @GetMapping("")
    public String showClientsPage(Model model) {
        Collection<Client> clients = clientDAO.getAll();
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/{id}")
    public String showClientDetails(@PathVariable Long id, Model model) {
        try {
            String clientDetails = clientDAO.getClientDetails(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            model.addAttribute("clientDetails", clientDetails);
            return "client_details";
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
}