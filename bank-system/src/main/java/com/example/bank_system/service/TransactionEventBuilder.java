package com.example.bank_system.service;

import com.example.bank_system.entity.Transaction;
import com.example.bank_system.entity.Account;
import com.example.bank_system.entity.Customer;
import com.example.bank_system.entity.IndividualCustomer;
import com.example.bank_system.entity.BusinessCustomer;
import com.example.bank_system.entity.VIPCustomer;
import com.example.bank_system.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class TransactionEventBuilder {

    /**
     * Builds a complete TransactionEvent from a Transaction entity with balance information
     */
    public TransactionEvent buildTransactionEvent(Transaction transaction, String eventType,
                                                 BigDecimal fromBalanceBefore, BigDecimal fromBalanceAfter,
                                                 BigDecimal toBalanceBefore, BigDecimal toBalanceAfter,
                                                 String currency) {
        log.debug("Building transaction event for transaction ID: {}", transaction.getTransactionId());

        TransactionEvent event = new TransactionEvent();
        event.setEventType(eventType);
        event.setTimestamp(transaction.getProcessedAt() != null ? transaction.getProcessedAt() : transaction.getCreatedAt());
        event.setTransactionId(transaction.getId());
        event.setAmount(transaction.getAmount());
        event.setCurrency(currency);
        event.setDescription(transaction.getDescription());

        // Build from account details
        TransactionEvent.AccountDetails fromAccount = new TransactionEvent.AccountDetails();
        fromAccount.setId(transaction.getFromAccount().getId());
        fromAccount.setBalanceBefore(fromBalanceBefore);
        fromAccount.setBalanceAfter(fromBalanceAfter);
        fromAccount.setCustomer(buildCustomerDetails(transaction.getFromAccount().getCustomer()));
        event.setFromAccount(fromAccount);

        // Build to account details
        TransactionEvent.AccountDetails toAccount = new TransactionEvent.AccountDetails();
        toAccount.setId(transaction.getToAccount().getId());
        toAccount.setBalanceBefore(toBalanceBefore);
        toAccount.setBalanceAfter(toBalanceAfter);
        toAccount.setCustomer(buildCustomerDetails(transaction.getToAccount().getCustomer()));
        event.setToAccount(toAccount);

        // Build metadata
        TransactionEvent.Metadata metadata = new TransactionEvent.Metadata();
        metadata.setProcessedBy("bank-core-service");
        metadata.setSource("api/v1/transfer");
        event.setMetadata(metadata);

        return event;
    }

    /**
     * Builds a TransactionEvent for successful transfer
     */
    public TransactionEvent buildSuccessfulTransferEvent(Transaction transaction,
                                                        BigDecimal fromBalanceBefore, BigDecimal fromBalanceAfter,
                                                        BigDecimal toBalanceBefore, BigDecimal toBalanceAfter,
                                                        String currency) {
        return buildTransactionEvent(transaction, "transaction",
                                   fromBalanceBefore, fromBalanceAfter,
                                   toBalanceBefore, toBalanceAfter, currency);
    }

    /**
     * Builds a TransactionEvent for failed transfer
     */
    public TransactionEvent buildFailedTransferEvent(Transaction transaction,
                                                    BigDecimal fromBalanceBefore, BigDecimal toBalanceBefore,
                                                    String currency) {
        return buildTransactionEvent(transaction, "transaction_failed",
                                   fromBalanceBefore, fromBalanceBefore, // No balance change on failure
                                   toBalanceBefore, toBalanceBefore, currency);
    }

    /**
     * Builds a TransactionEvent for pending transfer
     */
    public TransactionEvent buildPendingTransferEvent(Transaction transaction, String currency) {
        BigDecimal fromBalance = transaction.getFromAccount().getBalance();
        BigDecimal toBalance = transaction.getToAccount().getBalance();

        return buildTransactionEvent(transaction, "transaction_pending",
                                   fromBalance, fromBalance, // No balance change yet
                                   toBalance, toBalance, currency);
    }

    /**
     * Builds customer details based on customer type
     */
    private TransactionEvent.CustomerDetails buildCustomerDetails(Customer customer) {
        TransactionEvent.CustomerDetails customerDetails = new TransactionEvent.CustomerDetails();
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
            customerDetails.setPersonalId("VIP-" + customer.getId());
        }

        return customerDetails;
    }
}
