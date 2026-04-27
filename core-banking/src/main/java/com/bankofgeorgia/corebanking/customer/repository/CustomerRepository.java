package com.bankofgeorgia.corebanking.customer.repository;

import com.bankofgeorgia.corebanking.customer.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);
}