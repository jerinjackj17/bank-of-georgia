package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequest);

}