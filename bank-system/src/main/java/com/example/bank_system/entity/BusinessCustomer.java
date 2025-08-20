package com.example.bank_system.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BUSINESS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BusinessCustomer extends Customer {

    private String businessRegistrationNumber;
    private String businessType;
    private String industry;
    private String taxId;

    public BusinessCustomer(String name, String email, String phone, String address,
                          String businessRegistrationNumber, String businessType,
                          String industry, String taxId) {
        super();
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.businessType = businessType;
        this.industry = industry;
        this.taxId = taxId;
    }
}
