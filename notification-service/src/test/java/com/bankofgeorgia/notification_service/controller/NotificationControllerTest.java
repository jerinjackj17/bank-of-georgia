package com.bankofgeorgia.notification_service.controller;

import com.bankofgeorgia.notification_service.dto.OtpNotificationRequestDTO;
import com.bankofgeorgia.notification_service.service.SmsSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class NotificationControllerTest {

    private MockMvc mockMvc;

    private SmsSenderService smsSenderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Creates a fresh mocked service before each test.
        smsSenderService = mock(SmsSenderService.class);

        // Creates the controller with the mocked service.
        NotificationController notificationController = new NotificationController(smsSenderService);

        // Builds MockMvc without starting the full Spring Boot application.
        mockMvc = standaloneSetup(notificationController).build();

        // Converts Java objects into JSON request bodies.
        objectMapper = new ObjectMapper();
    }

    @Test
    void sendOtpNotification_ShouldReturnSuccessResponse_WhenRequestIsValid() throws Exception {
        // Builds a valid OTP notification request with a phone number and OTP code.
        OtpNotificationRequestDTO request = new OtpNotificationRequestDTO("9999999999", "123456");

        // Tells Mockito to do nothing when sendOtp is called (it returns void).
        doNothing().when(smsSenderService).sendOtp(anyString(), anyString());

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/notifications/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));

        // Verifies that the controller called the SMS service once.
        verify(smsSenderService).sendOtp("9999999999", "123456");
    }

    @Test
    void sendOtpNotification_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        OtpNotificationRequestDTO request = new OtpNotificationRequestDTO("9999999999", "123456");

        // Forces the mocked service to throw an exception.
        doThrow(new RuntimeException("SMS delivery failed")).when(smsSenderService).sendOtp(anyString(), anyString());

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/notifications/otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("SMS delivery failed"));

        // Verifies that the SMS service was called.
        verify(smsSenderService).sendOtp("9999999999", "123456");
    }
}
