package com.commercecore.api.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isActive")
    private boolean active = true;

    @JsonProperty("isPurchasable")
    private boolean purchasable = true;

    private int displayOrder = 0;

    // Attribute mapping values list: e.g. [{"attributeSlug": "color", "valueSlug": "beige"}]
    private List<VariantAttributeSelectionDto> attributeSelections;

    // Associated media file UUIDs
    private List<VariantMediaDto> media;

    @JsonProperty("dimensions")
    public Map<String, Object> retrieveDimensions() {
        java.util.HashMap<String, Object> map = new java.util.HashMap<>();
        map.put("width", dimensionWidth);
        map.put("height", dimensionHeight);
        map.put("depth", dimensionLength);
        return map;
    }

    @JsonProperty("dimensions")
    public void populateDimensions(Map<String, Object> dimensions) {
        if (dimensions != null) {
            this.dimensionWidth = parseBigDecimal(dimensions.get("width"));
            this.dimensionHeight = parseBigDecimal(dimensions.get("height"));
            this.dimensionLength = parseBigDecimal(dimensions.get("depth"));
        }
    }

    private BigDecimal parseBigDecimal(Object value) {
        if (value == null || value.toString().isBlank()) return null;
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
