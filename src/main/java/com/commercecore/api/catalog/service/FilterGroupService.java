package com.commercecore.api.catalog.service;

import com.commercecore.api.catalog.dto.FilterGroupRequest;
import com.commercecore.api.catalog.dto.FilterGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FilterGroupService {

    FilterGroupResponse createFilterGroup(FilterGroupRequest request);

    FilterGroupResponse updateFilterGroup(UUID id, FilterGroupRequest request);

    Page<FilterGroupResponse> getFilterGroups(String search, UUID categoryId, UUID subCategoryId, Pageable pageable);

    FilterGroupResponse getFilterGroupById(UUID id);

    void deleteFilterGroup(UUID id);

    void assignFilterValues(UUID productId, List<Map<String, String>> values);

}
