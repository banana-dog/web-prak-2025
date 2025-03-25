package ru.cmc.msu.web_prak_2025.DAO.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.Client;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientDAOImpl extends CommonDAOImpl<Client, Long> implements ClientDAO {
    @PersistenceContext
    protected EntityManager entityManager;

    public ClientDAOImpl() {
        super(Client.class);
    }

    @Override
    public List<Client> getByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);

        criteriaQuery.select(criteriaBuilder.construct(
                Client.class,
                root.get("id"),
                root.get("firstName"),
                root.get("lastName"),
                root.get("type")
        ));

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getFirstName() != null)
            predicates.add(criteriaBuilder.like(root.get("firstName"), likeExpr(filter.getFirstName())));

        if (filter.getLastName() != null)
            predicates.add(criteriaBuilder.like(root.get("lastName"), likeExpr(filter.getLastName())));

        if (filter.getType() != null)
            predicates.add(criteriaBuilder.equal(root.get("type"), filter.getType()));

        if (!predicates.isEmpty())
            criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Client> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    @Override
    public Optional<String> getClientDetails(Long id) {
        try {
            String sql = "SELECT get_client_info(:id)";
            Query query = entityManager.createNativeQuery(sql, String.class);
            query.setParameter("id", id);

            String clientJson = (String) query.getSingleResult();
            return Optional.ofNullable(clientJson);
        } catch (Exception e) {
            System.out.println("getClientDetails exception: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}

