package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;

// Holds the phone number sent when a customer requests an OTP login code.
@Getter
@Setter
public class OtpRequestDTO {

    private String phoneNumber;

    public OtpRequestDTO(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
