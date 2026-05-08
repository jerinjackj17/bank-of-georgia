package com.bankofgeorgia.corebanking.account.dto;

public class OpenAccountRequestDTO {

    private String customerId;         // MongoDB _id of the Customer
    private String productId;          // MongoDB _id of the Product
    private String openedByEmployeeId; // employeeId (e.g., EMP001)

    public OpenAccountRequestDTO() {
        // Required for JSON request mapping.
    }

    public OpenAccountRequestDTO(String customerId, String productId, String openedByEmployeeId) {
        this.customerId = customerId;
        this.productId = productId;
        this.openedByEmployeeId = openedByEmployeeId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public String getOpenedByEmployeeId() {
        return openedByEmployeeId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setOpenedByEmployeeId(String openedByEmployeeId) {
        this.openedByEmployeeId = openedByEmployeeId;
    }
}
