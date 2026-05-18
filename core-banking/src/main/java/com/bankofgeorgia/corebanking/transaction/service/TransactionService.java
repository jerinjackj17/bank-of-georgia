package com.bankofgeorgia.corebanking.transaction.service;

import com.bankofgeorgia.corebanking.transaction.dto.DepositRequestDTO;
import com.bankofgeorgia.corebanking.transaction.dto.TransactionResponseDTO;
import com.bankofgeorgia.corebanking.transaction.dto.WithdrawRequestDTO;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO deposit(DepositRequestDTO request);

    TransactionResponseDTO withdraw(WithdrawRequestDTO request);

    List<TransactionResponseDTO> getAllTransactions();

    List<TransactionResponseDTO> getTransactionsByAccountNumber(String accountNumber);
}