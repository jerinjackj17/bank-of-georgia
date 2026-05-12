package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;

// Defines the contract for OTP generation and verification.
public interface OtpService {

    // Generates an OTP, sends it via SMS, and stores it in Redis.
    OtpResponseDTO requestOtp(OtpRequestDTO otpRequest);

    // Checks the submitted OTP against the value stored in Redis.
    LoginResponseDTO verifyOtp(OtpVerificationRequestDTO otpVerificationRequest);
}
