package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.EmployeeLoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;

// Defines the contract for username-based employee authentication.
public interface EmployeeAuthService {

    // Validates an employee's username and password and returns a login result.
    LoginResponseDTO loginByUsername(EmployeeLoginRequestDTO request);
}
