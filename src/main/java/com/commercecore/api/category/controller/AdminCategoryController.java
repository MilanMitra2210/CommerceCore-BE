package com.commercecore.api.category.controller;

import com.commercecore.api.category.dto.CategoryRequest;
import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.service.CategoryService;
import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.common.dto.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Administrative controller for managing catalog taxonomies.
 */
@RestController
@RequestMapping("/admin/categories")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Catalog Management", description = "Endpoints for administrators to write/update categories and subcategories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ---- Admin Category Write Operations ----

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new category along with nested SEO and ContentBlock metadata")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Modifies an existing category and updates its metadata")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Soft deletes a category and cascade detaches nested structures")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // ---- Admin SubCategory Write Operations ----

    @PostMapping("/subcategories")
    @Operation(summary = "Create subcategory", description = "Creates a subcategory under an existing parent category")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> createSubCategory(
            @RequestBody @Valid SubCategoryRequest request) {
        SubCategoryResponse response = categoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subcategory created successfully", response));
    }

    @PutMapping("/subcategories/{id}")
    @Operation(summary = "Update subcategory", description = "Modifies subcategory attributes")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable UUID id,
            @RequestBody @Valid SubCategoryRequest request) {
        SubCategoryResponse response = categoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subcategory updated successfully", response));
    }

    @DeleteMapping("/subcategories/{id}")
    @Operation(summary = "Delete subcategory", description = "Soft deletes a subcategory record")
    public ResponseEntity<ApiResponse<Void>> deleteSubCategory(@PathVariable UUID id) {
        categoryService.deleteSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Subcategory deleted successfully"));
    }

    // ---- Admin Lookups ----

    @GetMapping("/subcategories")
    @Operation(summary = "List all subcategories", description = "Returns a paginated list of subcategories for admin grid views")
    public ResponseEntity<ApiResponse<PaginatedResponse<SubCategoryResponse>>> getSubCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        int pageIndex = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("displayOrder").ascending());
        Page<SubCategoryResponse> subcategories = categoryService.getSubCategories(search, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Subcategories retrieved successfully", PaginatedResponse.from(subcategories))
        );
    }
}
