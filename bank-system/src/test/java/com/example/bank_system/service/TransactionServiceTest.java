package com.example.bank_system.service;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.*;
import com.example.bank_system.event.TransactionEvent;
import com.example.bank_system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private TransactionEventBuilder transactionEventBuilder;

    @InjectMocks
    private TransactionService transactionService;

    private TransferRequest transferRequest;
    private Account fromAccount;
    private Account toAccount;
    private Transaction transaction;
    private IndividualCustomer fromCustomer;
    private BusinessCustomer toCustomer;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequest();
        transferRequest.setFromAccountNumber("ACC123456789");
        transferRequest.setToAccountNumber("ACC987654321");
        transferRequest.setAmount(new BigDecimal("500.00"));
        transferRequest.setCurrency("USD");
        transferRequest.setDescription("Test transfer");
        transferRequest.setReferenceNumber("REF001");

        fromCustomer = new IndividualCustomer();
        fromCustomer.setId(1L);
        fromCustomer.setName("John Doe");

        toCustomer = new BusinessCustomer();
        toCustomer.setId(2L);
        toCustomer.setName("Tech Corp");

        fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("ACC123456789");
        fromAccount.setAccountType(Account.AccountType.CHECKING);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setCurrency(Currency.USD);
        fromAccount.setStatus(Account.AccountStatus.ACTIVE);
        fromAccount.setCustomer(fromCustomer);

        toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setAccountNumber("ACC987654321");
        toAccount.setAccountType(Account.AccountType.BUSINESS);
        toAccount.setBalance(new BigDecimal("2000.00"));
        toAccount.setCurrency(Currency.USD);
        toAccount.setStatus(Account.AccountStatus.ACTIVE);
        toAccount.setCustomer(toCustomer);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionId("TXN123456789");
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription("Test transfer");
        transaction.setReferenceNumber("REF001");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setProcessedAt(LocalDateTime.now());
    }

    @Test
    void processTransfer_Success() {
        // Given
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);
        when(accountService.findAccountByNumber("ACC987654321")).thenReturn(toAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionEvent successEvent = new TransactionEvent();
        when(transactionEventBuilder.buildSuccessfulTransferEvent(any(), any(), any(), any(), any(), anyString()))
            .thenReturn(successEvent);

        // When
        TransactionApiResponse response = transactionService.processTransfer(transferRequest);

        // Then
        assertNotNull(response);
        assertEquals("transaction", response.getEventType());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        assertEquals("USD", response.getCurrency());
        assertEquals("Test transfer", response.getDescription());
        assertEquals("COMPLETED", response.getStatus());

        // Verify account balances were updated
        assertEquals(new BigDecimal("500.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("2500.00"), toAccount.getBalance());

        verify(accountService).findAccountByNumber("ACC123456789");
        verify(accountService).findAccountByNumber("ACC987654321");
        verify(transactionRepository, times(2)).save(any(Transaction.class)); // Once for creation, once for completion
        verify(transactionProducer).sendTransactionEvent(successEvent);
    }

    @Test
    void processTransfer_MissingCurrency_ThrowsException() {
        // Given
        transferRequest.setCurrency(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));
        assertEquals("Currency is required and cannot be null or empty", exception.getMessage());

        verify(accountService, never()).findAccountByNumber(anyString());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processTransfer_InsufficientBalance_ThrowsException() {
        // Given
        fromAccount.setBalance(new BigDecimal("100.00")); // Less than transfer amount
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);
        when(accountService.findAccountByNumber("ACC987654321")).thenReturn(toAccount);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));
        assertEquals("Insufficient balance", exception.getMessage());

        verify(accountService).findAccountByNumber("ACC123456789");
        verify(accountService).findAccountByNumber("ACC987654321");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processTransfer_SameAccount_ThrowsException() {
        // Given
        transferRequest.setToAccountNumber("ACC123456789"); // Same as from account
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));
        assertEquals("Cannot transfer to the same account", exception.getMessage());
    }

    @Test
    void processTransfer_CurrencyMismatch_ThrowsException() {
        // Given - Set fromAccount to EUR but keep request currency as USD
        // This will trigger validateRequestCurrency error first
        fromAccount.setCurrency(Currency.EUR);
        toAccount.setCurrency(Currency.USD);
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);
        when(accountService.findAccountByNumber("ACC987654321")).thenReturn(toAccount);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));
        assertTrue(exception.getMessage().contains("Source account currency mismatch"));
        assertTrue(exception.getMessage().contains("expected EUR but request currency is USD"));
    }

    @Test
    void processTransfer_InactiveAccount_ThrowsException() {
        // Given
        fromAccount.setStatus(Account.AccountStatus.INACTIVE);
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);
        when(accountService.findAccountByNumber("ACC987654321")).thenReturn(toAccount);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));
        assertEquals("Source account is not active", exception.getMessage());
    }

    @Test
    void processTransfer_DatabaseError_HandlesException() {
        // Given
        when(accountService.findAccountByNumber("ACC123456789")).thenReturn(fromAccount);
        when(accountService.findAccountByNumber("ACC987654321")).thenReturn(toAccount);

        // Mock the repository save calls:
        // 1st call: createTransaction method (succeeds)
        // 2nd call: completion save in try block (fails)
        // 3rd call: failed transaction save in catch block (succeeds)
        Transaction createdTransaction = new Transaction();
        createdTransaction.setId(1L);
        createdTransaction.setTransactionId("TXN123456789");
        createdTransaction.setFromAccount(fromAccount);
        createdTransaction.setToAccount(toAccount);
        createdTransaction.setAmount(new BigDecimal("500.00"));
        createdTransaction.setType(Transaction.TransactionType.TRANSFER);
        createdTransaction.setStatus(Transaction.TransactionStatus.PENDING);

        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(createdTransaction)  // 1st call: createTransaction succeeds
            .thenThrow(new RuntimeException("Database error"))  // 2nd call: completion fails
            .thenReturn(createdTransaction);  // 3rd call: failed transaction save succeeds

        TransactionEvent failedEvent = new TransactionEvent();
        when(transactionEventBuilder.buildFailedTransferEvent(any(), any(), any(), anyString()))
            .thenReturn(failedEvent);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> transactionService.processTransfer(transferRequest));

        // Use contains() for more flexible matching
        assertTrue(exception.getMessage().contains("Database error"));

        verify(transactionProducer).sendTransactionEvent(failedEvent);
        verify(transactionRepository, times(3)).save(any(Transaction.class));
    }

    @Test
    void buildTransactionApiResponse_Success() {
        // When
        TransactionApiResponse response = transactionService.buildTransactionApiResponse(transaction, "USD");

        // Then
        assertNotNull(response);
        assertEquals("transaction", response.getEventType());
        assertEquals(1L, response.getTransactionId());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        assertEquals("USD", response.getCurrency());
        assertEquals("Test transfer", response.getDescription());
        assertEquals("PENDING", response.getStatus());

        assertNotNull(response.getFromAccount());
        assertEquals(1L, response.getFromAccount().getId());
        assertEquals("John Doe", response.getFromAccount().getCustomerName());
        assertEquals("individual", response.getFromAccount().getCustomerType());

        assertNotNull(response.getToAccount());
        assertEquals(2L, response.getToAccount().getId());
        assertEquals("Tech Corp", response.getToAccount().getCustomerName());
        assertEquals("business", response.getToAccount().getCustomerType());
    }
}
