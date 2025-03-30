package ru.cmc.msu.web_prak_2025.DAO.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.cmc.msu.web_prak_2025.DAO.BankBranchDAO;
import ru.cmc.msu.web_prak_2025.models.Account;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

public class BankBranchDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<BankBranch> bankBranchCriteriaQuery;

    @Mock
    private Root<BankBranch> bankBranchRoot;

    @Mock
    private TypedQuery<BankBranch> bankBranchTypedQuery;

    @InjectMocks
    private BankBranchDAOImpl bankBranchDAO;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bankBranchDAO.entityManager = entityManager;
    }

    @Test
    public void testGetByFilter_WithNameFilter() {
        String branchName = "Central";
        BankBranchDAO.Filter filter = new BankBranchDAO.Filter(branchName, null);

        BankBranch branch = new BankBranch();
        branch.setName("Central Branch");
        branch.setClientsNumber(10);

        List<BankBranch> expectedBranches = List.of(branch);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(BankBranch.class)).thenReturn(bankBranchCriteriaQuery);
        when(bankBranchCriteriaQuery.from(BankBranch.class)).thenReturn(bankBranchRoot);

        Predicate namePredicate = mock(Predicate.class);
        when(criteriaBuilder.like(bankBranchRoot.get("name"), "%" + branchName + "%"))
                .thenReturn(namePredicate);

        when(bankBranchCriteriaQuery.where(namePredicate)).thenReturn(bankBranchCriteriaQuery);

        when(entityManager.createQuery(bankBranchCriteriaQuery)).thenReturn(bankBranchTypedQuery);
        when(bankBranchTypedQuery.getResultList()).thenReturn(expectedBranches);

        List<BankBranch> result = bankBranchDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst().getName(), "Central Branch");

        verify(criteriaBuilder).like(bankBranchRoot.get("name"), "%" + branchName + "%");
    }

    @Test
    public void testGetByFilter_WithClientsNumberFilter() {
        Integer clientsNumber = 20;
        BankBranchDAO.Filter filter = new BankBranchDAO.Filter(null, clientsNumber);

        BankBranch branch = new BankBranch();
        branch.setName("Main Branch");
        branch.setClientsNumber(clientsNumber);

        List<BankBranch> expectedBranches = List.of(branch);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(BankBranch.class)).thenReturn(bankBranchCriteriaQuery);
        when(bankBranchCriteriaQuery.from(BankBranch.class)).thenReturn(bankBranchRoot);

        Predicate numberPredicate = mock(Predicate.class);
        when(criteriaBuilder.like(bankBranchRoot.get("name"), "%" + clientsNumber + "%"))
                .thenReturn(numberPredicate);

        when(bankBranchCriteriaQuery.where(numberPredicate)).thenReturn(bankBranchCriteriaQuery);

        when(entityManager.createQuery(bankBranchCriteriaQuery)).thenReturn(bankBranchTypedQuery);
        when(bankBranchTypedQuery.getResultList()).thenReturn(expectedBranches);

        List<BankBranch> result = bankBranchDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst().getClientsNumber(), clientsNumber);

        verify(criteriaBuilder).equal(bankBranchRoot.get("clientsNumber"), clientsNumber);
    }

    @Test
    public void testGetByFilter_WithAllFilters() {
        String branchName = "North";
        Integer clientsNumber = 15;
        BankBranchDAO.Filter filter = new BankBranchDAO.Filter(branchName, clientsNumber);

        BankBranch branch = new BankBranch();
        branch.setName(branchName);
        branch.setClientsNumber(clientsNumber);

        List<BankBranch> expectedBranches = List.of(branch);

        Predicate clientsPredicate = mock(Predicate.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(BankBranch.class)).thenReturn(bankBranchCriteriaQuery);
        when(bankBranchCriteriaQuery.from(BankBranch.class)).thenReturn(bankBranchRoot);

        when(criteriaBuilder.equal(bankBranchRoot.get("clientsNumber"), clientsNumber))
                .thenReturn(clientsPredicate);

        when(bankBranchCriteriaQuery.where(any(Predicate.class), any(Predicate.class)))
                .thenReturn(bankBranchCriteriaQuery);

        when(entityManager.createQuery(bankBranchCriteriaQuery)).thenReturn(bankBranchTypedQuery);
        when(bankBranchTypedQuery.getResultList()).thenReturn(expectedBranches);

        List<BankBranch> result = bankBranchDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst().getName(), branchName);
        assertEquals(result.getFirst().getClientsNumber(), clientsNumber);

        verify(criteriaBuilder).like(bankBranchRoot.get("name"), "%" + branchName + "%");
        verify(criteriaBuilder).equal(bankBranchRoot.get("clientsNumber"), clientsNumber);
    }

    @Test
    public void testGetByFilter_NoFilters() {
        BankBranchDAO.Filter filter = new BankBranchDAO.Filter(null, null);

        List<BankBranch> expectedBranches = new ArrayList<>();
        expectedBranches.add(new BankBranch());
        expectedBranches.add(new BankBranch());

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(BankBranch.class)).thenReturn(bankBranchCriteriaQuery);
        when(bankBranchCriteriaQuery.from(BankBranch.class)).thenReturn(bankBranchRoot);
        when(entityManager.createQuery(bankBranchCriteriaQuery)).thenReturn(bankBranchTypedQuery);
        when(bankBranchTypedQuery.getResultList()).thenReturn(expectedBranches);

        List<BankBranch> result = bankBranchDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 2);
        verify(bankBranchCriteriaQuery, never()).where((Predicate) any());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testGetByFilter_ExceptionHandling() {
        EntityManager mockEm = mock(EntityManager.class);
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("Test exception"));
        bankBranchDAO.entityManager = mockEm;
        BankBranchDAO.Filter filter = new BankBranchDAO.Filter("test", null);
        bankBranchDAO.getByFilter(filter);
    }

    @Test
    public void testLikeExpr() {
        String input = "test";
        String result = bankBranchDAO.likeExpr(input);
        assertEquals(result, "%test%");
    }
    @Mock
    private CriteriaQuery<Client> clientCriteriaQuery;

    @Mock
    private Root<Account> accountRoot;

    @Mock
    private Join<Account, Client> clientJoin;

    @Mock
    private TypedQuery<Client> clientTypedQuery;
    @Mock
    private Path<Object> branchIdPath;
    @Mock
    private Path<Object> idPath;
    @Test
    public void testGetClientsByBranchId() {
        // Setup
        Long branchId = 1L;
        Client client1 = new Client();
        client1.setId(1L);
        Client client2 = new Client();
        client2.setId(2L);
        List<Client> expectedClients = List.of(client1, client2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.from(Account.class)).thenReturn(accountRoot);
        doReturn(clientJoin).when(accountRoot).join("clientId", JoinType.INNER);  // matches Account.clientId field

        Predicate branchPredicate = mock(Predicate.class);
        when(accountRoot.get("branchId")).thenReturn(branchIdPath);
        when(branchIdPath.get("id")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, branchId)).thenReturn(branchPredicate);

        when(clientCriteriaQuery.select(clientJoin)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.where(branchPredicate)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.distinct(true)).thenReturn(clientCriteriaQuery);

        when(entityManager.createQuery(clientCriteriaQuery)).thenReturn(clientTypedQuery);
        when(clientTypedQuery.getResultList()).thenReturn(expectedClients);

        List<Client> result = bankBranchDAO.getClientsByBranchId(branchId);

        assertNotNull(result);
        assertEquals(result.size(), 2);
        verify(entityManager).createQuery(clientCriteriaQuery);
    }

    @Test
    public void testGetClientsByBranchId_NoResults() {
        Long branchId = 999L;

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Client.class)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.from(Account.class)).thenReturn(accountRoot);
        doReturn(clientJoin).when(accountRoot).join("clientId", JoinType.INNER);

        Predicate branchPredicate = mock(Predicate.class);
        when(accountRoot.get("branchId")).thenReturn(branchIdPath);
        when(branchIdPath.get("id")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, branchId)).thenReturn(branchPredicate);

        when(clientCriteriaQuery.select(clientJoin)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.where(branchPredicate)).thenReturn(clientCriteriaQuery);
        when(clientCriteriaQuery.distinct(true)).thenReturn(clientCriteriaQuery);

        when(entityManager.createQuery(clientCriteriaQuery)).thenReturn(clientTypedQuery);
        when(clientTypedQuery.getResultList()).thenReturn(List.of());

        List<Client> result = bankBranchDAO.getClientsByBranchId(branchId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testGetClientsByBranchId_Exception() {
        Long branchId = 1L;
        when(entityManager.getCriteriaBuilder()).thenThrow(new RuntimeException("Database error"));
        bankBranchDAO.getClientsByBranchId(branchId);
    }
}