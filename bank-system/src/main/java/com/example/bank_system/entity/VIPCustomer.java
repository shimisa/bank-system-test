package com.example.bank_system.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("VIP")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VIPCustomer extends Customer {

    private String vipLevel; // GOLD, PLATINUM, DIAMOND
    private BigDecimal minimumBalance;
    private String personalBanker;
    private String specialServices;

    public VIPCustomer(String name, String email, String phone, String address,
                      String vipLevel, BigDecimal minimumBalance,
                      String personalBanker, String specialServices) {
        super();
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        this.vipLevel = vipLevel;
        this.minimumBalance = minimumBalance;
        this.personalBanker = personalBanker;
        this.specialServices = specialServices;
    }
}
