package com.example.transaction_analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String name;
    private String type;

    @JsonProperty("personalId")
    private String personalId;

    @JsonProperty("businessNumber")
    private String businessNumber;
}
