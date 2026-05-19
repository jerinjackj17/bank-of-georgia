package com.bankofgeorgia.corebanking.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// Returned to the client after a login or OTP verification attempt.
@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {
    private String message;  // human-readable result (e.g. "Login successful")
    private String loginId;  // username, email, or phone number used to log in
    private String verified; // "true" if login succeeded, "false" otherwise
    private String token;    // signed JWT issued on success, null on failure

    // Used for failed responses where no token is issued.
    public LoginResponseDTO(String message, String loginId, String verified) {
        this.message = message;
        this.loginId = loginId;
        this.verified = verified;
    }

    // Used for successful responses that include a JWT.
    public LoginResponseDTO(String message, String loginId, String verified, String token) {
        this.message = message;
        this.loginId = loginId;
        this.verified = verified;
        this.token = token;
    }
}
