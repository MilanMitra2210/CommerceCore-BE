package com.commercecore.api.category.dto;

import com.commercecore.api.common.dto.SeoDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Input DTO for creating or updating a SubCategory.
 */
@Getter
@Setter
public class SubCategoryRequest {

    @NotNull(message = "Parent Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "SubCategory name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "SubCategory slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase alphanumeric and hyphenated only")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String description;

    private int displayOrder = 0;

    private boolean active = true;

    @Valid
    private SeoDto seo;

    @Valid
    private List<com.commercecore.api.common.dto.ContentBlockDto> contentBlocks;

}
