package ru.cmc.msu.web_prak_2025.DAO.impl;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import lombok.Setter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.cmc.msu.web_prak_2025.models.*;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountDAOImpl extends CommonDAOImpl<Account, Long> implements AccountDAO {
    @PersistenceContext
    public EntityManager entityManager;

    @Setter
    private TransactionTemplate transactionTemplate;

    public AccountDAOImpl(PlatformTransactionManager transactionManager) {
        super(Account.class);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<Account> getByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
        Root<Account> root = criteriaQuery.from(Account.class);
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getClientId() != null)
            predicates.add(criteriaBuilder.equal(root.get("clientId"), filter.getClientId()));

        if (filter.getAccountType() != null)
            predicates.add(criteriaBuilder.equal(root.get("accountType"), filter.getAccountType()));

        if (filter.getOpeningDate() != null)
            predicates.add(criteriaBuilder.equal(root.get("openingDate"), filter.getOpeningDate()));

        if (!predicates.isEmpty())
            criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Account> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    @Override
    public Optional<String> getAccountDetails(Long id) {
        try {
            String sql = "SELECT    get_account_info(:id, (SELECT account_type FROM account WHERE account_id = :id))";
            Query query = entityManager.createNativeQuery(sql, String.class);
            query.setParameter("id", id);

            String accountJson = (String) query.getSingleResult();
            return Optional.ofNullable(accountJson);
        } catch (Exception e) {
            System.out.println("getAccountDetails exception: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void performTransaction(Long accountId, float amount, float rate) {
        try {
            transactionTemplate.execute(status -> {
                Account account = entityManager.find(Account.class, accountId);
                if (account == null) {
                    throw new RuntimeException("Account not found, id: " + accountId);
                }

                // Логика выполнения транзакции
                float newBalance = getBalance(amount, account, rate);
                account.setCurrentBalance(newBalance);
                entityManager.merge(account);

                return null;
            });
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static float getBalance(float amount, Account account, float rate) {
        float currentBalance = account.getCurrentBalance();
        float rateAmount = Math.abs(amount) * (Math.abs(rate) / 100);
        float delta = (rate > 0) ? (amount - rateAmount) : (amount + rateAmount);
        return currentBalance + delta;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createCheckingAccount(AccountAdditionalInfo accountInfo, Long clientId) {
        CheckingAccount account = new CheckingAccount(getById(clientId), accountInfo.getOverdraftLimit());
        entityManager.persist(account);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createSavingsAccount(AccountAdditionalInfo accountInfo, Long clientId) {
        SavingsAccount account = new SavingsAccount(getById(clientId), accountInfo.getInterestRate(), accountInfo.getInterestPayoutInterval(), accountInfo.getWithdrawalLimit());
        entityManager.persist(account);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createCreditAccount(AccountAdditionalInfo accountInfo, Long clientId) {
        CreditAccount account = new CreditAccount(getById(clientId), accountInfo.getMaxCredit(), accountInfo.getCurrentDebt(), accountInfo.getInterestRate(), accountInfo.getRepaymentRestriction(), accountInfo.getInterestPayoutInterval(), accountInfo.getPaymentMethod());
        entityManager.persist(account);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createDepositAccount(AccountAdditionalInfo accountInfo, Long clientId) {
        DepositAccount account = new DepositAccount(getById(clientId), accountInfo.getInterestRate(), accountInfo.getMaturityDate(), accountInfo.getPaymentMethod());
        entityManager.persist(account);
    }
}