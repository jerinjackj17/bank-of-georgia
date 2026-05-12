package com.bankofgeorgia.corebanking.account.dto;

public class AccountStatusUpdateRequestDTO {

    private String status;             // ACTIVE, FROZEN, CLOSED
    private String updatedByEmployeeId;

    public AccountStatusUpdateRequestDTO() {
        // Required for JSON request mapping.
    }

    public AccountStatusUpdateRequestDTO(String status, String updatedByEmployeeId) {
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
