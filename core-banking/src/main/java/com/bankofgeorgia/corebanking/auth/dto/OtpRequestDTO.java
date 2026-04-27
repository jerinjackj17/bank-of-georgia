package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDTO {
    
    private String phoneNumber;

    public OtpRequestDTO(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
