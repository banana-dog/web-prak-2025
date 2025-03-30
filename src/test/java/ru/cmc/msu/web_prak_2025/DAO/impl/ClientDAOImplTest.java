package ru.cmc.msu.web_prak_2025.DAO.impl;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.models.Client;
import ru.cmc.msu.web_prak_2025.models.Client.ClientType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.*;

import static org.testng.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ClientDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Client> criteriaQuery;

    @Mock
    private Root<Client> root;

    @Mock
    private TypedQuery<Client> typedQuery;

    @Mock
    private Query nativeQuery;

    private ClientDAOImpl clientDAO;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        clientDAO = new ClientDAOImpl(Client.class); // Добавьте такой конструктор в ClientDAOImpl
        clientDAO.entityManager = entityManager;
    }

    @Test
    public void getByFilter_AllFilters() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder()
                .firstName("John")
                .lastName("Doe")
                .type(ClientType.NATURAL_PERSON)
                .build();

        List<Client> expectedClients = Arrays.asList(
                new Client("John", "Doe", "contact1", ClientType.NATURAL_PERSON),
                new Client("John", "Doe", "contact2", ClientType.NATURAL_PERSON)
        );

        when(typedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = clientDAO.getByFilter(filter);

        assertEquals(result, expectedClients);
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getByFilter_FirstNameOnly() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder()
                .firstName("John")
                .build();

        List<Client> expectedClients = Collections.singletonList(
                new Client("John", "Smith", "contact", ClientType.LEGAL_ENTITY)
        );

        when(typedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = clientDAO.getByFilter(filter);

        assertEquals(result, expectedClients);
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getByFilter_LastNameOnly() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder()
                .lastName("Doe")
                .build();

        List<Client> expectedClients = Collections.singletonList(
                new Client("Jane", "Doe", "contact", ClientType.NATURAL_PERSON)
        );

        when(typedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = clientDAO.getByFilter(filter);

        assertEquals(result, expectedClients);
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getByFilter_TypeOnly() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder()
                .type(ClientType.LEGAL_ENTITY)
                .build();

        List<Client> expectedClients = Arrays.asList(
                new Client("Company1", "Inc", "contact1", ClientType.LEGAL_ENTITY),
                new Client("Company2", "LLC", "contact2", ClientType.LEGAL_ENTITY)
        );

        when(typedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = clientDAO.getByFilter(filter);

        assertEquals(result, expectedClients);
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getByFilter_EmptyFilter() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder().build();

        List<Client> expectedClients = Arrays.asList(
                new Client("John", "Doe", "contact1", ClientType.NATURAL_PERSON),
                new Client("Jane", "Smith", "contact2", ClientType.NATURAL_PERSON),
                new Client("Company", "Inc", "contact3", ClientType.LEGAL_ENTITY)
        );

        when(typedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = clientDAO.getByFilter(filter);

        assertEquals(result, expectedClients);
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getByFilter_NoResults() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Client.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        ClientDAO.Filter filter = ClientDAO.getFilterBuilder()
                .firstName("Nonexistent")
                .build();

        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Client> result = clientDAO.getByFilter(filter);

        assertTrue(result.isEmpty());
        verify(criteriaQuery, times(1)).select(root);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void getClientDetails_Success() {
        Long clientId = 1L;
        String expectedJson = "{\"id\":1,\"name\":\"John Doe\"}";

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", clientId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(expectedJson);

        Optional<String> result = clientDAO.getClientDetails(clientId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), expectedJson);
        verify(nativeQuery, times(1)).setParameter("id", clientId);
        verify(nativeQuery, times(1)).getSingleResult();
    }

    @Test
    public void getClientDetails_NotFound() {
        Long clientId = 999L;

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", clientId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(null);

        Optional<String> result = clientDAO.getClientDetails(clientId);

        assertFalse(result.isPresent());
        verify(nativeQuery, times(1)).setParameter("id", clientId);
        verify(nativeQuery, times(1)).getSingleResult();
    }

    @Test
    public void getClientDetails_Exception() {
        Long clientId = 1L;

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", clientId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));

        Optional<String> result = clientDAO.getClientDetails(clientId);

        assertFalse(result.isPresent());
        verify(nativeQuery, times(1)).setParameter("id", clientId);
        verify(nativeQuery, times(1)).getSingleResult();
    }

    @Test
    public void likeExpr_Test() {
        String input = "test";

        String result = clientDAO.likeExpr(input);

        assertEquals(result, "%test%");
    }
}