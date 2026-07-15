package com.commercecore.api.catalog.service;

import com.commercecore.api.catalog.dto.FilterGroupRequest;
import com.commercecore.api.catalog.dto.FilterGroupResponse;
import com.commercecore.api.catalog.dto.FilterOptionRequest;
import com.commercecore.api.catalog.entity.FilterGroup;
import com.commercecore.api.catalog.entity.FilterOption;
import com.commercecore.api.catalog.entity.Product;
import com.commercecore.api.catalog.entity.ProductFilterValue;
import com.commercecore.api.catalog.mapper.FilterGroupMapper;
import com.commercecore.api.catalog.repository.FilterGroupRepository;
import com.commercecore.api.catalog.repository.FilterOptionRepository;
import com.commercecore.api.catalog.repository.ProductFilterValueRepository;
import com.commercecore.api.catalog.repository.ProductRepository;
import com.commercecore.api.category.entity.Category;
import com.commercecore.api.category.entity.SubCategory;
import com.commercecore.api.category.repository.CategoryRepository;
import com.commercecore.api.category.repository.SubCategoryRepository;
import com.commercecore.api.media.entity.Media;
import com.commercecore.api.media.repository.MediaRepository;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilterGroupServiceImpl implements FilterGroupService {

    private final FilterGroupRepository filterGroupRepository;
    private final FilterOptionRepository filterOptionRepository;
    private final ProductFilterValueRepository productFilterValueRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final MediaRepository mediaRepository;
    private final FilterGroupMapper filterGroupMapper;

    @Override
    @Transactional
    public FilterGroupResponse createFilterGroup(FilterGroupRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        FilterGroup group = filterGroupMapper.toEntity(request);
        group.setCategory(category);

        if (request.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", request.getSubCategoryId()));
            group.setSubCategory(subCategory);
        }

        if (request.getOptions() != null) {
            for (FilterOptionRequest optReq : request.getOptions()) {
                FilterOption option = filterGroupMapper.toEntity(optReq);
                option.setFilterGroup(group);
                if (optReq.getMediaId() != null) {
                    Media media = mediaRepository.findById(optReq.getMediaId()).orElse(null);
                    option.setMedia(media);
                }
                group.getOptions().add(option);
            }
        }

        FilterGroup saved = filterGroupRepository.save(group);
        return filterGroupMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public FilterGroupResponse updateFilterGroup(UUID id, FilterGroupRequest request) {
        FilterGroup group = filterGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FilterGroup", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        group.setCategory(category);

        if (request.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", request.getSubCategoryId()));
            group.setSubCategory(subCategory);
        } else {
            group.setSubCategory(null);
        }

        group.setFilterKey(request.getFilterKey());
        group.setLabel(request.getLabel());
        group.setSingleSelect(request.isSingleSelect());
        group.setDisplayOrder(request.getDisplayOrder());

        // Update Option entities
        group.getOptions().clear();
        if (request.getOptions() != null) {
            for (FilterOptionRequest optReq : request.getOptions()) {
                FilterOption option = filterGroupMapper.toEntity(optReq);
                option.setFilterGroup(group);
                if (optReq.getMediaId() != null) {
                    Media media = mediaRepository.findById(optReq.getMediaId()).orElse(null);
                    option.setMedia(media);
                }
                group.getOptions().add(option);
            }
        }

        FilterGroup updated = filterGroupRepository.save(group);
        return filterGroupMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FilterGroupResponse> getFilterGroups(String search, UUID categoryId, UUID subCategoryId, Pageable pageable) {
        Specification<FilterGroup> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (subCategoryId != null) {
                predicates.add(cb.equal(root.get("subCategory").get("id"), subCategoryId));
            }

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("label")), "%" + search.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return filterGroupRepository.findAll(spec, pageable).map(filterGroupMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FilterGroupResponse getFilterGroupById(UUID id) {
        FilterGroup group = filterGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FilterGroup", id));
        return filterGroupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public void deleteFilterGroup(UUID id) {
        FilterGroup group = filterGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FilterGroup", id));
        filterGroupRepository.delete(group);
    }

    @Override
    @Transactional
    public void assignFilterValues(UUID productId, List<Map<String, String>> values) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // Delete existing filter mapping values first
        productFilterValueRepository.deleteByProductId(productId);

        if (values != null) {
            for (Map<String, String> item : values) {
                UUID groupId = UUID.fromString(item.get("filterGroupId"));
                UUID optionId = UUID.fromString(item.get("filterOptionId"));

                FilterGroup group = filterGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ResourceNotFoundException("FilterGroup", groupId));
                FilterOption option = filterOptionRepository.findById(optionId)
                        .orElseThrow(() -> new ResourceNotFoundException("FilterOption", optionId));

                ProductFilterValue val = new ProductFilterValue();
                val.setProduct(product);
                val.setFilterGroup(group);
                val.setFilterOption(option);

                productFilterValueRepository.save(val);
            }
        }
    }
}
