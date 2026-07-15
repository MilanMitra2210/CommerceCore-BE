package com.commercecore.api.catalog.dto;

import com.commercecore.api.common.dto.SeoDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Output DTO representing a full Product record and its relations.
 */
@Getter
@Setter
public class ProductResponse {

    private UUID id;
    private UUID categoryId;
    private UUID subCategoryId;
    private String name;
    private String slug;
    private String description;
    @com.fasterxml.jackson.annotation.JsonProperty("isBestseller")
    private boolean bestseller;

    @com.fasterxml.jackson.annotation.JsonProperty("isFeatured")
    private boolean featured;

    @com.fasterxml.jackson.annotation.JsonProperty("isActive")
    private boolean active;

    private int displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    private SeoDto seo;
    private List<Map<String, Object>> contentBlocks;
    private List<ProductInfoTabDto> infoTabs;
    private List<BadgeDto> badges;
    private List<ProductVariantDto> variants;

    // ---- Computed properties matching Python property logic ----
    private String sku;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private int inventory;
    private BigDecimal discount;
    private String discountType;

}
