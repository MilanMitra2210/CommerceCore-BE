package com.commercecore.api.category.service;

import com.commercecore.api.category.dto.CategoryRequest;
import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface defining category and subcategory operations.
 */
public interface CategoryService {

    // ---- Category Operations ----

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(UUID id, CategoryRequest request);

    CategoryResponse getCategoryById(UUID id);

    CategoryResponse getCategoryBySlug(String slug);

    Page<CategoryResponse> getCategories(String search, Pageable pageable);

    void deleteCategory(UUID id);

    // ---- SubCategory Operations ----

    SubCategoryResponse createSubCategory(SubCategoryRequest request);

    SubCategoryResponse updateSubCategory(UUID id, SubCategoryRequest request);

    SubCategoryResponse getSubCategoryById(UUID id);

    SubCategoryResponse getSubCategoryBySlug(String slug);

    Page<SubCategoryResponse> getSubCategories(String search, Pageable pageable);

    Page<SubCategoryResponse> getSubCategoriesByParent(UUID categoryId, Pageable pageable);

    void deleteSubCategory(UUID id);

}
