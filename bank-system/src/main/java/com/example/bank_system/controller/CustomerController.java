package com.example.bank_system.controller;

import com.example.bank_system.dto.*;
import com.example.bank_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/individual")
    public ResponseEntity<CustomerResponse> createIndividualCustomer(
            @Valid @RequestBody CreateIndividualCustomerRequest request) {
        CustomerResponse response = customerService.createIndividualCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/business")
    public ResponseEntity<CustomerResponse> createBusinessCustomer(
            @Valid @RequestBody CreateBusinessCustomerRequest request) {
        CustomerResponse response = customerService.createBusinessCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/vip")
    public ResponseEntity<CustomerResponse> createVIPCustomer(
            @Valid @RequestBody CreateVIPCustomerRequest request) {
        CustomerResponse response = customerService.createVIPCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
