package com.bankofgeorgia.corebanking.product.controller;

import com.bankofgeorgia.corebanking.product.dto.ProductRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductResponseDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.UpdateProductRequestDTO;
import com.bankofgeorgia.corebanking.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO request) {
        // Product creation is handled by employee/admin users.
        log.info("Received product creation request for product name: {}", request.getProductName());

        ProductResponseDTO response = productService.createProduct(request);

        log.info("Product created successfully with id: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        // Product list is used by employee/admin screens.
        log.info("Received request to fetch all products");

        List<ProductResponseDTO> response = productService.getAllProducts();

        log.info("Fetched {} products", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        // Product details are fetched before edit or account opening.
        log.info("Received request to fetch product with id: {}", id);

        ProductResponseDTO response = productService.getProductById(id);

        log.info("Product fetched successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequestDTO request) {

        // Product configuration changes are tracked with employee id.
        log.info("Received product update request for id: {}", id);

        ProductResponseDTO response = productService.updateProduct(id, request);

        log.info("Product updated successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProductResponseDTO> updateProductStatus(
            @PathVariable String id,
            @RequestBody ProductStatusUpdateRequestDTO request) {

        // Product status controls whether new accounts can use this product.
        log.info("Received product status update request for id: {}", id);

        ProductResponseDTO response = productService.updateProductStatus(id, request);

        log.info("Product status updated successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }
}