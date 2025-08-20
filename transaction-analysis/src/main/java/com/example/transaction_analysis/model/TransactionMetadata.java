package com.example.transaction_analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionMetadata {
    @JsonProperty("processedBy")
    private String processedBy;

    private String source;
}
