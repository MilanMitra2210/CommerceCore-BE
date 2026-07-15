package com.commercecore.api.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FilterGroupResponse {

    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private UUID subCategoryId;
    private String subCategoryName;
    private String filterKey;
    private String label;

    @JsonProperty("isSingleSelect")
    private boolean isSingleSelect;

    private int displayOrder;
    private List<FilterOptionResponse> options;
    private int optionsCount;

}
