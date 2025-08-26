package com.example.bank_system.service;

import com.example.bank_system.entity.Transaction;
import com.example.bank_system.event.TransactionEvent;
import com.example.bank_system.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionFailureService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;
    private final TransactionEventBuilder transactionEventBuilder;

    /**
     * Handle transfer failure in a separate transaction to ensure the failure record is persisted
     * even when the main transaction is rolled back.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleTransferFailure(Transaction transaction, BigDecimal fromBalanceBefore,
                                    BigDecimal toBalanceBefore, String currency, Exception originalException) {
        try {
            // Mark transaction as failed
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setProcessedAt(LocalDateTime.now());

            // Save failed transaction in a separate transaction
            Transaction failedTransaction = transactionRepository.save(transaction);

            // Send failed transaction event
            TransactionEvent failedEvent = transactionEventBuilder.buildFailedTransferEvent(
                failedTransaction, fromBalanceBefore, toBalanceBefore, currency);
            transactionProducer.sendTransactionEvent(failedEvent);

            log.info("Failed transaction recorded with ID: {}", failedTransaction.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to record transaction failure for transaction ID: {}",
                     transaction.getTransactionId(), e);
            // Don't throw here - we want the original exception to be thrown by the caller
        }
    }
}
