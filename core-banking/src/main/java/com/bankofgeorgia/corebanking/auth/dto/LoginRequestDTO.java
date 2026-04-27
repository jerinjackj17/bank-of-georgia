package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String loginType;
    private String loginId;
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String loginType, String loginId, String password) {
        this.loginType = loginType;
        this.loginId = loginId;
        this.password = password;
    }
    
}
