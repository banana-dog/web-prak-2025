package ru.cmc.msu.web_prak_2025.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.List;

public interface BankBranchDAO extends CommonDAO<BankBranch, Long> {
    List<BankBranch> getByFilter(Filter filter);

    List<Client> getClientsByBranchId(Long branchId);

    @Builder
    @Getter
    @AllArgsConstructor
    class Filter {
        public String name;
        public Integer clientsNumber;
    }

}