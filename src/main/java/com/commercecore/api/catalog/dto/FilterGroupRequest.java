package com.commercecore.api.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FilterGroupRequest {

    private UUID id;
    private UUID categoryId;
    private UUID subCategoryId;
    private String filterKey;
    private String label;

    @JsonProperty("isSingleSelect")
    private boolean isSingleSelect;

    private int displayOrder;
    private List<FilterOptionRequest> options;

}
