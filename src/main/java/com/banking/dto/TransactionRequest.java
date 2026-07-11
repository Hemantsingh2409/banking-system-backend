package com.banking.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String description;
    private String type;
}
