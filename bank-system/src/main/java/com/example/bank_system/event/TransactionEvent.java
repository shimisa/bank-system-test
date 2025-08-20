package com.example.bank_system.event;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String eventType; // "transaction"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private Long transactionId;
    private AccountDetails fromAccount;
    private AccountDetails toAccount;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Metadata metadata;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountDetails {
        private Long id;
        private BigDecimal balanceBefore;
        private BigDecimal balanceAfter;
        private CustomerDetails customer;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerDetails {
        private Long id;
        private String name;
        private String type; // "individual", "business", "vip"
        private String personalId; // for individual customers
        private String businessNumber; // for business customers
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metadata {
        private String processedBy;
        private String source;
    }
}
