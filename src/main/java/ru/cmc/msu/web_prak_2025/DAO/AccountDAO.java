package ru.cmc.msu.web_prak_2025.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;
import ru.cmc.msu.web_prak_2025.models.Account.AccountType;
import ru.cmc.msu.web_prak_2025.models.Account;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountDAO extends CommonDAO<Account, Long> {
    List<Account> getByFilter(Filter filter);

    Optional<String> getAccountDetails(Long id);

    void performTransaction(Long accountId, float amount, float rate);

    @Transactional
    void createCheckingAccount(AccountAdditionalInfo info, Long accountId);

    @Transactional
    void createSavingsAccount(AccountAdditionalInfo accountInfo, Long clientId);

    @Transactional
    void createCreditAccount(AccountAdditionalInfo accountInfo, Long clientId);

    @Transactional
    void createDepositAccount(AccountAdditionalInfo accountInfo, Long clientId);

    @AllArgsConstructor
    @Builder
    @Getter
    class Filter {
        public Long clientId;
        public AccountType accountType;
        public Date openingDate;
    }

    @Getter
    @Setter
    class AccountAdditionalInfo {
        Float maxCredit;
        Float currentDebt;
        Float interestRate;
        String repaymentRestriction;
        String interestPayoutInterval;
        Account.PaymentMethod paymentMethod;
        Date maturityDate;
        Float withdrawalLimit;
        Float overdraftLimit;
    }

    @Getter
    @Setter
    class AccountMainInfo {
        Long Id;
        Long accountNo;
        Long clientId;
        Account.Status status;
        Account.Currency currency;
        Float balance;
        Account.AccountType type;
        Long bankBranchId;
        Date openingDate;
    }

    @Getter
    @Setter
    public class AccountAllInfo {
        AccountMainInfo mainInfo;
        AccountAdditionalInfo additionalInfo;
    }
}
