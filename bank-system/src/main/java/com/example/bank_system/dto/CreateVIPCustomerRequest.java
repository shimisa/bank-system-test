package com.example.bank_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class CreateVIPCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "VIP level is required")
    private String vipLevel; // GOLD, PLATINUM, DIAMOND

    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum balance must be positive")
    private BigDecimal minimumBalance;

    private String personalBanker;

    private String specialServices;
}
