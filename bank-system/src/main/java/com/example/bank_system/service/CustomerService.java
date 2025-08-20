package com.example.bank_system.service;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.*;
import com.example.bank_system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse createIndividualCustomer(CreateIndividualCustomerRequest request) {
        log.info("Creating individual customer: {}", request.getName());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists");
        }

        IndividualCustomer customer = new IndividualCustomer(
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getNationalId(),
            request.getDateOfBirth(),
            request.getOccupation()
        );

        customer = customerRepository.save(customer);
        log.info("Individual customer created successfully with ID: {}", customer.getId());
        return mapToCustomerResponse(customer);
    }

    public CustomerResponse createBusinessCustomer(CreateBusinessCustomerRequest request) {
        log.info("Creating business customer: {}", request.getName());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists");
        }

        BusinessCustomer customer = new BusinessCustomer(
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getBusinessRegistrationNumber(),
            request.getBusinessType(),
            request.getIndustry(),
            request.getTaxId()
        );

        customer = customerRepository.save(customer);
        log.info("Business customer created successfully with ID: {}", customer.getId());
        return mapToCustomerResponse(customer);
    }

    public CustomerResponse createVIPCustomer(CreateVIPCustomerRequest request) {
        log.info("Creating VIP customer: {}", request.getName());

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Customer with email already exists");
        }

        VIPCustomer customer = new VIPCustomer(
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getVipLevel(),
            request.getMinimumBalance(),
            request.getPersonalBanker(),
            request.getSpecialServices()
        );

        customer = customerRepository.save(customer);
        log.info("VIP customer created successfully with ID: {}", customer.getId());
        return mapToCustomerResponse(customer);
    }

    public Customer findCustomerById(Long id) {
        log.info("Looking up customer by ID: {}", id);
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        String customerType = determineCustomerType(customer);

        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getAddress(),
            customerType,
            customer.getCreatedAt()
        );
    }

    private String determineCustomerType(Customer customer) {
        if (customer instanceof BusinessCustomer) {
            return "BUSINESS";
        } else if (customer instanceof VIPCustomer) {
            return "VIP";
        } else if (customer instanceof IndividualCustomer) {
            return "INDIVIDUAL";
        }
        return "UNKNOWN";
    }
}
