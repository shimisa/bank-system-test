package com.example.bank_system.service;

import com.example.bank_system.dto.TransferRequest;
import com.example.bank_system.dto.TransactionEventResponse;
import com.example.bank_system.dto.TransactionApiResponse;
import com.example.bank_system.entity.*;
import com.example.bank_system.event.TransactionEvent;
import com.example.bank_system.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionProducer transactionProducer;
    private final TransactionEventBuilder transactionEventBuilder;

    public TransactionApiResponse processTransfer(TransferRequest request) {
        log.info("Processing transfer from {} to {} amount: {} {}",
                request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount(), request.getCurrency());

        // Validate currency is provided
        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            throw new RuntimeException("Currency is required and cannot be null or empty");
        }

        // Find accounts
        Account fromAccount = accountService.findAccountByNumber(request.getFromAccountNumber());
        Account toAccount = accountService.findAccountByNumber(request.getToAccountNumber());

        // Validate request currency matches account currencies
        validateRequestCurrency(request.getCurrency(), fromAccount, toAccount);

        // Store balances before transfer
        BigDecimal fromBalanceBefore = fromAccount.getBalance();
        BigDecimal toBalanceBefore = toAccount.getBalance();

        // Validate transfer
        validateTransfer(fromAccount, toAccount, request.getAmount());

        // Create transaction record
        Transaction transaction = createTransaction(fromAccount, toAccount, request);

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        // Mark transaction as completed
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setProcessedAt(LocalDateTime.now());

        try {
            // Only wrap the database save operation
            transaction = transactionRepository.save(transaction);
        } catch (Exception e) {
            // Mark transaction as failed
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            // Send failed transaction event
            TransactionEvent failedEvent = transactionEventBuilder.buildFailedTransferEvent(
                transaction, fromBalanceBefore, toBalanceBefore, request.getCurrency());
            transactionProducer.sendTransactionEvent(failedEvent);

            log.error("Transfer failed. Transaction ID: {}", transaction.getTransactionId(), e);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        // Get balances after transfer
        BigDecimal fromBalanceAfter = fromAccount.getBalance();
        BigDecimal toBalanceAfter = toAccount.getBalance();

        // Send successful transaction event with complete details
        TransactionEvent successEvent = transactionEventBuilder.buildSuccessfulTransferEvent(
            transaction, fromBalanceBefore, fromBalanceAfter, toBalanceBefore, toBalanceAfter, request.getCurrency());
        transactionProducer.sendTransactionEvent(successEvent);

        // Build and return simplified API response (without sensitive data)
        TransactionApiResponse response = buildTransactionApiResponse(transaction, request.getCurrency());

        log.info("Transfer completed successfully. Transaction ID: {}", transaction.getTransactionId());
        return response;
    }

    private void validateRequestCurrency(String requestCurrency, Account fromAccount, Account toAccount) {
        // Check if request currency matches source account currency
        if (!fromAccount.getCurrency().name().equals(requestCurrency)) {
            throw new RuntimeException(String.format(
                "Source account currency mismatch: expected %s but request currency is %s",
                fromAccount.getCurrency().name(), requestCurrency));
        }

        // Check if request currency matches destination account currency
        if (!toAccount.getCurrency().name().equals(requestCurrency)) {
            throw new RuntimeException(String.format(
                "Destination account currency mismatch: expected %s but request currency is %s",
                toAccount.getCurrency().name(), requestCurrency));
        }
    }

    private void validateTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        // Check if accounts are active
        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active");
        }
        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account is not active");
        }

        // Check currency compatibility
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new RuntimeException(String.format(
                "Currency mismatch: Source account currency is %s but destination account currency is %s. " +
                "Cross-currency transfers are not supported.",
                fromAccount.getCurrency().getDisplayName(),
                toAccount.getCurrency().getDisplayName()));
        }

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Prevent self-transfer
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
    }

    private Transaction createTransaction(Account fromAccount, Account toAccount, TransferRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(request.getReferenceNumber());

        return transactionRepository.save(transaction);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public TransactionApiResponse buildTransactionApiResponse(Transaction transaction, String currency) {
        TransactionApiResponse response = new TransactionApiResponse();
        response.setEventType("transaction");
        response.setTimestamp(transaction.getProcessedAt() != null ? transaction.getProcessedAt() : transaction.getCreatedAt());
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(currency);
        response.setDescription(transaction.getDescription());
        response.setStatus(transaction.getStatus().name());

        // Build simplified account summaries (without sensitive data like balances)
        TransactionApiResponse.AccountSummary fromAccountSummary = new TransactionApiResponse.AccountSummary();
        fromAccountSummary.setId(transaction.getFromAccount().getId());
        fromAccountSummary.setCustomerName(transaction.getFromAccount().getCustomer().getName());
        fromAccountSummary.setCustomerType(getCustomerType(transaction.getFromAccount().getCustomer()));
        response.setFromAccount(fromAccountSummary);

        TransactionApiResponse.AccountSummary toAccountSummary = new TransactionApiResponse.AccountSummary();
        toAccountSummary.setId(transaction.getToAccount().getId());
        toAccountSummary.setCustomerName(transaction.getToAccount().getCustomer().getName());
        toAccountSummary.setCustomerType(getCustomerType(transaction.getToAccount().getCustomer()));
        response.setToAccount(toAccountSummary);

        return response;
    }

    private String getCustomerType(Customer customer) {
        // Handle Hibernate proxy case
        Customer realCustomer = customer;
        if (customer.getClass().getName().contains("HibernateProxy")) {
            realCustomer = (Customer) org.hibernate.Hibernate.unproxy(customer);
        }

        if (realCustomer instanceof IndividualCustomer) {
            return "individual";
        } else if (realCustomer instanceof BusinessCustomer) {
            return "business";
        } else if (realCustomer instanceof VIPCustomer) {
            return "vip";
        }

        log.warn("Unknown customer type for customer ID: {}, class: {}", customer.getId(), realCustomer.getClass().getName());
        return "unknown";
    }

    private TransactionEventResponse.CustomerDetails buildCustomerDetails(Customer customer) {
        TransactionEventResponse.CustomerDetails customerDetails = new TransactionEventResponse.CustomerDetails();
        customerDetails.setId(customer.getId());
        customerDetails.setName(customer.getName());

        // Set type and specific identifiers based on customer type
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) customer;
            customerDetails.setType("individual");
            customerDetails.setPersonalId(individual.getNationalId());
        } else if (customer instanceof BusinessCustomer) {
            BusinessCustomer business = (BusinessCustomer) customer;
            customerDetails.setType("business");
            customerDetails.setBusinessNumber(business.getBusinessRegistrationNumber());
        } else if (customer instanceof VIPCustomer) {
            customerDetails.setType("vip");
            // VIP customers might have personal ID if they extend individual
            customerDetails.setPersonalId("VIP-" + customer.getId());
        }

        return customerDetails;
    }
}
