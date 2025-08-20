package com.example.bank_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CreateBusinessCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Business registration number is required")
    private String businessRegistrationNumber;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Tax ID is required")
    private String taxId;
}
