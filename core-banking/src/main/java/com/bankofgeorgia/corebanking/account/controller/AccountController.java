package com.bankofgeorgia.corebanking.account.controller;

import com.bankofgeorgia.corebanking.account.dto.AccountResponseDTO;
import com.bankofgeorgia.corebanking.account.dto.AccountStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.OpenAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.UpdateAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> openAccount(@RequestBody OpenAccountRequestDTO request) {
        log.info("Received request to open account for customerId: {}", request.getCustomerId());

        AccountResponseDTO response = accountService.openAccount(request);

        log.info("Account opened successfully with accountNumber: {}", response.getAccountNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        log.info("Received request to fetch all accounts");

        List<AccountResponseDTO> response = accountService.getAllAccounts();

        log.info("Fetched {} accounts", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByAccountNumber(@PathVariable String accountNumber) {
        log.info("Received request to fetch account with accountNumber: {}", accountNumber);

        AccountResponseDTO response = accountService.getAccountByAccountNumber(accountNumber);

        log.info("Account fetched successfully with accountNumber: {}", accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByCustomerId(@PathVariable String customerId) {
        log.info("Received request to fetch accounts for customerId: {}", customerId);

        List<AccountResponseDTO> response = accountService.getAccountsByCustomerId(customerId);

        log.info("Fetched {} accounts for customerId: {}", response.size(), customerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody UpdateAccountRequestDTO request) {

        log.info("Received update request for accountNumber: {}", accountNumber);

        AccountResponseDTO response = accountService.updateAccount(accountNumber, request);

        log.info("Account updated successfully with accountNumber: {}", accountNumber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(
            @PathVariable String accountNumber,
            @RequestBody AccountStatusUpdateRequestDTO request) {

        log.info("Received status update request for accountNumber: {}", accountNumber);

        AccountResponseDTO response = accountService.updateAccountStatus(accountNumber, request);

        log.info("Account status updated successfully for accountNumber: {}", accountNumber);
        return ResponseEntity.ok(response);
    }
}