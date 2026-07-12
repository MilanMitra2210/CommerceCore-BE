package com.commercecore.api.category.service;

import com.commercecore.api.category.dto.CategoryRequest;
import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.entity.Category;
import com.commercecore.api.category.entity.SubCategory;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.entity.SeoMetadata;
import com.commercecore.api.common.exception.DuplicateResourceException;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import com.commercecore.api.category.mapper.CategoryMapper;
import com.commercecore.api.common.mapper.SeoMapper;
import com.commercecore.api.category.repository.CategoryRepository;
import com.commercecore.api.category.repository.SubCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryMapper categoryMapper;
    private final SeoMapper seoMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            SubCategoryRepository subCategoryRepository,
            CategoryMapper categoryMapper,
            SeoMapper seoMapper) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.categoryMapper = categoryMapper;
        this.seoMapper = seoMapper;
    }

    // ==============================================================================
    // Category Operations
    // ==============================================================================

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category with name={}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", request.getSlug());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        log.info("Updating category id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Validation checks for unique name/slug if modified
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }
        if (!category.getSlug().equals(request.getSlug()) && categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", request.getSlug());
        }

        // Apply modifications
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setUpcoming(request.isUpcoming());
        category.setActive(request.isActive());

        // Update SEO Metadata
        if (request.getSeo() != null) {
            if (category.getSeo() == null) {
                category.setSeo(seoMapper.toEntity(request.getSeo()));
            } else {
                SeoMetadata seo = category.getSeo();
                seo.setMetaTitle(request.getSeo().getMetaTitle());
                seo.setMetaDescription(request.getSeo().getMetaDescription());
                seo.setMetaKeywords(request.getSeo().getMetaKeywords());
                seo.setMetaRobots(request.getSeo().getMetaRobots());
                seo.setMetaImageId(request.getSeo().getMetaImageId());
            }
        } else {
            category.setSeo(null);
        }

        // Update Content Blocks
        if (request.getContentBlocks() != null) {
            if (category.getContentBlocks() == null) {
                ContentBlock block = new ContentBlock();
                block.setBlocks(request.getContentBlocks());
                category.setContentBlocks(block);
            } else {
                category.getContentBlocks().setBlocks(request.getContentBlocks());
            }
        } else {
            category.setContentBlocks(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return categoryMapper.toResponse(category);
    }

    @Override
    public Page<CategoryResponse> getCategories(String search, Pageable pageable) {
        Page<Category> categoriesPage;
        if (search != null && !search.trim().isEmpty()) {
            categoriesPage = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            categoriesPage = categoryRepository.findAll(pageable);
        }
        return categoriesPage.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Soft deleting category id={}", id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categoryRepository.delete(category);
    }

    // ==============================================================================
    // SubCategory Operations
    // ==============================================================================

    @Override
    @Transactional
    public SubCategoryResponse createSubCategory(SubCategoryRequest request) {
        log.info("Creating subcategory name={} under parentId={}", request.getName(), request.getCategoryId());

        Category parent = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        if (subCategoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("SubCategory", "slug", request.getSlug());
        }

        SubCategory subCategory = categoryMapper.toEntity(request);
        subCategory.setCategory(parent);

        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return categoryMapper.toResponse(savedSubCategory);
    }

    @Override
    @Transactional
    public SubCategoryResponse updateSubCategory(UUID id, SubCategoryRequest request) {
        log.info("Updating subcategory id={}", id);

        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", id));

        Category parent = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        if (!subCategory.getSlug().equals(request.getSlug()) && subCategoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("SubCategory", "slug", request.getSlug());
        }

        subCategory.setName(request.getName());
        subCategory.setSlug(request.getSlug());
        subCategory.setDescription(request.getDescription());
        subCategory.setDisplayOrder(request.getDisplayOrder());
        subCategory.setActive(request.isActive());
        subCategory.setCategory(parent);

        // Update SEO Metadata
        if (request.getSeo() != null) {
            if (subCategory.getSeo() == null) {
                subCategory.setSeo(seoMapper.toEntity(request.getSeo()));
            } else {
                SeoMetadata seo = subCategory.getSeo();
                seo.setMetaTitle(request.getSeo().getMetaTitle());
                seo.setMetaDescription(request.getSeo().getMetaDescription());
                seo.setMetaKeywords(request.getSeo().getMetaKeywords());
                seo.setMetaRobots(request.getSeo().getMetaRobots());
                seo.setMetaImageId(request.getSeo().getMetaImageId());
            }
        } else {
            subCategory.setSeo(null);
        }

        // Update Content Blocks
        if (request.getContentBlocks() != null) {
            if (subCategory.getContentBlocks() == null) {
                ContentBlock block = new ContentBlock();
                block.setBlocks(request.getContentBlocks());
                subCategory.setContentBlocks(block);
            } else {
                subCategory.getContentBlocks().setBlocks(request.getContentBlocks());
            }
        } else {
            subCategory.setContentBlocks(null);
        }

        SubCategory updated = subCategoryRepository.save(subCategory);
        return categoryMapper.toResponse(updated);
    }

    @Override
    public SubCategoryResponse getSubCategoryById(UUID id) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SubCategory", id));
        return categoryMapper.toResponse(subCategory);
    }

    @Override
    public SubCategoryResponse getSubCategoryBySlug(String slug) {
        SubCategory subCategory = subCategoryRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("SubCategory", "slug", slug));
        return categoryMapper.toResponse(subCategory);
    }

    @Override
    public Page<SubCategoryResponse> getSubCategories(String search, Pageable pageable) {
        Page<SubCategory> subPage;
        if (search != null && !search.trim().isEmpty()) {
            subPage = subCategoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            subPage = subCategoryRepository.findAll(pageable);
        }
        return subPage.map(categoryMapper::toResponse);
    }

    @Override
    public Page<SubCategoryResponse> getSubCategoriesByParent(UUID categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }
        return subCategoryRepository.findByCategoryId(categoryId, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteSubCategory(UUID id) {
        log.info("Soft deleting subcategory id={}", id);
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", id));
        subCategoryRepository.delete(subCategory);
    }

}
