package com.bankofgeorgia.notification_service.dto;

// Returned by notification-service after attempting to send an OTP SMS.
public class NotificationResponseDTO {
    private String status;  // "success" or an error indicator
    private String message; // human-readable description of the result

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
