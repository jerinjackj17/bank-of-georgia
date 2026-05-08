package com.bankofgeorgia.corebanking.account.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "accounts")
public class Account {

    @Id
    private String id;

    private String accountNumber;      // e.g., ACC000001 — auto-generated
    private String customerId;         // MongoDB _id of the Customer
    private String productId;          // MongoDB _id of the Product
    private String productType;        // denormalized from Product (e.g., SAVINGS_ACCOUNT)
    private BigDecimal balance;        // managed by future transaction endpoints
    private String status;             // ACTIVE, FROZEN, CLOSED
    private String openedByEmployeeId; // employeeId (e.g., EMP001)
    private String updatedByEmployeeId;
    private Instant createdAt;
    private Instant updatedAt;

    public Account() {
        // Required by MongoDB.
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOpenedByEmployeeId(String openedByEmployeeId) {
        this.openedByEmployeeId = openedByEmployeeId;
    }

    public void setUpdatedByEmployeeId(String updatedByEmployeeId) {
        this.updatedByEmployeeId = updatedByEmployeeId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
