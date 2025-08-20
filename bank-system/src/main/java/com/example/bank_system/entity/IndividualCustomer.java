package com.example.bank_system.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("INDIVIDUAL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IndividualCustomer extends Customer {

    private String nationalId;
    private String dateOfBirth;
    private String occupation;

    public IndividualCustomer(String name, String email, String phone, String address,
                            String nationalId, String dateOfBirth, String occupation) {
        super();
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;
    }
}
