package ru.cmc.msu.web_prak_2025.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.cmc.msu.web_prak_2025.DAO.impl.AccountDAOImpl;
import ru.cmc.msu.web_prak_2025.models.Account;
import ru.cmc.msu.web_prak_2025.models.BankBranch;
import ru.cmc.msu.web_prak_2025.models.Client;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountDAOTest {

    @InjectMocks
    private AccountDAOImpl accountDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TypedQuery<Account> typedQuery;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder;

    @Mock
    private jakarta.persistence.criteria.CriteriaQuery<Account> criteriaQuery;

    @Mock
    private jakarta.persistence.criteria.Root<Account> root;

    @Mock
    private Query nativeQuery;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transactionTemplate = new TransactionTemplate(transactionManager);
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
    void testGetByFilter() {
        AccountDAO.Filter filter = new AccountDAO.Filter(1L, Account.AccountType.CHECKING, null);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Account.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Account.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(account));

        List<Account> result = accountDAO.getByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(account, result.getFirst());
    }

    @Test
    void testGetAccountDetails_Found() {
        Long accountId = 1L;
        String expectedJson = "{\"id\":1,\"balance\":1000}";

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", accountId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(expectedJson);

        Optional<String> result = accountDAO.getAccountDetails(accountId);

        assertTrue(result.isPresent());
        assertEquals(expectedJson, result.get());
    }

    @Test
    void testGetAccountDetails_NotFound() {
        Long accountId = 2L;

        when(entityManager.createNativeQuery(anyString(), eq(String.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", accountId)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenThrow(new NoResultException());

        Optional<String> result = accountDAO.getAccountDetails(accountId);

        assertFalse(result.isPresent());
    }

    @Test
    void testPerformTransaction_Success() {
        Long accountId = 1L;
        float amount = 500.0f;
        float rate = 10.0f;

        when(entityManager.find(Account.class, accountId)).thenReturn(account);
        when(entityManager.merge(any(Account.class))).thenReturn(account);

        accountDAO.performTransaction(accountId, amount, rate);

        float expectedNewBalance = 1000.0f + 500.0f - (500.0f * 0.1f);
        assertEquals(expectedNewBalance, account.getCurrentBalance());
    }

    @Test
    void testPerformTransaction_AccountNotFound() {
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

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountDAO.performTransaction(accountId, amount, rate);
        });

        assertEquals("Account not found, id: 99", exception.getMessage());
    }

    @Test
    void testGetBalance_PositiveRate() {
        float newBalance = AccountDAOImpl.getBalance(1000, account, 10);
        assertEquals(1000 + 1000 - (1000 * 0.1f), newBalance);
    }

    @Test
    void testGetBalance_NegativeRate() {
        float newBalance = AccountDAOImpl.getBalance(1000, account, -10);
        assertEquals(1000 + 1000 + (1000 * 0.1f), newBalance);
    }

    @Test
    void testCreateCheckingAccount() {
        AccountDAO.AccountAdditionalInfo info = mock(AccountDAO.AccountAdditionalInfo.class);
        when(info.getOverdraftLimit()).thenReturn(500.0f);
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createCheckingAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }

    @Test
    void testCreateSavingsAccount() {
        AccountDAO.AccountAdditionalInfo info = mock(AccountDAO.AccountAdditionalInfo.class);
        when(info.getInterestRate()).thenReturn(5.0f);
        when(info.getInterestPayoutInterval()).thenReturn("Monthly");
        when(info.getWithdrawalLimit()).thenReturn(1000.0f);
        when(entityManager.find(Account.class, 1L)).thenReturn(account);
        doNothing().when(entityManager).persist(any());

        accountDAO.createSavingsAccount(info, 1L);
        verify(entityManager, times(1)).persist(any());
    }

    @Test
    void testCreateCreditAccount() {
        AccountDAO.AccountAdditionalInfo info = mock(AccountDAO.AccountAdditionalInfo.class);
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