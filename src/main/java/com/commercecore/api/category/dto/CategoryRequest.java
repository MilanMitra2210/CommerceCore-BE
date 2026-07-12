package com.commercecore.api.category.dto;

import com.commercecore.api.common.dto.SeoDto;
import com.commercecore.api.common.dto.ContentBlockDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Input DTO for creating or updating a Category.
 */
@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase alphanumeric and hyphenated only")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String description;

    private int displayOrder = 0;

    private boolean upcoming = false;

    @JsonProperty("isActive")
    private boolean active = true;

    @Valid
    private SeoDto seo;

    @Valid
    private List<ContentBlockDto> contentBlocks;

}
