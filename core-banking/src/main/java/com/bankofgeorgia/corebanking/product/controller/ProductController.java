package com.bankofgeorgia.corebanking.product.controller;

import com.bankofgeorgia.corebanking.product.dto.ProductRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductResponseDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.UpdateProductRequestDTO;
import com.bankofgeorgia.corebanking.product.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO request) {
        // Product creation is handled by employee/admin users.
        logger.info("Received product creation request for product name: {}", request.getProductName());

        try {
            ProductResponseDTO response = productService.createProduct(request);

            logger.info("Product created successfully with id: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            logger.error("Product creation failed for product name: {}", request.getProductName(), ex);

            return ResponseEntity
                    .status(resolveStatus(ex.getMessage()))
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        // Product list is used by employee/admin screens.
        logger.info("Received request to fetch all products");

        try {
            List<ProductResponseDTO> response = productService.getAllProducts();

            logger.info("Fetched {} products", response.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch products", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        // Product details are fetched before edit or account opening.
        logger.info("Received request to fetch product with id: {}", id);

        try {
            ProductResponseDTO response = productService.getProductById(id);

            logger.info("Product fetched successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch product with id: {}", id, ex);

            return ResponseEntity
                    .status(resolveStatus(ex.getMessage()))
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequestDTO request) {
        // Product configuration changes are tracked with employee id.
        logger.info("Received product update request for id: {}", id);

        try {
            ProductResponseDTO response = productService.updateProduct(id, request);

            logger.info("Product updated successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Product update failed for id: {}", id, ex);

            return ResponseEntity
                    .status(resolveStatus(ex.getMessage()))
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable String id,
            @RequestBody ProductStatusUpdateRequestDTO request) {
        // Product status controls whether new accounts can use this product.
        logger.info("Received product status update request for id: {}", id);

        try {
            ProductResponseDTO response = productService.updateProductStatus(id, request);

            logger.info("Product status updated successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Product status update failed for id: {}", id, ex);

            return ResponseEntity
                    .status(resolveStatus(ex.getMessage()))
                    .body(ex.getMessage());
        }
    }

    private HttpStatus resolveStatus(String message) {

        if (message == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (message.contains("not found")) {
            return HttpStatus.NOT_FOUND;
        }

        if (message.contains("already exists")) {
            return HttpStatus.CONFLICT;
        }

        if (message.contains("required")
                || message.contains("Invalid")
                || message.contains("negative")) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}