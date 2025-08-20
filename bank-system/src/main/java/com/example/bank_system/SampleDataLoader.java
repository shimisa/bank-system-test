package com.example.bank_system;

import com.example.bank_system.dto.*;
import com.example.bank_system.entity.Account;
import com.example.bank_system.entity.Currency;
import com.example.bank_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Sample data loader to demonstrate the banking system functionality
 */
@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("=== Loading Sample Data for Digital Banking System ===");
//
//        // Create Individual Customer
//        CreateIndividualCustomerRequest individualRequest = new CreateIndividualCustomerRequest();
//        individualRequest.setName("John Doe");
//        individualRequest.setEmail("john.doe@email.com");
//        individualRequest.setPhone("+1234567890");
//        individualRequest.setAddress("123 Main St, City");
//        individualRequest.setNationalId("123456789");
//        individualRequest.setDateOfBirth("1990-01-01");
//        individualRequest.setOccupation("Software Engineer");
//
//        CustomerResponse individual = customerService.createIndividualCustomer(individualRequest);
//        System.out.println("Created Individual Customer: " + individual.getName());
//
//        // Create Business Customer
//        CreateBusinessCustomerRequest businessRequest = new CreateBusinessCustomerRequest();
//        businessRequest.setName("Tech Corp Ltd");
//        businessRequest.setEmail("info@techcorp.com");
//        businessRequest.setPhone("+1987654321");
//        businessRequest.setAddress("456 Business Ave, City");
//        businessRequest.setBusinessRegistrationNumber("BRN123456");
//        businessRequest.setBusinessType("Technology");
//        businessRequest.setIndustry("Software Development");
//        businessRequest.setTaxId("TAX789123");
//
//        CustomerResponse business = customerService.createBusinessCustomer(businessRequest);
//        System.out.println("Created Business Customer: " + business.getName());
//
//        // Create VIP Customer
//        CreateVIPCustomerRequest vipRequest = new CreateVIPCustomerRequest();
//        vipRequest.setName("Alice Smith");
//        vipRequest.setEmail("alice.smith@email.com");
//        vipRequest.setPhone("+1555666777");
//        vipRequest.setAddress("789 Elite Blvd, City");
//        vipRequest.setVipLevel("PLATINUM");
//        vipRequest.setMinimumBalance(new BigDecimal("50000.00"));
//        vipRequest.setPersonalBanker("Robert Johnson");
//        vipRequest.setSpecialServices("24/7 Support, Investment Advisory");
//
//        CustomerResponse vip = customerService.createVIPCustomer(vipRequest);
//        System.out.println("Created VIP Customer: " + vip.getName());
//
//        // Create accounts for customers with different currencies
//        CreateAccountRequest account1 = new CreateAccountRequest();
//        account1.setCustomerId(individual.getId());
//        account1.setAccountType(Account.AccountType.CHECKING);
//        account1.setInitialBalance(new BigDecimal("5000.00"));
//        account1.setCurrency(Currency.USD); // USD account for John
//
//        AccountResponse johnAccount = accountService.createAccount(account1);
//        System.out.println("Created USD account for John: " + johnAccount.getAccountNumber() + " (" + johnAccount.getCurrency() + ")");
//
//        CreateAccountRequest account2 = new CreateAccountRequest();
//        account2.setCustomerId(business.getId());
//        account2.setAccountType(Account.AccountType.BUSINESS);
//        account2.setInitialBalance(new BigDecimal("25000.00"));
//        account2.setCurrency(Currency.USD); // USD account for Tech Corp
//
//        AccountResponse businessAccount = accountService.createAccount(account2);
//        System.out.println("Created USD account for Tech Corp: " + businessAccount.getAccountNumber() + " (" + businessAccount.getCurrency() + ")");
//
//        // Create additional accounts with different currencies for demonstration
//        CreateAccountRequest account3 = new CreateAccountRequest();
//        account3.setCustomerId(individual.getId());
//        account3.setAccountType(Account.AccountType.SAVINGS);
//        account3.setInitialBalance(new BigDecimal("18000.00"));
//        account3.setCurrency(Currency.ILS); // ILS account for John
//
//        AccountResponse johnILSAccount = accountService.createAccount(account3);
//        System.out.println("Created ILS account for John: " + johnILSAccount.getAccountNumber() + " (" + johnILSAccount.getCurrency() + ")");
//
//        CreateAccountRequest account4 = new CreateAccountRequest();
//        account4.setCustomerId(vip.getId());
//        account4.setAccountType(Account.AccountType.VIP);
//        account4.setInitialBalance(new BigDecimal("75000.00"));
//        account4.setCurrency(Currency.EUR); // EUR account for VIP
//
//        AccountResponse vipEURAccount = accountService.createAccount(account4);
//        System.out.println("Created EUR account for Alice (VIP): " + vipEURAccount.getAccountNumber() + " (" + vipEURAccount.getCurrency() + ")");
//
//        System.out.println("=== Sample Data Loaded Successfully ===");
//        System.out.println("You can now test transfers between accounts:");
//        System.out.println("Valid transfers (same currency):");
//        System.out.println("  USD: " + johnAccount.getAccountNumber() + " → " + businessAccount.getAccountNumber() + " (Both USD)");
//        System.out.println("Invalid transfers (different currency):");
//        System.out.println("  Mixed: " + johnAccount.getAccountNumber() + " (USD) → " + johnILSAccount.getAccountNumber() + " (ILS) - Will be blocked");
//        System.out.println("");
//        System.out.println("Account Summary:");
//        System.out.println("- John (Individual): " + johnAccount.getAccountNumber() + " - $5,000 USD");
//        System.out.println("- John (Individual): " + johnILSAccount.getAccountNumber() + " - ₪18,000 ILS");
//        System.out.println("- Tech Corp (Business): " + businessAccount.getAccountNumber() + " - $25,000 USD");
//        System.out.println("- Alice (VIP): " + vipEURAccount.getAccountNumber() + " - €75,000 EUR");
    }
}
