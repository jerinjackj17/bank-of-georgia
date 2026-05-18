package com.bankofgeorgia.corebanking.transaction.repository;

import com.bankofgeorgia.corebanking.transaction.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
}