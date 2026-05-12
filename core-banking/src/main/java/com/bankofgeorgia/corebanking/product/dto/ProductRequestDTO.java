package com.bankofgeorgia.corebanking.product.dto;

import java.math.BigDecimal;

public class ProductRequestDTO {

    private String productName;
    private String productType;
    private String description;
    private BigDecimal monthlyMaintenanceFee;
    private BigDecimal minimumBalance;
    private String createdByEmployeeId;

    public ProductRequestDTO() {
        // Required for JSON request mapping.
    }

    public ProductRequestDTO(String productName,
                             String productType,
                             String description,
                             BigDecimal monthlyMaintenanceFee,
                             BigDecimal minimumBalance,
                             String createdByEmployeeId) {
        this.productName = productName;
        this.productType = productType;
        this.description = description;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.minimumBalance = minimumBalance;
        this.createdByEmployeeId = createdByEmployeeId;
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

    public String getCreatedByEmployeeId() {
        return createdByEmployeeId;
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

    public void setCreatedByEmployeeId(String createdByEmployeeId) {
        this.createdByEmployeeId = createdByEmployeeId;
    }
}