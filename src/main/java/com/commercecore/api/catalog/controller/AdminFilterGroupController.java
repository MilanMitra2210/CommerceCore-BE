package com.commercecore.api.catalog.controller;

import com.commercecore.api.catalog.dto.FilterGroupRequest;
import com.commercecore.api.catalog.dto.FilterGroupResponse;
import com.commercecore.api.catalog.service.FilterGroupService;
import com.commercecore.api.common.dto.ApiResponse;
import com.commercecore.api.common.dto.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Filter Configurations", description = "Endpoints for administrators to configure faceted category filters")
@RequiredArgsConstructor
public class AdminFilterGroupController {

    private final FilterGroupService filterGroupService;

    @GetMapping("/admin/filters/groups")
    @Operation(summary = "List filter configurations", description = "Returns a paginated list of configured faceted filter groups")
    public ResponseEntity<ApiResponse<PaginatedResponse<FilterGroupResponse>>> getFilterGroups(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID subCategoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit) {

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by("displayOrder").ascending());
        Page<FilterGroupResponse> result = filterGroupService.getFilterGroups(search, categoryId, subCategoryId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Filter configurations retrieved successfully", PaginatedResponse.from(result))
        );
    }

    @GetMapping("/admin/filters/groups/{id}")
    @Operation(summary = "Get filter group by ID")
    public ResponseEntity<ApiResponse<FilterGroupResponse>> getFilterGroupById(@PathVariable UUID id) {
        FilterGroupResponse response = filterGroupService.getFilterGroupById(id);
        return ResponseEntity.ok(ApiResponse.success("Filter group retrieved successfully", response));
    }

    @PostMapping("/admin/filters/groups")
    @Operation(summary = "Create filter group configuration")
    public ResponseEntity<ApiResponse<FilterGroupResponse>> createFilterGroup(
            @RequestBody FilterGroupRequest request) {
        FilterGroupResponse response = filterGroupService.createFilterGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Filter group created successfully", response));
    }

    @PutMapping("/admin/filters/groups/{id}")
    @Operation(summary = "Update filter group configuration")
    public ResponseEntity<ApiResponse<FilterGroupResponse>> updateFilterGroup(
            @PathVariable UUID id,
            @RequestBody FilterGroupRequest request) {
        FilterGroupResponse response = filterGroupService.updateFilterGroup(id, request);
        return ResponseEntity.ok(ApiResponse.success("Filter group updated successfully", response));
    }

    @DeleteMapping("/admin/filters/groups/{id}")
    @Operation(summary = "Delete filter group configuration")
    public ResponseEntity<ApiResponse<Void>> deleteFilterGroup(@PathVariable UUID id) {
        filterGroupService.deleteFilterGroup(id);
        return ResponseEntity.ok(ApiResponse.success("Filter group deleted successfully"));
    }

    @PostMapping("/admin/products/{productId}/filter-values")
    @Operation(summary = "Assign filter value choices to product")
    public ResponseEntity<ApiResponse<Void>> assignFilterValues(
            @PathVariable UUID productId,
            @RequestBody List<Map<String, String>> values) {
        filterGroupService.assignFilterValues(productId, values);
        return ResponseEntity.ok(ApiResponse.success("Filter values assigned successfully"));
    }

}
