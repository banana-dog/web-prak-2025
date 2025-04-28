package ru.cmc.msu.web_prak_2025.DAO.impl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @InjectMocks
    private ClientDAOImpl clientDAO;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        clientDAO.entityManager = entityManager;
        clientDAO.setEntityManager(entityManager);
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

    @Test
    public void testGetById() {
        Long clientId = 1L;
        Client expectedClient = new Client("John", "Doe", "contact", ClientType.NATURAL_PERSON);
        expectedClient.setId(clientId);

        when(entityManager.find(Client.class, clientId)).thenReturn(expectedClient);

        Client result = clientDAO.getById(clientId);

        assertEquals(result, expectedClient);
        verify(entityManager, times(1)).find(Client.class, clientId);
    }

    @Test
    public void testGetAll() {
        List<Client> expectedClients = Arrays.asList(
                new Client("John", "Doe", "contact1", ClientType.NATURAL_PERSON),
                new Client("Jane", "Smith", "contact2", ClientType.NATURAL_PERSON)
        );

        when(entityManager.createQuery(anyString(), eq(Client.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedClients);

        Collection<Client> result = clientDAO.getAll();

        assertEquals(result.size(), expectedClients.size());
        assertTrue(result.containsAll(expectedClients));
        verify(entityManager, times(1))
                .createQuery("FROM Client", Client.class);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void testSave() {
        Client newClient = new Client("New", "Client", "contact", ClientType.LEGAL_ENTITY);

        clientDAO.save(newClient);

        verify(entityManager, times(1)).persist(newClient);
    }

    @Test
    public void testUpdate() {
        Client existingClient = new Client("Existing", "Client", "contact", ClientType.NATURAL_PERSON);
        existingClient.setId(1L);

        when(entityManager.merge(existingClient)).thenReturn(existingClient);

        clientDAO.update(existingClient);

        verify(entityManager, times(1)).merge(existingClient);
    }

    @Test
    public void testDelete() {
        Client clientToDelete = new Client("ToDelete", "Client", "contact", ClientType.LEGAL_ENTITY);
        clientToDelete.setId(2L);

        clientDAO.delete(clientToDelete);

        verify(entityManager, times(1)).remove(clientToDelete);
    }

    @Test
    public void testDeleteById() {
        Long clientId = 3L;
        Client clientToDelete = new Client("ToDeleteById", "Client", "contact", ClientType.NATURAL_PERSON);
        clientToDelete.setId(clientId);

        when(entityManager.find(Client.class, clientId)).thenReturn(clientToDelete);

        clientDAO.deleteById(clientId);

        verify(entityManager, times(1)).find(Client.class, clientId);
        verify(entityManager, times(1)).remove(clientToDelete);
    }

    @Test
    public void testSaveCollection_Null() {
        clientDAO.saveCollection(null);

        verify(entityManager, never()).persist(any());
    }

    @Test
    public void testSaveCollection_Empty() {
        List<Client> emptyList = Collections.emptyList();

        clientDAO.saveCollection(emptyList);

        verify(entityManager, never()).persist(any());
    }

    @Test
    public void testSaveCollection() {
        List<Client> clients = Arrays.asList(
                new Client("Client1", "Last1", "contact1", ClientType.NATURAL_PERSON),
                new Client("Client2", "Last2", "contact2", ClientType.LEGAL_ENTITY)
        );

        clientDAO.saveCollection(clients);

        verify(entityManager, times(clients.size())).persist(any(Client.class));
        for (Client client : clients) {
            verify(entityManager, times(1)).persist(client);
        }
    }

    @Test
    public void testSaveCollection_WithNonNullIds() {
        List<Client> clients = Arrays.asList(
                new Client("Client1", "Last1", "contact1", ClientType.NATURAL_PERSON),
                new Client("Client2", "Last2", "contact2", ClientType.LEGAL_ENTITY)
        );
        clients.get(0).setId(1L);
        clients.get(1).setId(2L);

        clientDAO.saveCollection(clients);

        assertNull(clients.get(0).getId());
        assertNull(clients.get(1).getId());

        verify(entityManager, times(clients.size())).persist(any(Client.class));
        for (Client client : clients) {
            verify(entityManager, times(1)).persist(client);
        }
    }

    @Test
    public void testSaveCollection_WithNullIds() {
        List<Client> clients = Arrays.asList(
                new Client("Client1", "Last1", "contact1", ClientType.NATURAL_PERSON),
                new Client("Client2", "Last2", "contact2", ClientType.LEGAL_ENTITY)
        );
        clients.get(0).setId(null);
        clients.get(1).setId(null);

        clientDAO.saveCollection(clients);

        assertNull(clients.get(0).getId());
        assertNull(clients.get(1).getId());

        verify(entityManager, times(clients.size())).persist(any(Client.class));
        for (Client client : clients) {
            verify(entityManager, times(1)).persist(client);
        }
    }
}