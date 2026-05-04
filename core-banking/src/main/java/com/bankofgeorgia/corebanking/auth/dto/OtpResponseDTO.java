package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Returned to the client after an OTP has been sent to their phone.
@Getter
@Setter
@NoArgsConstructor
public class OtpResponseDTO {
    private String message;             // human-readable result (e.g. "OTP requested successfully")
    private String phoneNumber;         // phone number the OTP was sent to
    private String otpRequired;         // "true" if the OTP step is required
    private String otpExpiresInSeconds; // how long the OTP is valid (currently "300" = 5 minutes)

    public OtpResponseDTO(String message, String phoneNumber, String otpRequired, String otpExpiresInSeconds) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.otpRequired = otpRequired;
        this.otpExpiresInSeconds = otpExpiresInSeconds;
    }
}
