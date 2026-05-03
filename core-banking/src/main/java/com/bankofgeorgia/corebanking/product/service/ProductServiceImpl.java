package com.bankofgeorgia.corebanking.product.service;

import com.bankofgeorgia.corebanking.product.dto.ProductRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductResponseDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.UpdateProductRequestDTO;
import com.bankofgeorgia.corebanking.product.entity.Product;
import com.bankofgeorgia.corebanking.product.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {

        logger.info("Creating product with name: {}", request.getProductName());

        validateCreateRequest(request);

        String productName = request.getProductName().trim();
        String productType = request.getProductType().trim().toUpperCase();

        if (productRepository.existsByProductName(productName)) {
            logger.warn("Product creation failed. Product name already exists: {}", productName);
            throw new RuntimeException("Product name already exists");
        }

        if (productRepository.existsByProductType(productType)) {
            logger.warn("Product creation failed. Product type already exists: {}", productType);
            throw new RuntimeException("Product type already exists");
        }

        Instant now = Instant.now();

        Product product = new Product();
        product.setProductName(productName);
        product.setProductType(productType);
        product.setDescription(cleanText(request.getDescription()));
        product.setMonthlyMaintenanceFee(request.getMonthlyMaintenanceFee());
        product.setMinimumBalance(request.getMinimumBalance());
        product.setStatus(STATUS_ACTIVE);
        product.setCreatedByEmployeeId(cleanText(request.getCreatedByEmployeeId()));
        product.setUpdatedByEmployeeId(cleanText(request.getCreatedByEmployeeId()));
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        Product saved = productRepository.save(product);

        logger.info("Product created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {

        logger.info("Fetching all products");

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(String id) {

        logger.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    public ProductResponseDTO updateProduct(String id, UpdateProductRequestDTO request) {

        logger.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getProductName() != null && !request.getProductName().isBlank()) {
            String productName = request.getProductName().trim();

            productRepository.findByProductName(productName)
                    .filter(existingProduct -> !existingProduct.getId().equals(id))
                    .ifPresent(existingProduct -> {
                        logger.warn("Product update failed. Product name already exists: {}", productName);
                        throw new RuntimeException("Product name already exists");
                    });

            product.setProductName(productName);
        }

        if (request.getDescription() != null) {
            product.setDescription(cleanText(request.getDescription()));
        }

        if (request.getMonthlyMaintenanceFee() != null) {
            validateMoneyField(request.getMonthlyMaintenanceFee(), "Monthly maintenance fee");
            product.setMonthlyMaintenanceFee(request.getMonthlyMaintenanceFee());
        }

        if (request.getMinimumBalance() != null) {
            validateMoneyField(request.getMinimumBalance(), "Minimum balance");
            product.setMinimumBalance(request.getMinimumBalance());
        }

        if (request.getUpdatedByEmployeeId() != null) {
            product.setUpdatedByEmployeeId(cleanText(request.getUpdatedByEmployeeId()));
        }

        product.setUpdatedAt(Instant.now());

        Product updated = productRepository.save(product);

        logger.info("Product updated successfully with id: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    public ProductResponseDTO updateProductStatus(String id, ProductStatusUpdateRequestDTO request) {

        logger.info("Updating product status for id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String status = normalizeStatus(request.getStatus());

        product.setStatus(status);
        product.setUpdatedByEmployeeId(cleanText(request.getUpdatedByEmployeeId()));
        product.setUpdatedAt(Instant.now());

        Product updated = productRepository.save(product);

        logger.info("Product status updated successfully for id: {} to {}", updated.getId(), status);

        return mapToResponse(updated);
    }

    private void validateCreateRequest(ProductRequestDTO request) {

        if (request.getProductName() == null || request.getProductName().isBlank()) {
            throw new RuntimeException("Product name is required");
        }

        if (request.getProductType() == null || request.getProductType().isBlank()) {
            throw new RuntimeException("Product type is required");
        }

        validateProductType(request.getProductType());
        validateMoneyField(request.getMonthlyMaintenanceFee(), "Monthly maintenance fee");
        validateMoneyField(request.getMinimumBalance(), "Minimum balance");
    }

    private void validateProductType(String productType) {

        String normalizedType = productType.trim().toUpperCase();

        if (!"CHECKING_ACCOUNT".equals(normalizedType)
                && !"SAVINGS_ACCOUNT".equals(normalizedType)
                && !"CERTIFICATE_OF_DEPOSIT".equals(normalizedType)
                && !"BUSINESS_CHECKING_ACCOUNT".equals(normalizedType)
                && !"STUDENT_SAVINGS_ACCOUNT".equals(normalizedType)) {
            throw new RuntimeException("Invalid product type");
        }
    }

    private void validateMoneyField(BigDecimal value, String fieldName) {

        if (value == null) {
            throw new RuntimeException(fieldName + " is required");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(fieldName + " cannot be negative");
        }
    }

    private String normalizeStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Product status is required");
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_INACTIVE.equals(normalizedStatus)) {
            throw new RuntimeException("Invalid product status");
        }

        return normalizedStatus;
    }

    private String cleanText(String value) {
        return value == null ? null : value.trim();
    }

    private ProductResponseDTO mapToResponse(Product product) {

        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getProductType(),
                product.getDescription(),
                product.getMonthlyMaintenanceFee(),
                product.getMinimumBalance(),
                product.getStatus(),
                product.getCreatedByEmployeeId(),
                product.getUpdatedByEmployeeId(),
                product.getCreatedAt() == null ? null : product.getCreatedAt().toString(),
                product.getUpdatedAt() == null ? null : product.getUpdatedAt().toString());
    }
}