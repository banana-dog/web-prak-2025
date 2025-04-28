package ru.cmc.msu.web_prak_2025.DAO.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.cmc.msu.web_prak_2025.DAO.AccountDAO;
import ru.cmc.msu.web_prak_2025.models.Account;
import ru.cmc.msu.web_prak_2025.models.AccountForm;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

public class AccountDAOImplTest {

    @InjectMocks
    private AccountDAOImpl accountDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TypedQuery<Account> typedQuery;

    @Mock
    private jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder;

    @Mock
    private jakarta.persistence.criteria.CriteriaQuery<Account> criteriaQuery;

    @Mock
    private jakarta.persistence.criteria.Root<Account> root;

    @Mock
    private Query nativeQuery;

    private Account account;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        new TransactionTemplate(transactionManager);
        accountDAO = new AccountDAOImpl(transactionManager);
        accountDAO.entityManager = entityManager;

        java.lang.reflect.Field entityManagerField;
        try {
            entityManagerField = AccountDAOImpl.class.getSuperclass().getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(accountDAO, entityManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Ошибка установки entityManager в CommonDAOImpl", e);
        }

        Client client = new Client();
        client.setId(1L);

        BankBranch branch = new BankBranch();
        branch.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setClientId(client);
        account.setBranchId(branch);
        account.setAccountStatus(Account.Status.ACTIVE);
        account.setAccountCurrency(Account.Currency.RUB);
        account.setCurrentBalance(1000.0f);
        account.setAccountType(Account.AccountType.CHECKING);
        account.setOpeningDate(new Date());
    }

    @Test
    public void testGetByFilter_AllFilters() {
        Client client = new Client();
        client.setId(1L);

        Date openingDate = new Date();

        Account account1 = new Account();
        account1.setId(1L);
        account1.setClientId(client);
        account1.setAccountType(Account.AccountType.CHECKING);
        account1.setOpeningDate(openingDate);

        AccountDAO.Filter filter = new AccountDAO.Filter(
                1L,
                Account.AccountType.CHECKING,
                openingDate
        );

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_OnlyClientId() {
        Client client = new Client();
        client.setId(1L);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setClientId(client);
        account1.setAccountType(Account.AccountType.SAVINGS);

        AccountDAO.Filter filter = new AccountDAO.Filter(1L, null, null);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_OnlyAccountType() {
        Client client = new Client();
        client.setId(1L);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setClientId(client);
        account1.setAccountType(Account.AccountType.CREDIT);

        AccountDAO.Filter filter = new AccountDAO.Filter(null, Account.AccountType.CREDIT, null);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_OnlyOpeningDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date openingDate = sdf.parse("2023-01-01");

        Account account1 = new Account();
        account1.setId(1L);
        account1.setOpeningDate(openingDate);

        AccountDAO.Filter filter = new AccountDAO.Filter(null, null, openingDate);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_ClientIdAndAccountType() {
        Client client = new Client();
        client.setId(1L);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setClientId(client);
        account1.setAccountType(Account.AccountType.DEPOSIT);

        AccountDAO.Filter filter = new AccountDAO.Filter(1L, Account.AccountType.DEPOSIT, null);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_ClientIdAndOpeningDate() throws ParseException {
        Client client = new Client();
        client.setId(1L);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date openingDate = sdf.parse("2023-01-01");

        Account account1 = new Account();
        account1.setId(1L);
        account1.setClientId(client);
        account1.setOpeningDate(openingDate);

        AccountDAO.Filter filter = new AccountDAO.Filter(1L, null, openingDate);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_AccountTypeAndOpeningDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date openingDate = sdf.parse("2023-01-01");

        Account account1 = new Account();
        account1.setId(1L);
        account1.setAccountType(Account.AccountType.SAVINGS);
        account1.setOpeningDate(openingDate);

        AccountDAO.Filter filter = new AccountDAO.Filter(null, Account.AccountType.SAVINGS, openingDate);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.getFirst(), account1);
    }

    @Test
    public void testGetByFilter_NoFilters() {
        Account account1 = new Account();
        account1.setId(1L);

        Account account2 = new Account();
        account2.setId(2L);

        AccountDAO.Filter filter = new AccountDAO.Filter(null, null, null);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account1, account2));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(account1));
        assertTrue(result.contains(account2));
    }

