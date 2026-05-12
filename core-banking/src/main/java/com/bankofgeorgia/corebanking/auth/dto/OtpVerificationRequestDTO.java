package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Holds the phone number and OTP code entered by the customer on the verify screen.
@Getter
@Setter
@NoArgsConstructor
public class OtpVerificationRequestDTO {
    private String phoneNumber; // must match the phone used during requestOtp
    private String otp;         // 6-digit code entered by the customer

    public OtpVerificationRequestDTO(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }

}
