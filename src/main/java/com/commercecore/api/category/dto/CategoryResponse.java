package com.commercecore.api.category.dto;

import com.commercecore.api.common.dto.SeoDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Output DTO representing a category and its nested relationships.
 */
@Getter
@Setter
public class CategoryResponse {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private int displayOrder;
    private boolean upcoming;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    
    private SeoDto seo;
    private List<com.commercecore.api.common.dto.ContentBlockDto> contentBlocks;
    private List<SubCategoryResponse> subCategories;

}
