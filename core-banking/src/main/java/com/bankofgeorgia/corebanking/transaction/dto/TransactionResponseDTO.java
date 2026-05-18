package com.bankofgeorgia.corebanking.transaction.dto;

import java.math.BigDecimal;

public class TransactionResponseDTO {

    private final String id;
    private final String transactionId;
    private final String accountId;
    private final String accountNumber;
    private final String customerId;
    private final String type;
    private final BigDecimal amount;
    private final String description;
    private final BigDecimal balanceAfter;
    private final String status;
    private final String createdAt;

    public TransactionResponseDTO(String id,
                                  String transactionId,
                                  String accountId,
                                  String accountNumber,
                                  String customerId,
                                  String type,
                                  BigDecimal amount,
                                  String description,
                                  BigDecimal balanceAfter,
                                  String status,
                                  String createdAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}