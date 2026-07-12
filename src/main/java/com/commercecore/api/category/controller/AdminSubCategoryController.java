package com.commercecore.api.category.controller;

import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.service.CategoryService;
import com.commercecore.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller exposing write operations for subcategories matching admin expectation endpoints (/admin/sub-categories).
 */
@RestController
@RequestMapping("/admin/sub-categories")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin SubCategory Management", description = "Endpoints for administrators to write/update subcategories")
public class AdminSubCategoryController {

    private final CategoryService categoryService;

    public AdminSubCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create subcategory")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> createSubCategory(
            @RequestBody @Valid SubCategoryRequest request) {
        SubCategoryResponse response = categoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subcategory created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subcategory")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable UUID id,
            @RequestBody @Valid SubCategoryRequest request) {
        SubCategoryResponse response = categoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subcategory updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subcategory")
    public ResponseEntity<ApiResponse<Void>> deleteSubCategory(@PathVariable UUID id) {
        categoryService.deleteSubCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Subcategory deleted successfully"));
    }

}
