package ru.cmc.msu.web_prak_2025.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.models.Account;
import ru.cmc.msu.web_prak_2025.models.BankBranch;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.List;
import java.util.ArrayList;

@Repository
public class BankBranchDAOImpl extends CommonDAOImpl<BankBranch, Long> implements BankBranchDAO {
    @PersistenceContext
    protected EntityManager entityManager;

    public BankBranchDAOImpl() {
        super(BankBranch.class);
    }

    @Override
    public List<BankBranch> getByFilter(BankBranchDAO.Filter filter) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<BankBranch> criteriaQuery = criteriaBuilder.createQuery(BankBranch.class);
            Root<BankBranch> root = criteriaQuery.from(BankBranch.class);

            List<Predicate> predicates = new ArrayList<>();
            if (filter.getName() != null)
                predicates.add(criteriaBuilder.like(root.get("name"), likeExpr(filter.getName())));

            if (filter.getClientsNumber() != null)
                predicates.add(criteriaBuilder.equal(root.get("clientsNumber"), filter.getClientsNumber()));

            if (!predicates.isEmpty())
                criteriaQuery.where(predicates.toArray(new Predicate[0]));

            TypedQuery<BankBranch> typedQuery = entityManager.createQuery(criteriaQuery);
            return typedQuery.getResultList();
        } catch (Exception e) {
            System.err.println("performTransaction error: " + e.getMessage());
            throw new RuntimeException("Error performing transaction", e);
        }
    }

    String likeExpr(String param) {
        return "%" + param + "%";
    }

    @Override
    public List<Client> getClientsByBranchId(Long branchId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Account> account = cq.from(Account.class);
        Join<Account, Client> client = account.join("clientId", JoinType.INNER);

        cq.select(client)
                .where(cb.equal(account.get("branchId").get("id"), branchId))
                .distinct(true);

        return entityManager.createQuery(cq).getResultList();
    }
}