package com.bankofgeorgia.corebanking.transaction.controller;

import com.bankofgeorgia.corebanking.transaction.dto.DepositRequestDTO;
import com.bankofgeorgia.corebanking.transaction.dto.TransactionResponseDTO;
import com.bankofgeorgia.corebanking.transaction.dto.WithdrawRequestDTO;
import com.bankofgeorgia.corebanking.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@RequestBody DepositRequestDTO request) {
        log.info("Received deposit request for accountNumber: {}", request.getAccountNumber());

        TransactionResponseDTO response = transactionService.deposit(request);

        log.info("Deposit request completed successfully with transactionId: {}", response.getTransactionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@RequestBody WithdrawRequestDTO request) {
        log.info("Received withdrawal request for accountNumber: {}", request.getAccountNumber());

        TransactionResponseDTO response = transactionService.withdraw(request);

        log.info("Withdrawal request completed successfully with transactionId: {}", response.getTransactionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        log.info("Received request to fetch all transactions");

        List<TransactionResponseDTO> response = transactionService.getAllTransactions();

        log.info("Fetched {} transactions", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccountNumber(
            @PathVariable String accountNumber) {

        log.info("Received request to fetch transaction history for accountNumber: {}", accountNumber);

        List<TransactionResponseDTO> response =
                transactionService.getTransactionsByAccountNumber(accountNumber);

        log.info("Fetched {} transactions for accountNumber: {}", response.size(), accountNumber);
        return ResponseEntity.ok(response);
    }
}