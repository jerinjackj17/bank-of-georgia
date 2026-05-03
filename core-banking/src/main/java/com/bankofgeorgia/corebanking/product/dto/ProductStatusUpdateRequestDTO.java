package com.bankofgeorgia.corebanking.product.dto;

public class ProductStatusUpdateRequestDTO {

    private String status;
    private String updatedByEmployeeId;

    public ProductStatusUpdateRequestDTO() {
        // Required for JSON request mapping.
    }

    public ProductStatusUpdateRequestDTO(String status,
                                         String updatedByEmployeeId) {
        this.status = status;
        this.updatedByEmployeeId = updatedByEmployeeId;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdatedByEmployeeId() {
        return updatedByEmployeeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedByEmployeeId(String updatedByEmployeeId) {
        this.updatedByEmployeeId = updatedByEmployeeId;
    }
}