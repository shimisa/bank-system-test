package com.example.bank_system.dto;

import com.example.bank_system.entity.Account;
import com.example.bank_system.entity.Currency;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private Currency currency;
    private Account.AccountStatus status;
    private Long customerId;
    private String customerName;
    private LocalDateTime createdAt;
}
