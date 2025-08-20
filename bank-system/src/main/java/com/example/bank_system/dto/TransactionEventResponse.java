package com.example.bank_system.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEventResponse {
    private String eventType = "transaction";
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
        private String type;
        private String personalId; // for individual customers
        private String businessNumber; // for business customers
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metadata {
        private String processedBy = "bank-core-service";
        private String source = "api/v1/transfer";
    }
}
