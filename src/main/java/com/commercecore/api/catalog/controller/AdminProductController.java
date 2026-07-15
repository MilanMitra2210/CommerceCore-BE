package com.commercecore.api.catalog.controller;

import com.commercecore.api.catalog.dto.ProductRequest;
import com.commercecore.api.catalog.dto.ProductResponse;
import com.commercecore.api.catalog.service.ProductService;
import com.commercecore.api.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Administrative controller for managing catalog Products and Variants.
 */
@Slf4j
@RestController
@RequestMapping("/admin/products")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Catalog Management", description = "Endpoints for administrators to write/update products and variants")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create product via multipart form data", description = "Creates a product along with variants and tabs using multipart form data payload")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestPart("data") String dataJson) throws Exception {
        log.info("[CREATE] Raw JSON payload: {}", dataJson);
        ProductRequest request = objectMapper.readValue(dataJson, ProductRequest.class);
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Fetches a product catalog record by database UUID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update product via multipart form data", description = "Modifies a product, its variants, and metadata using multipart form data payload")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @RequestPart("data") String dataJson) throws Exception {
        log.info("[UPDATE] Raw JSON payload: {}", dataJson);
        ProductRequest request = objectMapper.readValue(dataJson, ProductRequest.class);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft deletes a product record and cascades variant deletes")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

}
