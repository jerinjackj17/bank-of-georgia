package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtpVerificationRequestDTO {
    private String phoneNumber;
    private String otp;

    public OtpVerificationRequestDTO(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }
    
}
