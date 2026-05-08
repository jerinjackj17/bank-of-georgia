package com.bankofgeorgia.corebanking.account.repository;

import com.bankofgeorgia.corebanking.account.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(String customerId);

    boolean existsByAccountNumber(String accountNumber);
}
