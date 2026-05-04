package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;

// Defines the contract for password-based customer authentication.
public interface AuthService {

    // Validates credentials and returns a login result.
    LoginResponseDTO login(LoginRequestDTO loginRequest);

}
