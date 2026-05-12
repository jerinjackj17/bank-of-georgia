package com.bankofgeorgia.corebanking.product.service;

import com.bankofgeorgia.corebanking.product.dto.ProductRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductResponseDTO;
import com.bankofgeorgia.corebanking.product.dto.ProductStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.product.dto.UpdateProductRequestDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO request);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(String id);

    ProductResponseDTO updateProduct(String id, UpdateProductRequestDTO request);

    ProductResponseDTO updateProductStatus(String id, ProductStatusUpdateRequestDTO request);
}