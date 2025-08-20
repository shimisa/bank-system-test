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

        log.info("Building customer details for customer ID: {}, class: {}, actual class: {}",
                customer.getId(), customer.getClass().getSimpleName(), customer.getClass().getName());

        // Force Hibernate to load the actual customer data
        Customer realCustomer = customer;
        if (customer.getClass().getName().contains("HibernateProxy")) {
            log.info("Customer is a Hibernate proxy, attempting to get real implementation");
            // Handle Hibernate proxy case
            realCustomer = (Customer) org.hibernate.Hibernate.unproxy(customer);
            log.info("Real customer class after unproxying: {}", realCustomer.getClass().getName());
        }

        // Set type and specific identifiers based on customer type
        if (realCustomer instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) realCustomer;
            customerDetails.setType("individual");
            customerDetails.setPersonalId(individual.getNationalId());
            log.info("Individual customer - nationalId: {}", individual.getNationalId());
        } else if (realCustomer instanceof BusinessCustomer) {
            BusinessCustomer business = (BusinessCustomer) realCustomer;
            customerDetails.setType("business");
            customerDetails.setBusinessNumber(business.getBusinessRegistrationNumber());
            log.info("Business customer - registrationNumber: {}", business.getBusinessRegistrationNumber());
        } else if (realCustomer instanceof VIPCustomer) {
            VIPCustomer vip = (VIPCustomer) realCustomer;
            customerDetails.setType("vip");
            // VIP customers should use a standard VIP identifier
            customerDetails.setPersonalId("VIP-" + customer.getId());
            // Keep businessNumber as null for VIP customers unless they extend BusinessCustomer
            customerDetails.setBusinessNumber(null);
            log.info("VIP customer - personalId: {}, vipLevel: {}", customerDetails.getPersonalId(), vip.getVipLevel());
        } else {
            customerDetails.setType("unknown");
            log.error("Unknown customer type for customer ID: {}, class: {}, superclass: {}",
                    customer.getId(), realCustomer.getClass().getName(), realCustomer.getClass().getSuperclass().getName());
        }

        log.info("Final customer details - ID: {}, type: {}, personalId: {}, businessNumber: {}",
                customerDetails.getId(), customerDetails.getType(),
                customerDetails.getPersonalId(), customerDetails.getBusinessNumber());

        return customerDetails;
    }
}
