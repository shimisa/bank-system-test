package com.example.transaction_analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionEvent {
    @JsonProperty("eventType")
    private String eventType;

    private Instant timestamp;

    @JsonProperty("transactionId")
    private Long transactionId;

    @JsonProperty("fromAccount")
    private Account fromAccount;

    @JsonProperty("toAccount")
    private Account toAccount;

    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionMetadata metadata;
}
