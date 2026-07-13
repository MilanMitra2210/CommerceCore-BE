package com.commercecore.api.catalog.dto;

import com.commercecore.api.common.dto.SeoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * Input DTO for creating or updating a Product catalog record.
 */
@Getter
@Setter
public class ProductRequest {

    @NotNull(message = "Parent Category ID is required")
    private UUID categoryId;

    private UUID subCategoryId;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Product slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase alphanumeric and hyphenated only")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String description;

    @JsonProperty("isBestseller")
    private boolean bestseller = false;

    @JsonProperty("isFeatured")
    private boolean featured = false;

    @JsonProperty("isActive")
    private boolean active = true;

    private int displayOrder = 0;

    @Valid
    private SeoDto seo;

    // Content Block DTO mappings
    private List<Map<String, Object>> contentBlocks;

    // List of reusable badge labels (e.g. ["Best Seller", "New Arrival"])
    private List<String> badgeLabels;

    @Valid
    private List<ProductVariantDto> variants;

    @Valid
    private List<ProductInfoTabDto> infoTabs;

    // Facet options mappings: list of option UUIDs to assign to this product
    private List<UUID> filterOptionIds;

}
