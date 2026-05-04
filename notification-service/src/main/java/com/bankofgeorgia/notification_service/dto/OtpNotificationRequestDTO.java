package com.bankofgeorgia.notification_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Payload received by notification-service when core-banking requests an OTP SMS.
@Getter
@Setter
@NoArgsConstructor
public class OtpNotificationRequestDTO {
    private String phoneNumber; // recipient's phone number
    private String otpCode;     // the OTP code to include in the message text

    public OtpNotificationRequestDTO(String phoneNumber, String otpCode) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
    }
}
