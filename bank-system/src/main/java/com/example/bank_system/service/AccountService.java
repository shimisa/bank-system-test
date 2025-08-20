package com.example.bank_system.service;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.*;
import com.example.bank_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for customer ID: {}, type: {}, currency: {}, initial balance: {}",
                request.getCustomerId(), request.getAccountType(), request.getCurrency(), request.getInitialBalance());

        Customer customer = customerService.findCustomerById(request.getCustomerId());

        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setCurrency(request.getCurrency());
        account.setCustomer(customer);
        account.setStatus(Account.AccountStatus.ACTIVE);

        account = accountRepository.save(account);

        log.info("Account created successfully - Account Number: {}, Customer: {}, Currency: {}",
                account.getAccountNumber(), customer.getName(), account.getCurrency());

        return mapToAccountResponse(account);
    }

    public Account findAccountByNumber(String accountNumber) {
        log.info("Looking up account by number: {}", accountNumber);

        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> {
                log.warn("Account not found: {}", accountNumber);
                return new RuntimeException("Account not found: " + accountNumber);
            });
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        log.info("Getting account response for account number: {}", accountNumber);
        Account account = findAccountByNumber(accountNumber);
        return mapToAccountResponse(account);
    }

    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        log.info("Retrieving accounts for customer ID: {}", customerId);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
            .map(this::mapToAccountResponse)
            .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + System.currentTimeMillis() +
                          String.format("%03d", (int)(Math.random() * 1000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getBalance(),
            account.getCurrency(), // Add currency field
            account.getStatus(),
            account.getCustomer().getId(),
            account.getCustomer().getName(),
            account.getCreatedAt()
        );
    }
}
