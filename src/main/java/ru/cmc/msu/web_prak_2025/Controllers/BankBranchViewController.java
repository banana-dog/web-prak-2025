package ru.cmc.msu.web_prak_2025.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/bank")
public class BankBranchViewController {
    private final BankBranchDAO bankBranchDAO;

    @Autowired
    public BankBranchViewController(BankBranchDAO bankBranchDAO) {
        this.bankBranchDAO = bankBranchDAO;
    }

    @GetMapping("")
    public String showBankBranches(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer clientsNumber,
                                   Model model) {
        Collection<BankBranch> branches;

        if (name != null || clientsNumber != null) {
            BankBranchDAO.Filter filter = new BankBranchDAO.Filter(name, clientsNumber);
            branches = bankBranchDAO.getByFilter(filter);
        } else {
            branches = bankBranchDAO.getAll();
        }

        model.addAttribute("branches", branches);
        model.addAttribute("filterName", name);
        model.addAttribute("filterClientsNumber", clientsNumber);
        return "bank_branches";
    }

    @PostMapping("/add")
    public String addBankBranch(@ModelAttribute BankBranch branch) {
        bankBranchDAO.save(branch);
        return "redirect:/bank";
    }

    @PostMapping("/{id}/edit")
    public String editBankBranch(@PathVariable Long id, @ModelAttribute BankBranch branchUpdates) {
        BankBranch branch = bankBranchDAO.getById(id);
        if (branch != null) {
            branch.setName(branchUpdates.getName());
            branch.setAddress(branchUpdates.getAddress());
            branch.setClientsNumber(branchUpdates.getClientsNumber());
            bankBranchDAO.update(branch);
        }
        return "redirect:/bank";
    }

    @PostMapping("/{id}/delete")
    public String deleteBankBranch(@PathVariable Long id) {
        bankBranchDAO.deleteById(id);
        return "redirect:/bank";
    }

    @GetMapping("/{id}")
    public String showBankBranchDetails(@PathVariable Long id, Model model) {
        BankBranch branch = bankBranchDAO.getById(id);
        if (branch == null) {
            return "redirect:/bank";
        }

        List<Client> clients = bankBranchDAO.getClientsByBranchId(id);

        model.addAttribute("branch", branch);
        model.addAttribute("clients", clients);
        return "bank_branch_details";
    }
}