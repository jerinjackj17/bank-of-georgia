package com.bankofgeorgia.corebanking.account.controller;

import com.bankofgeorgia.corebanking.account.dto.AccountResponseDTO;
import com.bankofgeorgia.corebanking.account.dto.AccountStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.OpenAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.UpdateAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.service.AccountService;
import com.bankofgeorgia.corebanking.common.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AccountControllerTest {

    private MockMvc mockMvc;

    private AccountService accountService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Creates a fresh mocked service before each test.
        accountService = mock(AccountService.class);

        // Creates the controller with the mocked service.
        AccountController accountController = new AccountController(accountService);

        // Registers global exception handling for standalone MockMvc tests.
        mockMvc = standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Converts Java objects into JSON request bodies.
        objectMapper = new ObjectMapper();
    }

    @Test
    void openAccount_ShouldReturnCreatedAccount_WhenRequestIsValid() throws Exception {
        // Builds a valid account opening request.
        OpenAccountRequestDTO request = new OpenAccountRequestDTO("cust001", "prod001", "EMP001");

        // Builds the response the mocked service should return.
        AccountResponseDTO response = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod001",
                "SAVINGS_ACCOUNT",
                BigDecimal.ZERO,
                "ACTIVE"
        );

        // Tells Mockito what to return when openAccount is called.
        when(accountService.openAccount(any(OpenAccountRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status 201 plus returned JSON fields.
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.customerId").value("cust001"))
                .andExpect(jsonPath("$.productId").value("prod001"))
                .andExpect(jsonPath("$.productType").value("SAVINGS_ACCOUNT"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called the service once.
        verify(accountService).openAccount(any(OpenAccountRequestDTO.class));
    }

    @Test
    void openAccount_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        OpenAccountRequestDTO request = new OpenAccountRequestDTO("cust-missing", "prod001", "EMP001");

        // Forces the mocked service to throw an exception.
        when(accountService.openAccount(any(OpenAccountRequestDTO.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        // Global exception handler returns structured error JSON.
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"));

        // Verifies that the service method was called.
        verify(accountService).openAccount(any(OpenAccountRequestDTO.class));
    }

    @Test
    void getAllAccounts_ShouldReturnAccountList_WhenAccountsExist() throws Exception {
        // Builds one fake account returned by the mocked service.
        AccountResponseDTO account = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod001",
                "SAVINGS_ACCOUNT",
                BigDecimal.ZERO,
                "ACTIVE"
        );

        // Tells Mockito to return a list with one account.
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        // Sends GET request and checks first account in the JSON array.
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$[0].customerId").value("cust001"))
                .andExpect(jsonPath("$[0].productType").value("SAVINGS_ACCOUNT"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Verifies that the controller asked the service for all accounts.
        verify(accountService).getAllAccounts();
    }

    @Test
    void getAllAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() throws Exception {
        // Tells Mockito to return an empty account list.
        when(accountService.getAllAccounts()).thenReturn(List.of());

        // Sends GET request and checks that the JSON array is empty.
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verifies that the service method was called.
        verify(accountService).getAllAccounts();
    }

    @Test
    void getAccountByAccountNumber_ShouldReturnAccount_WhenAccountExists() throws Exception {
        // Builds a fake account for the requested account number.
        AccountResponseDTO account = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod001",
                "SAVINGS_ACCOUNT",
                BigDecimal.ZERO,
                "ACTIVE"
        );

        // Tells Mockito to return the account when ACC000001 is requested.
        when(accountService.getAccountByAccountNumber("ACC000001")).thenReturn(account);

        // Sends GET request by account number and checks returned fields.
        mockMvc.perform(get("/api/accounts/ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.customerId").value("cust001"))
                .andExpect(jsonPath("$.productType").value("SAVINGS_ACCOUNT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called the service with the correct account number.
        verify(accountService).getAccountByAccountNumber("ACC000001");
    }

    @Test
    void getAccountByAccountNumber_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        // Forces the mocked service to fail for a missing account.
        when(accountService.getAccountByAccountNumber("ACC999999"))
                .thenThrow(new RuntimeException("Account not found"));

        // Global exception handler returns structured error JSON.
        mockMvc.perform(get("/api/accounts/ACC999999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"));

        // Verifies that the service was called with the requested account number.
        verify(accountService).getAccountByAccountNumber("ACC999999");
    }

    @Test
    void getAccountsByCustomerId_ShouldReturnAccountList_WhenCustomerHasAccounts() throws Exception {
        // Builds a fake account belonging to the customer.
        AccountResponseDTO account = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod001",
                "SAVINGS_ACCOUNT",
                BigDecimal.ZERO,
                "ACTIVE"
        );

        // Tells Mockito to return the account list for cust001.
        when(accountService.getAccountsByCustomerId("cust001")).thenReturn(List.of(account));

        // Sends GET request for a customer's accounts and checks the result.
        mockMvc.perform(get("/api/accounts/customer/cust001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$[0].customerId").value("cust001"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Verifies that the service was called with the correct customer ID.
        verify(accountService).getAccountsByCustomerId("cust001");
    }

    @Test
    void getAccountsByCustomerId_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        // Forces the mocked service to fail for a missing customer.
        when(accountService.getAccountsByCustomerId("cust-missing"))
                .thenThrow(new RuntimeException("Customer not found"));

        // Global exception handler returns structured error JSON.
        mockMvc.perform(get("/api/accounts/customer/cust-missing"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"));

        // Verifies that the service was called with the requested customer ID.
        verify(accountService).getAccountsByCustomerId("cust-missing");
    }

    @Test
    void updateAccount_ShouldReturnUpdatedAccount_WhenRequestIsValid() throws Exception {
        // Builds an update request to migrate the account to a different product.
        UpdateAccountRequestDTO request = new UpdateAccountRequestDTO("prod002", "EMP001");

        // Builds the updated response returned by the service.
        AccountResponseDTO response = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod002",
                "CHECKING_ACCOUNT",
                BigDecimal.ZERO,
                "ACTIVE"
        );

        // Tells Mockito to return the updated account.
        when(accountService.updateAccount(eq("ACC000001"), any(UpdateAccountRequestDTO.class))).thenReturn(response);

        // Sends PUT request and checks updated fields in the response.
        mockMvc.perform(put("/api/accounts/ACC000001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.productId").value("prod002"))
                .andExpect(jsonPath("$.productType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that updateAccount was called with the correct account number and request type.
        verify(accountService).updateAccount(eq("ACC000001"), any(UpdateAccountRequestDTO.class));
    }

    @Test
    void updateAccount_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        // Builds an update request for an account that will fail in service.
        UpdateAccountRequestDTO request = new UpdateAccountRequestDTO("prod-missing", "EMP001");

        // Forces the mocked service to throw an exception during update.
        when(accountService.updateAccount(eq("ACC999999"), any(UpdateAccountRequestDTO.class)))
                .thenThrow(new RuntimeException("Account not found"));

        // Global exception handler returns structured error JSON.
        mockMvc.perform(put("/api/accounts/ACC999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"));

        // Verifies that the update service method was called.
        verify(accountService).updateAccount(eq("ACC999999"), any(UpdateAccountRequestDTO.class));
    }

    @Test
    void updateAccountStatus_ShouldReturnUpdatedAccount_WhenStatusIsValid() throws Exception {
        // Builds a request to freeze the account.
        AccountStatusUpdateRequestDTO request = new AccountStatusUpdateRequestDTO("FROZEN", "EMP001");

        // Builds the response after status change.
        AccountResponseDTO response = buildAccountResponse(
                "acc-id-001",
                "ACC000001",
                "cust001",
                "prod001",
                "SAVINGS_ACCOUNT",
                BigDecimal.ZERO,
                "FROZEN"
        );

        // Tells Mockito to return the updated account.
        when(accountService.updateAccountStatus(eq("ACC000001"), any(AccountStatusUpdateRequestDTO.class)))
                .thenReturn(response);

        // Sends PUT request and checks the changed status.
        mockMvc.perform(put("/api/accounts/ACC000001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.status").value("FROZEN"));

        // Verifies that status update service method was called.
        verify(accountService).updateAccountStatus(eq("ACC000001"), any(AccountStatusUpdateRequestDTO.class));
    }

    @Test
    void updateAccountStatus_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        // Builds a request with a status value.
        AccountStatusUpdateRequestDTO request = new AccountStatusUpdateRequestDTO("CLOSED", "EMP001");

        // Forces the mocked service to fail during status update.
        when(accountService.updateAccountStatus(eq("ACC999999"), any(AccountStatusUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Account not found"));

        // Global exception handler returns structured error JSON.
        mockMvc.perform(put("/api/accounts/ACC999999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"));

        // Verifies that status update service method was called.
        verify(accountService).updateAccountStatus(eq("ACC999999"), any(AccountStatusUpdateRequestDTO.class));
    }

    private AccountResponseDTO buildAccountResponse(
            String id,
            String accountNumber,
            String customerId,
            String productId,
            String productType,
            BigDecimal balance,
            String status
    ) {
        // Creates reusable account response test data.
        return new AccountResponseDTO(
                id,
                accountNumber,
                customerId,
                productId,
                productType,
                balance,
                status,
                "EMP001",
                "EMP001",
                "2026-05-08T00:00:00Z",
                "2026-05-08T00:00:00Z"
        );
    }
}