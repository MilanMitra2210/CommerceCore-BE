package com.commercecore.api.category.controller;

import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.service.CategoryService;
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

/**
 * Public storefront controller for browsing category hierarchies.
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "Storefront Catalog", description = "Public endpoints for browsing categories and subcategories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List categories", description = "Returns a paginated list of categories sorted by display order")
    public ResponseEntity<ApiResponse<PaginatedResponse<CategoryResponse>>> getCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by("displayOrder").ascending());
        Page<CategoryResponse> categories = categoryService.getCategories(search, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Categories retrieved successfully", PaginatedResponse.from(categories))
        );
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieves a category page along with its active subcategories and SEO metadata")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(
                ApiResponse.success("Category retrieved successfully", category)
        );
    }

    @GetMapping("/subcategories/{slug}")
    @Operation(summary = "Get subcategory by slug", description = "Retrieves subcategory page layouts and SEO metadata")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> getSubCategoryBySlug(@PathVariable String slug) {
        SubCategoryResponse subCategory = categoryService.getSubCategoryBySlug(slug);
        return ResponseEntity.ok(
                ApiResponse.success("Subcategory retrieved successfully", subCategory)
        );
    }

}
