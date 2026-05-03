package com.bankofgeorgia.corebanking.product.repository;

import com.bankofgeorgia.corebanking.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductName(String productName);

    Optional<Product> findByProductType(String productType);

    boolean existsByProductName(String productName);

    boolean existsByProductType(String productType);
}