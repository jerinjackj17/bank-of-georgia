package com.bankofgeorgia.corebanking.account.service;

import com.bankofgeorgia.corebanking.account.dto.AccountResponseDTO;
import com.bankofgeorgia.corebanking.account.dto.AccountStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.OpenAccountRequestDTO;
import com.bankofgeorgia.corebanking.account.dto.UpdateAccountRequestDTO;

import java.util.List;

public interface AccountService {

    AccountResponseDTO openAccount(OpenAccountRequestDTO request);

    List<AccountResponseDTO> getAllAccounts();

    AccountResponseDTO getAccountByAccountNumber(String accountNumber);

    List<AccountResponseDTO> getAccountsByCustomerId(String customerId);

    AccountResponseDTO updateAccount(String accountNumber, UpdateAccountRequestDTO request);

    AccountResponseDTO updateAccountStatus(String accountNumber, AccountStatusUpdateRequestDTO request);
}
