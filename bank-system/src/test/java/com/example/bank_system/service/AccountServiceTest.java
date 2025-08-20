package com.example.bank_system.service;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.*;
import com.example.bank_system.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountRequest createAccountRequest;
    private IndividualCustomer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setCustomerId(1L);
        createAccountRequest.setAccountType(Account.AccountType.CHECKING);
        createAccountRequest.setInitialBalance(new BigDecimal("1000.00"));
        createAccountRequest.setCurrency(Currency.USD);

        customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC123456789");
        account.setAccountType(Account.AccountType.CHECKING);
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency(Currency.USD);
        account.setStatus(Account.AccountStatus.ACTIVE);
        account.setCustomer(customer);
        account.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createAccount_Success() {
        // Given
        when(customerService.findCustomerById(1L)).thenReturn(customer);
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        AccountResponse response = accountService.createAccount(createAccountRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ACC123456789", response.getAccountNumber());
        assertEquals(Account.AccountType.CHECKING, response.getAccountType());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
        assertEquals(Currency.USD, response.getCurrency());
        assertEquals(Account.AccountStatus.ACTIVE, response.getStatus());
        assertEquals(1L, response.getCustomerId());
        assertEquals("John Doe", response.getCustomerName());
        
        verify(customerService).findCustomerById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void findAccountByNumber_Success() {
        // Given
        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(account));

        // When
        Account result = accountService.findAccountByNumber("ACC123456789");

        // Then
        assertNotNull(result);
        assertEquals("ACC123456789", result.getAccountNumber());
        assertEquals(Account.AccountType.CHECKING, result.getAccountType());
        verify(accountRepository).findByAccountNumber("ACC123456789");
    }

    @Test
    void findAccountByNumber_NotFound_ThrowsException() {
        // Given
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> accountService.findAccountByNumber("INVALID"));
        assertEquals("Account not found: INVALID", exception.getMessage());
        verify(accountRepository).findByAccountNumber("INVALID");
    }

    @Test
    void getAccountByNumber_Success() {
        // Given
        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(account));

        // When
        AccountResponse response = accountService.getAccountByNumber("ACC123456789");

        // Then
        assertNotNull(response);
        assertEquals("ACC123456789", response.getAccountNumber());
        assertEquals("John Doe", response.getCustomerName());
        verify(accountRepository).findByAccountNumber("ACC123456789");
    }

    @Test
    void getAccountsByCustomerId_Success() {
        // Given
        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("ACC987654321");
        account2.setAccountType(Account.AccountType.SAVINGS);
        account2.setBalance(new BigDecimal("5000.00"));
        account2.setCurrency(Currency.USD);
        account2.setStatus(Account.AccountStatus.ACTIVE);
        account2.setCustomer(customer);
        account2.setCreatedAt(LocalDateTime.now());

        List<Account> accounts = Arrays.asList(account, account2);
        when(accountRepository.findByCustomerId(1L)).thenReturn(accounts);

        // When
        List<AccountResponse> responses = accountService.getAccountsByCustomerId(1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("ACC123456789", responses.get(0).getAccountNumber());
        assertEquals("ACC987654321", responses.get(1).getAccountNumber());
        verify(accountRepository).findByCustomerId(1L);
    }

    @Test
    void getAccountsByCustomerId_EmptyList() {
        // Given
        when(accountRepository.findByCustomerId(1L)).thenReturn(Arrays.asList());

        // When
        List<AccountResponse> responses = accountService.getAccountsByCustomerId(1L);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(accountRepository).findByCustomerId(1L);
    }
}
