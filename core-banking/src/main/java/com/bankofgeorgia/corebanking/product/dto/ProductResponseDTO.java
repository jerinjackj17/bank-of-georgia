package com.bankofgeorgia.corebanking.product.dto;

import java.math.BigDecimal;

public class ProductResponseDTO {

    private final String id;
    private final String productName;
    private final String productType;
    private final String description;
    private final BigDecimal monthlyMaintenanceFee;
    private final BigDecimal minimumBalance;
    private final String status;
    private final String createdByEmployeeId;
    private final String updatedByEmployeeId;
    private final String createdAt;
    private final String updatedAt;

    public ProductResponseDTO(String id,
                              String productName,
                              String productType,
                              String description,
                              BigDecimal monthlyMaintenanceFee,
                              BigDecimal minimumBalance,
                              String status,
                              String createdByEmployeeId,
                              String updatedByEmployeeId,
                              String createdAt,
                              String updatedAt) {
        this.id = id;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}