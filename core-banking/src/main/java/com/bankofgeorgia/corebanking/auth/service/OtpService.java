package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;

public interface OtpService {
    
    OtpResponseDTO requestOtp(OtpRequestDTO otpRequest);

    LoginResponseDTO verifyOtp(OtpVerificationRequestDTO otpVerificationRequest);
}
