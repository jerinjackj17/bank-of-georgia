package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SmsOtpRequest {
    private String phoneNumber;
    private String otpCode;

    public SmsOtpRequest(String phoneNumber, String otpCode) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
    }

}
