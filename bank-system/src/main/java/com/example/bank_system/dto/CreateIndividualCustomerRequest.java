package com.example.bank_system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CreateIndividualCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    private String occupation;
}
