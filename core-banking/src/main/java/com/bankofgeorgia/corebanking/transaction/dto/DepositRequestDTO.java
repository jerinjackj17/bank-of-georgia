package com.bankofgeorgia.corebanking.transaction.dto;

import java.math.BigDecimal;

public class DepositRequestDTO {

    private String accountNumber;
    private BigDecimal amount;
    private String description;

    public DepositRequestDTO() {
        // Required for JSON request mapping.
    }

    public DepositRequestDTO(String accountNumber, BigDecimal amount, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}