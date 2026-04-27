package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {
    private String message;
    private String loginId;
    private String verified;

    public LoginResponseDTO(String message, String loginId, String verified) {
        this.message = message;
        this.loginId = loginId;
        this.verified = verified;
    }
}
