package ru.cmc.msu.web_prak_2025.DAO.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.cmc.msu.web_prak_2025.DAO.CommonDAO;
import ru.cmc.msu.web_prak_2025.models.CommonEntity;

import java.io.Serializable;
import java.util.Collection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public abstract class CommonDAOImpl<T extends CommonEntity<ID>, ID extends Serializable> implements CommonDAO<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected Class<T> persistentClass;

    public CommonDAOImpl(Class<T> entityClass) {
        this.persistentClass = entityClass;
    }

    @Override
    @Transactional
    public T getById(ID id) {
        return entityManager.find(persistentClass, id);
    }

    @Override
    @Transactional
    public Collection<T> getAll() {
        return entityManager.createQuery("FROM " + persistentClass.getSimpleName(), persistentClass).getResultList();
    }

    @Override
    @Transactional
    public void save(T entity) {
        entityManager.persist(entity);
    }

    @Override
    @Transactional
    public void update(T entity) {
        entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        T entity = entityManager.find(persistentClass, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void saveCollection(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        for (T entity : entities) {
            if (entity.getId() != null) {
                entity.setId(null);
            }
            entityManager.persist(entity);
        }
    }
}