package com.bankofgeorgia.corebanking.product.controller;

import com.bankofgeorgia.corebanking.product.dto.ProductRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductResponseDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.UpdateProductRequestDTO;
import com.bankofgeorgia.corebanking.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct_WhenRequestIsValid() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO(
                "Checking Account",
                "CHECKING_ACCOUNT",
                "Basic checking account with monthly fee rules",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "emp001");

        ProductResponseDTO response = buildProductResponse();

        Mockito.when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("prod001"))
                .andExpect(jsonPath("$.productName").value("Checking Account"))
                .andExpect(jsonPath("$.productType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createProduct_ShouldReturnConflict_WhenProductAlreadyExists() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO(
                "Checking Account",
                "CHECKING_ACCOUNT",
                "Basic checking account with monthly fee rules",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "emp001");

        Mockito.when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenThrow(new RuntimeException("Product name already exists"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Product name already exists"));
    }

    @Test
    void getAllProducts_ShouldReturnProductList_WhenProductsExist() throws Exception {
        ProductResponseDTO response = buildProductResponse();

        Mockito.when(productService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("prod001"))
                .andExpect(jsonPath("$[0].productName").value("Checking Account"));
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() throws Exception {
        Mockito.when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        ProductResponseDTO response = buildProductResponse();

        Mockito.when(productService.getProductById("prod001")).thenReturn(response);

        mockMvc.perform(get("/api/products/prod001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod001"))
                .andExpect(jsonPath("$.productName").value("Checking Account"))
                .andExpect(jsonPath("$.productType").value("CHECKING_ACCOUNT"));
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        Mockito.when(productService.getProductById("missing-product"))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/products/missing-product"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenRequestIsValid() throws Exception {
        UpdateProductRequestDTO request = new UpdateProductRequestDTO(
                "Updated Checking Account",
                "Updated checking account configuration",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "emp001");

        ProductResponseDTO response = new ProductResponseDTO(
                "prod001",
                "Updated Checking Account",
                "CHECKING_ACCOUNT",
                "Updated checking account configuration",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "ACTIVE",
                "emp001",
                "emp001",
                "2026-05-03T20:00:00Z",
                "2026-05-03T20:10:00Z");

        Mockito.when(productService.updateProduct(eq("prod001"), any(UpdateProductRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/prod001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod001"))
                .andExpect(jsonPath("$.productName").value("Updated Checking Account"))
                .andExpect(jsonPath("$.description").value("Updated checking account configuration"));
    }

    @Test
    void updateProduct_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        UpdateProductRequestDTO request = new UpdateProductRequestDTO(
                "Checking Account",
                "Invalid fee update",
                new BigDecimal("-5.00"),
                new BigDecimal("100.00"),
                "emp001");

        Mockito.when(productService.updateProduct(eq("prod001"), any(UpdateProductRequestDTO.class)))
                .thenThrow(new RuntimeException("Monthly maintenance fee cannot be negative"));

        mockMvc.perform(put("/api/products/prod001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Monthly maintenance fee cannot be negative"));
    }

    @Test
    void updateProductStatus_ShouldReturnUpdatedProduct_WhenStatusIsValid() throws Exception {
        ProductStatusUpdateRequestDTO request = new ProductStatusUpdateRequestDTO("INACTIVE", "emp001");

        ProductResponseDTO response = new ProductResponseDTO(
                "prod001",
                "Checking Account",
                "CHECKING_ACCOUNT",
                "Basic checking account with monthly fee rules",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "INACTIVE",
                "emp001",
                "emp001",
                "2026-05-03T20:00:00Z",
                "2026-05-03T20:15:00Z");

        Mockito.when(productService.updateProductStatus(eq("prod001"), any(ProductStatusUpdateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/prod001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod001"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void updateProductStatus_ShouldReturnBadRequest_WhenStatusIsInvalid() throws Exception {
        ProductStatusUpdateRequestDTO request = new ProductStatusUpdateRequestDTO("DISABLED", "emp001");

        Mockito.when(productService.updateProductStatus(eq("prod001"), any(ProductStatusUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid product status"));

        mockMvc.perform(put("/api/products/prod001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid product status"));
    }

    private ProductResponseDTO buildProductResponse() {
        return new ProductResponseDTO(
                "prod001",
                "Checking Account",
                "CHECKING_ACCOUNT",
                "Basic checking account with monthly fee rules",
                new BigDecimal("5.00"),
                new BigDecimal("100.00"),
                "ACTIVE",
                "emp001",
                "emp001",
                "2026-05-03T20:00:00Z",
                "2026-05-03T20:00:00Z");
    }
}