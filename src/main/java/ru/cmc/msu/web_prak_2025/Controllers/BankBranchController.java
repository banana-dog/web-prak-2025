package ru.cmc.msu.web_prak_2025.Controllers;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/bank")
public class BankBranchController {
    private final BankBranchDAO bankBranchDAO;

    @Autowired
    public BankBranchController(BankBranchDAO bankBranchDAO) {
        this.bankBranchDAO = bankBranchDAO;
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<BankBranch>> getAllBankBranches() {
        Collection<BankBranch> bankBranches = bankBranchDAO.getAll();
        return ResponseEntity.ok(bankBranches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankBranch> getBankBranchById(@PathVariable Long id) {
        Optional<BankBranch> client = Optional.ofNullable(bankBranchDAO.getById(id));
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/clients")
    @Transactional
    public ResponseEntity<Collection<Client>> getAllClientsByBankBranchId(@PathVariable Long id) {
        List<Client> clients = bankBranchDAO.getClientsByBranchId(id);

        if (clients.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        clients.forEach(client -> client.setAccounts(null));

        return ResponseEntity.ok(clients);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BankBranch>> getBankBranchByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer clientsNumber) {

        List<BankBranch> branches;
        BankBranchDAO.Filter f = new BankBranchDAO.Filter(name, clientsNumber);
        branches = bankBranchDAO.getByFilter(f);

        return ResponseEntity.ok(branches);
    }

    @Transactional
    @RequestMapping(value = "/add_new_branch", method = RequestMethod.POST)
    public ResponseEntity<Void> addBankBranch(@RequestBody BankBranch newBranch) {
        try {
            bankBranchDAO.save(newBranch);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editBankBranch(@PathVariable("id") Long id,
                                               @RequestBody BankBranch branchUpdates) {
        BankBranch branch = bankBranchDAO.getById(id);

        if (branch == null) {
            return ResponseEntity.notFound().build();
        }

        branch.setName(branchUpdates.getName());
        branch.setAddress(branchUpdates.getAddress());
        branch.setClientsNumber(branchUpdates.getClientsNumber());

        bankBranchDAO.update(branch);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankBranch(@PathVariable Long id) {
        Optional<BankBranch> branch = Optional.ofNullable(bankBranchDAO.getById(id));
        if (branch.isPresent()) {
            bankBranchDAO.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


