package com.commercecore.api.category.dto;

import com.commercecore.api.common.dto.SeoDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Output DTO representing a subcategory record.
 */
@Getter
@Setter
public class SubCategoryResponse {

    private UUID id;
    private UUID categoryId;
    private String name;
    private String slug;
    private String description;
    private int displayOrder;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    private SeoDto seo;
    private List<com.commercecore.api.common.dto.ContentBlockDto> contentBlocks;

}
