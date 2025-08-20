package com.example.transaction_analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class Account {
    private Long id;

    @JsonProperty("balanceBefore")
    private BigDecimal balanceBefore;

    @JsonProperty("balanceAfter")
    private BigDecimal balanceAfter;

    private Customer customer;
}
