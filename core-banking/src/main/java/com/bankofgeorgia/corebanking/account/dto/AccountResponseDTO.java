package com.bankofgeorgia.corebanking.account.dto;

import java.math.BigDecimal;

public class AccountResponseDTO {

    private final String id;
    private final String accountNumber;
    private final String customerId;
    private final String productId;
    private final String productType;
    private final BigDecimal balance;
    private final String status;
    private final String openedByEmployeeId;
    private final String updatedByEmployeeId;
    private final String createdAt;
    private final String updatedAt;

    public AccountResponseDTO(String id,
                              String accountNumber,
                              String customerId,
                              String productId,
                              String productType,
                              BigDecimal balance,
                              String status,
                              String openedByEmployeeId,
                              String updatedByEmployeeId,
                              String createdAt,
                              String updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.productId = productId;
        this.productType = productType;
        this.balance = balance;
        this.status = status;
        this.openedByEmployeeId = openedByEmployeeId;
        this.updatedByEmployeeId = updatedByEmployeeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductType() {
        return productType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public String getOpenedByEmployeeId() {
        return openedByEmployeeId;
    }

    public String getUpdatedByEmployeeId() {
        return updatedByEmployeeId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
