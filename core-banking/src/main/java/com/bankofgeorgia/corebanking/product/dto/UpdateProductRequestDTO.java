package com.bankofgeorgia.corebanking.product.dto;

import java.math.BigDecimal;

public class UpdateProductRequestDTO {

    private String productName;
    private String description;
    private BigDecimal monthlyMaintenanceFee;
    private BigDecimal minimumBalance;
    private String updatedByEmployeeId;

    public UpdateProductRequestDTO() {
        // Required for JSON request mapping.
    }

    public UpdateProductRequestDTO(String productName,
                                   String description,
                                   BigDecimal monthlyMaintenanceFee,
                                   BigDecimal minimumBalance,
                                   String updatedByEmployeeId) {
        this.productName = productName;
        this.description = description;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.minimumBalance = minimumBalance;
        this.updatedByEmployeeId = updatedByEmployeeId;
    }

    public String getProductName() {
        return productName;
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

    public String getUpdatedByEmployeeId() {
        return updatedByEmployeeId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public void setUpdatedByEmployeeId(String updatedByEmployeeId) {
        this.updatedByEmployeeId = updatedByEmployeeId;
    }
}