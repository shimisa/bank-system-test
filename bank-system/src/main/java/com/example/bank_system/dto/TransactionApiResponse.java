package com.example.bank_system.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionApiResponse {
    private String eventType = "transaction";
    private LocalDateTime timestamp;
    private Long transactionId;
    private AccountSummary fromAccount;
    private AccountSummary toAccount;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountSummary {
        private Long id;
        private String customerName;
        private String customerType;
    }
}
