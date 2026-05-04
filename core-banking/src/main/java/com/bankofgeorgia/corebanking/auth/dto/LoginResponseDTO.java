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

    public LoginResponseDTO(String message, String loginId, String verified) {
        this.message = message;
        this.loginId = loginId;
        this.verified = verified;
    }
}
