package com.bankofgeorgia.corebanking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtpResponseDTO {
    private String message;
    private String phoneNumber;
    private String otpRequired;
    private String otpExpiresInSeconds;

    public OtpResponseDTO(String message, String phoneNumber, String otpRequired, String otpExpiresInSeconds) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.otpRequired = otpRequired;
        this.otpExpiresInSeconds = otpExpiresInSeconds;
    }
}
