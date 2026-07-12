package com.commercecore.api.catalog.service;

import com.commercecore.api.catalog.dto.ProductRequest;
import com.commercecore.api.catalog.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface defining product catalog operations.
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(UUID id, ProductRequest request);

    ProductResponse getProductById(UUID id);

    ProductResponse getProductBySlug(String slug);

    Page<ProductResponse> getProducts(String search, Pageable pageable);

    Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    Page<ProductResponse> getProductsBySubCategory(UUID subCategoryId, Pageable pageable);

    void deleteProduct(UUID id);

}
