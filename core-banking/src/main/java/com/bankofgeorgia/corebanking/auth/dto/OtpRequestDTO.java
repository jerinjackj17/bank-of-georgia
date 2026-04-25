package com.bankofgeorgia.corebanking.dto;

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
