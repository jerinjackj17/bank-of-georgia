package com.bankofgeorgia.corebanking.account.dto;

public class UpdateAccountRequestDTO {

    private String productId;          // optional — migrate account to a different product
    private String updatedByEmployeeId;

    public UpdateAccountRequestDTO() {
        // Required for JSON request mapping.
    }

    public UpdateAccountRequestDTO(String productId, String updatedByEmployeeId) {
        this.productId = productId;
        this.updatedByEmployeeId = updatedByEmployeeId;
    }

    public String getProductId() {
        return productId;
    }

    public String getUpdatedByEmployeeId() {
        return updatedByEmployeeId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setUpdatedByEmployeeId(String updatedByEmployeeId) {
        this.updatedByEmployeeId = updatedByEmployeeId;
    }
}
