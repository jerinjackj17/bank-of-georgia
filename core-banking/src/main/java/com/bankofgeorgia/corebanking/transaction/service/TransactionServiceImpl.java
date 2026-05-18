package com.bankofgeorgia.corebanking.transaction.service;

import com.bankofgeorgia.corebanking.account.entity.Account;
import com.bankofgeorgia.corebanking.account.repository.AccountRepository;
import com.bankofgeorgia.corebanking.common.exception.BadRequestException;
import com.bankofgeorgia.corebanking.common.exception.ResourceNotFoundException;
import com.bankofgeorgia.corebanking.transaction.dto.DepositRequestDTO;
import com.bankofgeorgia.corebanking.transaction.dto.TransactionResponseDTO;
import com.bankofgeorgia.corebanking.transaction.dto.WithdrawRequestDTO;
import com.bankofgeorgia.corebanking.transaction.entity.Transaction;
import com.bankofgeorgia.corebanking.transaction.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final String ACCOUNT_STATUS_ACTIVE = "ACTIVE";

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAWAL = "WITHDRAWAL";

    private static final String TRANSACTION_STATUS_COMPLETED = "COMPLETED";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResponseDTO deposit(DepositRequestDTO request) {

        validateAccountNumber(request.getAccountNumber());
        validateAmount(request.getAmount());

        log.info("Processing deposit for accountNumber: {}", request.getAccountNumber());

        Account account = getActiveAccount(request.getAccountNumber());

        BigDecimal updatedBalance = account.getBalance().add(request.getAmount());

        account.setBalance(updatedBalance);
        account.setUpdatedAt(Instant.now());

        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = buildTransaction(
                updatedAccount,
                TRANSACTION_TYPE_DEPOSIT,
                request.getAmount(),
                cleanDescription(request.getDescription(), "Cash deposit")
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Deposit completed successfully. transactionId: {}, accountNumber: {}",
                savedTransaction.getTransactionId(),
                savedTransaction.getAccountNumber());

        return mapToResponse(savedTransaction);
    }

    @Override
    public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {

        validateAccountNumber(request.getAccountNumber());
        validateAmount(request.getAmount());

        log.info("Processing withdrawal for accountNumber: {}", request.getAccountNumber());

        Account account = getActiveAccount(request.getAccountNumber());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Withdrawal failed due to insufficient balance for accountNumber: {}", request.getAccountNumber());
            throw new BadRequestException("Insufficient balance");
        }

        BigDecimal updatedBalance = account.getBalance().subtract(request.getAmount());

        account.setBalance(updatedBalance);
        account.setUpdatedAt(Instant.now());

        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = buildTransaction(
                updatedAccount,
                TRANSACTION_TYPE_WITHDRAWAL,
                request.getAmount(),
                cleanDescription(request.getDescription(), "Cash withdrawal")
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Withdrawal completed successfully. transactionId: {}, accountNumber: {}",
                savedTransaction.getTransactionId(),
                savedTransaction.getAccountNumber());

        return mapToResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {

        log.info("Fetching all transactions");

        return transactionRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByAccountNumber(String accountNumber) {

        validateAccountNumber(accountNumber);

        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new ResourceNotFoundException("Account not found");
        }

        log.info("Fetching transaction history for accountNumber: {}", accountNumber);

        return transactionRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Account getActiveAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!ACCOUNT_STATUS_ACTIVE.equals(account.getStatus())) {
            throw new BadRequestException("Transactions are allowed only for active accounts");
        }

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        return account;
    }

    private void validateAccountNumber(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new BadRequestException("Account number is required");
        }
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null) {
            throw new BadRequestException("Transaction amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be greater than zero");
        }
    }

    private Transaction buildTransaction(Account account,
                                         String type,
                                         BigDecimal amount,
                                         String description) {

        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setAccountId(account.getId());
        transaction.setAccountNumber(account.getAccountNumber());
        transaction.setCustomerId(account.getCustomerId());
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setStatus(TRANSACTION_STATUS_COMPLETED);
        transaction.setCreatedAt(Instant.now());

        return transaction;
    }

    private String generateTransactionId() {
        long count = transactionRepository.count();
        return String.format("TXN%06d", count + 1);
    }

    private String cleanDescription(String description, String defaultDescription) {

        if (description == null || description.isBlank()) {
            return defaultDescription;
        }

        return description.trim();
    }

    private TransactionResponseDTO mapToResponse(Transaction transaction) {

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getAccountNumber(),
                transaction.getCustomerId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getBalanceAfter(),
                transaction.getStatus(),
                transaction.getCreatedAt() == null ? null : transaction.getCreatedAt().toString()
        );
    }
}