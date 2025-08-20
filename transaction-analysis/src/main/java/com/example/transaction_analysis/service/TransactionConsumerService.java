package com.example.transaction_analysis.service;

import com.example.transaction_analysis.model.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransactionConsumerService {

    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal SUSPICIOUS_AMOUNT_THRESHOLD = new BigDecimal("50000.00");

    @KafkaListener(topics = "transaction", groupId = "transaction-analysis-group")
    public void consumeTransactionEvent(TransactionEvent event) {
        log.info("Received transaction event: {}", event);

        // Perform anomaly analysis
        analyzeTransaction(event);
    }

    private void analyzeTransaction(TransactionEvent event) {
        log.info("=== TRANSACTION ANALYSIS ===");
        log.info("Transaction ID: {}", event.getTransactionId());
        log.info("Amount: {} {}", event.getAmount(), event.getCurrency());
        log.info("From: {} (ID: {}) - Balance: {} -> {}",
                event.getFromAccount().getCustomer().getName(),
                event.getFromAccount().getId(),
                event.getFromAccount().getBalanceBefore(),
                event.getFromAccount().getBalanceAfter());
        log.info("To: {} (ID: {}) - Balance: {} -> {}",
                event.getToAccount().getCustomer().getName(),
                event.getToAccount().getId(),
                event.getToAccount().getBalanceBefore(),
                event.getToAccount().getBalanceAfter());

        // Anomaly checks
        checkForAnomalies(event);

        log.info("=== END ANALYSIS ===");
    }

    private void checkForAnomalies(TransactionEvent event) {
        // Check for large transactions
        if (event.getAmount().compareTo(LARGE_TRANSACTION_THRESHOLD) >= 0) {
            log.warn("🚨 ANOMALY DETECTED: Large transaction detected - Amount: {} {}",
                    event.getAmount(), event.getCurrency());
        }

        // Check for suspicious amounts
        if (event.getAmount().compareTo(SUSPICIOUS_AMOUNT_THRESHOLD) >= 0) {
            log.error("🚨 HIGH RISK ANOMALY: Suspicious large amount - Amount: {} {}",
                    event.getAmount(), event.getCurrency());
        }

        // Check for negative balance after transaction
        if (event.getFromAccount().getBalanceAfter().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("🚨 ANOMALY DETECTED: Negative balance after transaction - Account: {}, Balance: {}",
                    event.getFromAccount().getId(), event.getFromAccount().getBalanceAfter());
        }

        // Check for business to individual large transfers
        if ("business".equals(event.getFromAccount().getCustomer().getType()) &&
            "individual".equals(event.getToAccount().getCustomer().getType()) &&
            event.getAmount().compareTo(new BigDecimal("5000.00")) >= 0) {
            log.warn("🚨 ANOMALY DETECTED: Large business-to-individual transfer - Amount: {} {}",
                    event.getAmount(), event.getCurrency());
        }

        // Check for round number transactions (potential money laundering pattern)
        if (event.getAmount().remainder(new BigDecimal("1000.00")).equals(BigDecimal.ZERO) &&
            event.getAmount().compareTo(new BigDecimal("5000.00")) >= 0) {
            log.warn("🚨 ANOMALY DETECTED: Round number large transaction (potential structuring) - Amount: {} {}",
                    event.getAmount(), event.getCurrency());
        }

        // Check for frequent transactions (this would require storing transaction history)
        // For now, we'll just log if it's processed by API vs other sources
        if (event.getMetadata().getSource().contains("api")) {
            log.info("ℹ️ API-initiated transaction detected");
        }
    }
}
