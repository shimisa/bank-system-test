package com.example.bank_system.service;

import com.example.bank_system.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TRANSACTION_TOPIC = "transaction";

    /**
     * Sends a transaction event to the 'transaction' topic asynchronously
     */
    public void sendTransactionEvent(TransactionEvent event) {
        try {
            log.debug("Sending transaction event to Kafka topic 'transaction': {}", event.getTransactionId());

            CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TRANSACTION_TOPIC, String.valueOf(event.getTransactionId()), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent transaction event [{}] with offset=[{}]",
                        event.getTransactionId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send transaction event [{}]: {}",
                        event.getTransactionId(), ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error sending transaction event to Kafka for transaction [{}]: {}",
                event.getTransactionId(), e.getMessage(), e);
            // Don't fail the transaction if Kafka is down
        }
    }

    /**
     * Sends a transaction event synchronously to the 'transaction' topic
     */
    public void sendTransactionEventSync(TransactionEvent event) {
        try {
            log.debug("Sending transaction event synchronously to Kafka topic 'transaction': {}", event.getTransactionId());

            SendResult<String, Object> result = kafkaTemplate
                .send(TRANSACTION_TOPIC, String.valueOf(event.getTransactionId()), event)
                .get();

            log.info("Successfully sent transaction event [{}] synchronously with offset=[{}]",
                event.getTransactionId(), result.getRecordMetadata().offset());

        } catch (Exception e) {
            log.error("Error sending transaction event synchronously to Kafka for transaction [{}]: {}",
                event.getTransactionId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send transaction event to Kafka", e);
        }
    }
}
