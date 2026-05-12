package com.bankofgeorgia.corebanking.account.service;

import com.bankofgeorgia.corebanking.account.dto.AccountResponseDTO;
import com.bankofgeorgia.corebanking.account.dto.AccountStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.OpenAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.UpdateAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.entity.Account;
import com.bankofgeorgia.corebanking.account.repository.AccountRepository;
import com.bankofgeorgia.corebanking.customer.repository.CustomerRepository;
import com.bankofgeorgia.corebanking.product.entity.Product;
import com.bankofgeorgia.corebanking.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_FROZEN = "FROZEN";
    private static final String STATUS_CLOSED = "CLOSED";

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public AccountResponseDTO openAccount(OpenAccountRequestDTO request) {

        log.info("Opening account for customerId: {} with productId: {}", request.getCustomerId(), request.getProductId());

        // validate customer exists
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // validate product exists and is ACTIVE
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!STATUS_ACTIVE.equals(product.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not active and cannot be used to open an account");
        }

        Instant now = Instant.now();

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomerId(request.getCustomerId());
        account.setProductId(request.getProductId());
        account.setProductType(product.getProductType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(STATUS_ACTIVE);
        account.setOpenedByEmployeeId(request.getOpenedByEmployeeId());
        account.setUpdatedByEmployeeId(request.getOpenedByEmployeeId());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        Account saved = accountRepository.save(account);

        log.info("Account opened successfully with accountNumber: {}", saved.getAccountNumber());

        return toResponse(saved);
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {

        log.info("Fetching all accounts");

        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountResponseDTO getAccountByAccountNumber(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        log.info("Fetched account with accountNumber: {}", accountNumber);

        return toResponse(account);
    }

    @Override
    public List<AccountResponseDTO> getAccountsByCustomerId(String customerId) {

        // validate customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        log.info("Fetching accounts for customerId: {}", customerId);

        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AccountResponseDTO updateAccount(String accountNumber, UpdateAccountRequestDTO request) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        log.info("Updating account with accountNumber: {}", accountNumber);

        // migrate to a different product if provided
        if (request.getProductId() != null && !request.getProductId().isBlank()) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            if (!STATUS_ACTIVE.equals(product.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not active");
            }

            account.setProductId(product.getId());
            account.setProductType(product.getProductType());
        }

        if (request.getUpdatedByEmployeeId() != null) {
            account.setUpdatedByEmployeeId(request.getUpdatedByEmployeeId());
        }

        account.setUpdatedAt(Instant.now());

        Account updated = accountRepository.save(account);

        log.info("Account updated successfully with accountNumber: {}", accountNumber);

        return toResponse(updated);
    }

    @Override
    public AccountResponseDTO updateAccountStatus(String accountNumber, AccountStatusUpdateRequestDTO request) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        String status = normalizeStatus(request.getStatus());

        log.info("Updating account status for accountNumber: {} to {}", accountNumber, status);

        account.setStatus(status);

        if (request.getUpdatedByEmployeeId() != null) {
            account.setUpdatedByEmployeeId(request.getUpdatedByEmployeeId());
        }

        account.setUpdatedAt(Instant.now());

        Account updated = accountRepository.save(account);

        return toResponse(updated);
    }

    private String generateAccountNumber() {
        long count = accountRepository.count();
        return String.format("ACC%06d", count + 1);
    }

    private String normalizeStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account status is required");
        }

        String normalized = status.trim().toUpperCase();

        if (!STATUS_ACTIVE.equals(normalized) && !STATUS_FROZEN.equals(normalized) && !STATUS_CLOSED.equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account status. Must be ACTIVE, FROZEN, or CLOSED");
        }

        return normalized;
    }

    private AccountResponseDTO toResponse(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getProductId(),
                account.getProductType(),
                account.getBalance(),
                account.getStatus(),
                account.getOpenedByEmployeeId(),
                account.getUpdatedByEmployeeId(),
                account.getCreatedAt() == null ? null : account.getCreatedAt().toString(),
                account.getUpdatedAt() == null ? null : account.getUpdatedAt().toString());
    }
}
