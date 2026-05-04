package com.bankofgeorgia.corebanking.auth.controller;

import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;
import com.bankofgeorgia.corebanking.auth.service.AuthService;
import com.bankofgeorgia.corebanking.auth.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AuthControllerTest {

    private MockMvc mockMvc;

    private AuthService authService;

    private OtpService otpService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Creates fresh mocked services before each test.
        authService = mock(AuthService.class);
        otpService = mock(OtpService.class);

        // Creates the controller with the mocked services.
        AuthController authController = new AuthController(authService, otpService);

        // Builds MockMvc without starting the full Spring Boot application.
        mockMvc = standaloneSetup(authController).build();

        // Converts Java objects into JSON request bodies.
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() throws Exception {
        // Builds a valid login request with username and password.
        LoginRequestDTO request = new LoginRequestDTO("username", "john1", "password123");

        // Builds the response the mocked service should return.
        LoginResponseDTO response = new LoginResponseDTO("Login successful", "john1", "true");

        // Tells Mockito what to return when login is called.
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/auth/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.loginId").value("john1"))
                .andExpect(jsonPath("$.verified").value("true"));

        // Verifies that the controller called the auth service once.
        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        LoginRequestDTO request = new LoginRequestDTO("username", "john1", "wrongpassword");

        // Forces the mocked service to throw an exception.
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid password for user: john1"));

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/auth/customer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Invalid password for user: john1"));

        // Verifies that the auth service was called.
        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    void requestOtp_ShouldReturnOtpResponse_WhenPhoneNumberIsValid() throws Exception {
        // Builds a valid OTP request with a registered phone number.
        OtpRequestDTO request = new OtpRequestDTO("9999999999");

        // Builds the response the mocked service should return.
        OtpResponseDTO response = new OtpResponseDTO("OTP requested successfully", "9999999999", "true", "300");

        // Tells Mockito what to return when requestOtp is called.
        when(otpService.requestOtp(any(OtpRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/auth/customer/login/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP requested successfully"))
                .andExpect(jsonPath("$.phoneNumber").value("9999999999"))
                .andExpect(jsonPath("$.otpRequired").value("true"))
                .andExpect(jsonPath("$.otpExpiresInSeconds").value("300"));

        // Verifies that the controller called the OTP service once.
        verify(otpService).requestOtp(any(OtpRequestDTO.class));
    }

    @Test
    void requestOtp_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        OtpRequestDTO request = new OtpRequestDTO("0000000000");

        // Forces the mocked service to throw an exception.
        when(otpService.requestOtp(any(OtpRequestDTO.class)))
                .thenThrow(new RuntimeException("Phone number not registered"));

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/auth/customer/login/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Phone number not registered"));

        // Verifies that the OTP service was called.
        verify(otpService).requestOtp(any(OtpRequestDTO.class));
    }

    @Test
    void verifyOtp_ShouldReturnLoginResponse_WhenOtpIsValid() throws Exception {
        // Builds a valid OTP verification request with a correct code.
        OtpVerificationRequestDTO request = new OtpVerificationRequestDTO("9999999999", "123456");

        // Builds the response the mocked service should return.
        LoginResponseDTO response = new LoginResponseDTO("OTP verified successfully", "9999999999", "true");

        // Tells Mockito what to return when verifyOtp is called.
        when(otpService.verifyOtp(any(OtpVerificationRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/auth/customer/login/verifyOtp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"))
                .andExpect(jsonPath("$.loginId").value("9999999999"))
                .andExpect(jsonPath("$.verified").value("true"));

        // Verifies that the controller called the OTP service once.
        verify(otpService).verifyOtp(any(OtpVerificationRequestDTO.class));
    }

    @Test
    void verifyOtp_ShouldReturnServerError_WhenOtpIsInvalid() throws Exception {
        // Builds a request with an incorrect or expired OTP code.
        OtpVerificationRequestDTO request = new OtpVerificationRequestDTO("9999999999", "000000");

        // Forces the mocked service to throw an exception.
        when(otpService.verifyOtp(any(OtpVerificationRequestDTO.class)))
                .thenThrow(new RuntimeException("OTP verification failed"));

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/auth/customer/login/verifyOtp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("OTP verification failed"));

        // Verifies that the OTP service was called.
        verify(otpService).verifyOtp(any(OtpVerificationRequestDTO.class));
    }
}
