package com.commercecore.api.catalog.controller;

import com.commercecore.api.catalog.dto.ProductResponse;
import com.commercecore.api.catalog.entity.FilterGroup;
import com.commercecore.api.catalog.repository.FilterGroupRepository;
import com.commercecore.api.catalog.service.ProductService;
import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.common.dto.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Public storefront controller for browsing the product catalog.
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Storefront Catalog", description = "Public endpoints for catalog lookups and faceted search")
public class ProductController {

    private final ProductService productService;
    private final FilterGroupRepository filterGroupRepository;

    public ProductController(ProductService productService, FilterGroupRepository filterGroupRepository) {
        this.productService = productService;
        this.filterGroupRepository = filterGroupRepository;
    }

    @GetMapping
    @Operation(summary = "List and search products", description = "Returns a paginated list of catalog products")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("displayOrder").ascending());
        Page<ProductResponse> products = productService.getProducts(search, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", PaginatedResponse.from(products))
        );
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get product by slug", description = "Fetches a product's full layout tree, dynamic specs, and variant options")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(
                ApiResponse.success("Product retrieved successfully", product)
        );
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List products by category", description = "Returns products belonging to a category scope")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("displayOrder").ascending());
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Category products retrieved successfully", PaginatedResponse.from(products))
        );
    }

    @GetMapping("/subcategory/{subCategoryId}")
    @Operation(summary = "List products by subcategory")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getProductsBySubCategory(
            @PathVariable UUID subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("displayOrder").ascending());
        Page<ProductResponse> products = productService.getProductsBySubCategory(subCategoryId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Subcategory products retrieved successfully", PaginatedResponse.from(products))
        );
    }

    @GetMapping("/filters")
    @Operation(summary = "Get category faceted filters", description = "Returns active filter groups and selection options for a category sidebar")
    public ResponseEntity<ApiResponse<List<FilterGroup>>> getCategoryFilters(@RequestParam UUID categoryId) {
        List<FilterGroup> filters = filterGroupRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
        return ResponseEntity.ok(
                ApiResponse.success("Faceted filter parameters retrieved", filters)
        );
    }

}
