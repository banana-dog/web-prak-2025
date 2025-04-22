package ru.cmc.msu.web_prak_2025.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.models.BankBranch;

import java.util.Collection;

@Controller
@RequestMapping("/bank")
public class BankBranchViewController {
    private final BankBranchDAO bankBranchDAO;

    @Autowired
    public BankBranchViewController(BankBranchDAO bankBranchDAO) {
        this.bankBranchDAO = bankBranchDAO;
    }

    @GetMapping("") // Обрабатывает GET-запросы на /bank_branches
    public String showBankBranches(Model model) {
        Collection<BankBranch> branches = bankBranchDAO.getAll();
        model.addAttribute("branches", branches); // Передаёт список отделений в шаблон
        return "bank_branches"; // Ищет файл bank_branches.html в templates/
    }
}