package com.bankofgeorgia.corebanking.product.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String productName;
    private String productType;
    private String description;
    private BigDecimal monthlyMaintenanceFee;
    private BigDecimal minimumBalance;
    private String status;
    private String createdByEmployeeId;
    private String updatedByEmployeeId;
    private Instant createdAt;
    private Instant updatedAt;

    public Product() {
        // Required by MongoDB.
    }

    public Product(String productName,
                   String productType,
                   String description,
                   BigDecimal monthlyMaintenanceFee,
                   BigDecimal minimumBalance,
                   String status,
                   String createdByEmployeeId,
                   String updatedByEmployeeId,
                   Instant createdAt,
                   Instant updatedAt) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.minimumBalance = minimumBalance;
        this.status = status;
        this.createdByEmployeeId = createdByEmployeeId;
        this.updatedByEmployeeId = updatedByEmployeeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedByEmployeeId() {
        return createdByEmployeeId;
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

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedByEmployeeId(String createdByEmployeeId) {
        this.createdByEmployeeId = createdByEmployeeId;
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