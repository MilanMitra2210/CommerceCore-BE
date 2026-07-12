package com.commercecore.api.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing transactional attributes of a ProductVariant.
 */
@Getter
@Setter
public class ProductVariantDto {

    private UUID id;

    @NotBlank(message = "Variant SKU is required")
    private String sku;

    private String barcode;
    private String title;

    @NotNull(message = "Variant price is required")
    private BigDecimal price;

    private BigDecimal compareAtPrice;
    private BigDecimal costPrice;
    private String currencyCode = "INR";

    private int inventoryQuantity = 0;
    private int lowStockThreshold = 0;
    private boolean allowBackorder = false;

    private BigDecimal weightValue;
    private String weightUnit;

    // Physical shipping packaging dimensions
    private BigDecimal dimensionLength;
    private BigDecimal dimensionWidth;
    private BigDecimal dimensionHeight;
    private String dimensionUnit;

    // Scalable sizing parameters mapping (e.g. {"primary": {"value": "5x8", "unit": "ft"}})
    private Map<String, Object> size;

    private boolean isDefault = false;
    private boolean active = true;
    private boolean purchasable = true;
    private int displayOrder = 0;

    // Attribute mapping values list: e.g. [{"attributeSlug": "color", "valueSlug": "beige"}]
    private List<VariantAttributeSelectionDto> attributeSelections;

    // Associated media file UUIDs
    private List<VariantMediaDto> media;

}
