package com.bankofgeorgia.notification_service.dto;

public class NotificationResponseDTO {
    private String status;
    private String message;

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
