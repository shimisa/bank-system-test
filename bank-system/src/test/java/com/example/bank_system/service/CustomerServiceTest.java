package com.example.bank_system.service;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.*;
import com.example.bank_system.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CreateIndividualCustomerRequest individualRequest;
    private CreateBusinessCustomerRequest businessRequest;
    private CreateVIPCustomerRequest vipRequest;

    @BeforeEach
    void setUp() {
        individualRequest = new CreateIndividualCustomerRequest();
        individualRequest.setName("John Doe");
        individualRequest.setEmail("john@example.com");
        individualRequest.setPhone("+1-555-0123");
        individualRequest.setAddress("123 Main St");
        individualRequest.setNationalId("123-45-6789");
        individualRequest.setDateOfBirth("1990-01-01");
        individualRequest.setOccupation("Engineer");

        businessRequest = new CreateBusinessCustomerRequest();
        businessRequest.setName("Tech Corp");
        businessRequest.setEmail("info@techcorp.com");
        businessRequest.setPhone("+1-555-0456");
        businessRequest.setAddress("456 Business Ave");
        businessRequest.setBusinessRegistrationNumber("BRN-001");
        businessRequest.setBusinessType("Corporation");
        businessRequest.setIndustry("Technology");
        businessRequest.setTaxId("12-3456789");

        vipRequest = new CreateVIPCustomerRequest();
        vipRequest.setName("VIP Client");
        vipRequest.setEmail("vip@example.com");
        vipRequest.setPhone("+1-555-0789");
        vipRequest.setAddress("789 VIP Lane");
        vipRequest.setVipLevel("PLATINUM");
        vipRequest.setMinimumBalance(new BigDecimal("100000"));
        vipRequest.setPersonalBanker("Jane Smith");
        vipRequest.setSpecialServices("Concierge");
    }

    @Test
    void createIndividualCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        IndividualCustomer savedCustomer = new IndividualCustomer();
        savedCustomer.setId(1L);
        savedCustomer.setName("John Doe");
        savedCustomer.setEmail("john@example.com");
        savedCustomer.setCreatedAt(LocalDateTime.now());
        when(customerRepository.save(any(IndividualCustomer.class))).thenReturn(savedCustomer);

        // When
        CustomerResponse response = customerService.createIndividualCustomer(individualRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("INDIVIDUAL", response.getCustomerType());
        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository).save(any(IndividualCustomer.class));
    }

    @Test
    void createIndividualCustomer_EmailExists_ThrowsException() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.createIndividualCustomer(individualRequest));
        assertEquals("Customer with email already exists", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createBusinessCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        BusinessCustomer savedCustomer = new BusinessCustomer();
        savedCustomer.setId(2L);
        savedCustomer.setName("Tech Corp");
        savedCustomer.setEmail("info@techcorp.com");
        savedCustomer.setCreatedAt(LocalDateTime.now());
        when(customerRepository.save(any(BusinessCustomer.class))).thenReturn(savedCustomer);

        // When
        CustomerResponse response = customerService.createBusinessCustomer(businessRequest);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Tech Corp", response.getName());
        assertEquals("BUSINESS", response.getCustomerType());
        verify(customerRepository).save(any(BusinessCustomer.class));
    }

    @Test
    void createVIPCustomer_Success() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        VIPCustomer savedCustomer = new VIPCustomer();
        savedCustomer.setId(3L);
        savedCustomer.setName("VIP Client");
        savedCustomer.setEmail("vip@example.com");
        savedCustomer.setCreatedAt(LocalDateTime.now());
        when(customerRepository.save(any(VIPCustomer.class))).thenReturn(savedCustomer);

        // When
        CustomerResponse response = customerService.createVIPCustomer(vipRequest);

        // Then
        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("VIP Client", response.getName());
        assertEquals("VIP", response.getCustomerType());
        verify(customerRepository).save(any(VIPCustomer.class));
    }

    @Test
    void findCustomerById_Success() {
        // Given
        IndividualCustomer customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setName("John Doe");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerService.findCustomerById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        verify(customerRepository).findById(1L);
    }

    @Test
    void findCustomerById_NotFound_ThrowsException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.findCustomerById(1L));
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository).findById(1L);
    }
}
