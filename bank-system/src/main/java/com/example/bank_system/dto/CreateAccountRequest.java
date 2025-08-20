package com.example.bank_system.dto;

import com.example.bank_system.entity.Account;
import com.example.bank_system.entity.Currency;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @NotNull(message = "Currency is required")
    private Currency currency = Currency.USD; // Default to USD
}
