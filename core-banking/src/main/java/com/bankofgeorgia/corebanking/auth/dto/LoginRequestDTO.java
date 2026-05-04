package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;

// Holds the credentials sent by the customer on the login screen.
@Getter
@Setter
public class LoginRequestDTO {
    private String loginType; // "username" or "email"
    private String loginId;   // the actual username or email value
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String loginType, String loginId, String password) {
        this.loginType = loginType;
        this.loginId = loginId;
        this.password = password;
    }

}