    @Test
    public void testGetAccountDetails_Found() {
        Long accountId = 1L;
        String expectedJson = "{\"id\":1,\"balance\":1000}";

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", accountId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(expectedJson);

        Optional<String> result = accountDAO.getAccountDetails(accountId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), expectedJson);
    }

    @Test
    public void testGetAccountDetails_NotFound() {
        Long accountId = 2L;

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", accountId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenThrow(new NoResultException());

        Optional<String> result = accountDAO.getAccountDetails(accountId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testPerformTransaction_Success() {
        Long accountId = 1L;
        float amount = 500.0f;
        float rate = 10.0f;

        when(entityManager.find(Account.class, accountId)).thenReturn(account);
        when(entityManager.merge(any(Account.class))).thenReturn(account);

        accountDAO.performTransaction(accountId, amount, rate);

        float expectedNewBalance = 1000.0f + 500.0f - (500.0f * 0.1f);
        assertEquals(account.getCurrentBalance(), expectedNewBalance);
    }

    @Test(expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Account not found, id: 99")
    public void testPerformTransaction_AccountNotFound() {
        Long accountId = 99L;
        float amount = 500.0f;
        float rate = 10.0f;

        when(entityManager.find(Account.class, accountId)).thenReturn(null);

        TransactionTemplate realTemplate = new TransactionTemplate(transactionManager);
        TransactionTemplate spyTemplate = spy(realTemplate);

        accountDAO.setTransactionTemplate(spyTemplate);

        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        }).when(spyTemplate).execute(any());

        accountDAO.performTransaction(accountId, amount, rate);
    }

    @Test
    public void testGetBalance_PositiveRate() {
        float newBalance = AccountDAOImpl.getBalance(1000, account, 10);
        assertEquals(newBalance, 1000 + 1000 - (1000 * 0.1f));
    }

    @Test
    public void testGetBalance_NegativeRate() {
        float newBalance = AccountDAOImpl.getBalance(1000, account, -10);
        assertEquals(newBalance, 1000 + 1000 + (1000 * 0.1f));
    }

    @Test
    public void testCreateCheckingAccount() {
        AccountForm info = mock(AccountForm.class);
        when(info.getOverdraftLimit()).thenReturn(500.0f);
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createCheckingAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }

    @Test
    public void testCreateSavingsAccount() {
        AccountForm info = mock(AccountForm.class);
        when(info.getInterestRate()).thenReturn(5.0f);
        when(info.getInterestPayoutInterval()).thenReturn("Monthly");
        when(info.getWithdrawalLimit()).thenReturn(1000.0f);
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createSavingsAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }

    @Test
    public void testCreateDepositAccount() throws ParseException {
        AccountForm info = mock(AccountForm.class);
        when(info.getInterestRate()).thenReturn(5.0f);
        when(info.getPaymentMethod()).thenReturn(Account.PaymentMethod.MANUAL);
        String dateString = "2025-03-26";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);
        when(info.getMaturityDate()).thenReturn(date);
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createDepositAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }

    @Test
    public void testCreateCreditAccount() {
        AccountForm info = mock(AccountForm.class);
        when(info.getMaxCredit()).thenReturn(5000.0f);
        when(info.getCurrentDebt()).thenReturn(2000.0f);
        when(info.getInterestRate()).thenReturn(10.0f);
        when(info.getRepaymentRestriction()).thenReturn("No early repayment");
        when(info.getInterestPayoutInterval()).thenReturn("Quarterly");
        when(info.getPaymentMethod()).thenReturn(Account.PaymentMethod.valueOf("MANUAL"));
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createCreditAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }
}