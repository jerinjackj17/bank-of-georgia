package com.bankofgeorgia.corebanking.transaction.controller;

import com.bankofgeorgia.corebanking.common.exception.BadRequestException;
import com.bankofgeorgia.corebanking.common.exception.GlobalExceptionHandler;
import com.bankofgeorgia.corebanking.common.exception.ResourceNotFoundException;
import com.bankofgeorgia.corebanking.transaction.dto.DepositRequestDTO;
import com.bankofgeorgia.corebanking.transaction.dto.TransactionResponseDTO;
import com.bankofgeorgia.corebanking.transaction.dto.WithdrawRequestDTO;
import com.bankofgeorgia.corebanking.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TransactionControllerTest {

    private MockMvc mockMvc;
    private TransactionService transactionService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);

        TransactionController transactionController =
                new TransactionController(transactionService);

        mockMvc = standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void deposit_ShouldReturnCreatedTransaction_WhenRequestIsValid() throws Exception {

        DepositRequestDTO request = new DepositRequestDTO(
                "ACC000001",
                new BigDecimal("250.00"),
                "Initial cash deposit"
        );

        TransactionResponseDTO response = buildTransactionResponse(
                "txn-id-001",
                "TXN000001",
                "DEPOSIT",
                new BigDecimal("250.00"),
                "Initial cash deposit",
                new BigDecimal("250.00")
        );

        when(transactionService.deposit(any(DepositRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN000001"))
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.balanceAfter").value(250.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deposit_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {

        DepositRequestDTO request = new DepositRequestDTO(
                "ACC999999",
                new BigDecimal("250.00"),
                "Cash deposit"
        );

        when(transactionService.deposit(any(DepositRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void withdraw_ShouldReturnCreatedTransaction_WhenRequestIsValid() throws Exception {

        WithdrawRequestDTO request = new WithdrawRequestDTO(
                "ACC000001",
                new BigDecimal("75.00"),
                "ATM withdrawal"
        );

        TransactionResponseDTO response = buildTransactionResponse(
                "txn-id-002",
                "TXN000002",
                "WITHDRAWAL",
                new BigDecimal("75.00"),
                "ATM withdrawal",
                new BigDecimal("175.00")
        );

        when(transactionService.withdraw(any(WithdrawRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN000002"))
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(75.00))
                .andExpect(jsonPath("$.balanceAfter").value(175.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void withdraw_ShouldReturnBadRequest_WhenBalanceIsInsufficient() throws Exception {

        WithdrawRequestDTO request = new WithdrawRequestDTO(
                "ACC000001",
                new BigDecimal("500.00"),
                "ATM withdrawal"
        );

        when(transactionService.withdraw(any(WithdrawRequestDTO.class)))
                .thenThrow(new BadRequestException("Insufficient balance"));

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionList_WhenTransactionsExist() throws Exception {

        TransactionResponseDTO response = buildTransactionResponse(
                "txn-id-001",
                "TXN000001",
                "DEPOSIT",
                new BigDecimal("250.00"),
                "Initial cash deposit",
                new BigDecimal("250.00")
        );

        when(transactionService.getAllTransactions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transactionId").value("TXN000001"))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
    }

    @Test
    void getTransactionsByAccountNumber_ShouldReturnTransactionHistory_WhenAccountExists() throws Exception {

        TransactionResponseDTO first = buildTransactionResponse(
                "txn-id-002",
                "TXN000002",
                "WITHDRAWAL",
                new BigDecimal("75.00"),
                "ATM withdrawal",
                new BigDecimal("175.00")
        );

        TransactionResponseDTO second = buildTransactionResponse(
                "txn-id-001",
                "TXN000001",
                "DEPOSIT",
                new BigDecimal("250.00"),
                "Initial cash deposit",
                new BigDecimal("250.00")
        );

        when(transactionService.getTransactionsByAccountNumber("ACC000001"))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/transactions/account/ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].transactionId").value("TXN000002"))
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1].transactionId").value("TXN000001"))
                .andExpect(jsonPath("$[1].type").value("DEPOSIT"));
    }

    @Test
    void getTransactionsByAccountNumber_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {

        when(transactionService.getTransactionsByAccountNumber("ACC999999"))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/api/transactions/account/ACC999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    private TransactionResponseDTO buildTransactionResponse(String id,
                                                            String transactionId,
                                                            String type,
                                                            BigDecimal amount,
                                                            String description,
                                                            BigDecimal balanceAfter) {

        return new TransactionResponseDTO(
                id,
                transactionId,
                "acc-id-001",
                "ACC000001",
                "cust001",
                type,
                amount,
                description,
                balanceAfter,
                "COMPLETED",
                "2026-05-17T18:00:00Z"
        );
    }
}