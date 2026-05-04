package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// Payload sent from core-banking to notification-service to trigger an SMS.
@Getter
@Setter
@NoArgsConstructor
public class SmsOtpRequest {
    private String phoneNumber; // recipient's phone number
    private String otpCode;     // the generated OTP code to include in the message

    public SmsOtpRequest(String phoneNumber, String otpCode) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
    }

}
