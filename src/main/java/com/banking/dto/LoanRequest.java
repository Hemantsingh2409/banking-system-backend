package com.banking.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequest {
    private BigDecimal amount;
    private Double interestRate;
    private Integer tenureMonths;
    private String loanType;
}
