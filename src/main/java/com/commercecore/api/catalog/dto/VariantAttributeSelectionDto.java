package com.commercecore.api.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Helper DTO linking variant choices to attributes.
 */
@Getter
@Setter
public class VariantAttributeSelectionDto {

    @NotBlank(message = "Attribute slug is required")
    private String attributeSlug;

    private String attributeName;

    private String value;

    private String label;

    @NotBlank(message = "Attribute value slug is required")
    @JsonProperty("slug")
    private String valueSlug;

    private String colorHex;

    private BigDecimal sortValue;

}
