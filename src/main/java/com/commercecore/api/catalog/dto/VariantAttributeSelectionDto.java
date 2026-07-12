package com.commercecore.api.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Helper DTO linking variant choices to attributes.
 */
@Getter
@Setter
public class VariantAttributeSelectionDto {

    @NotBlank(message = "Attribute slug is required")
    private String attributeSlug;

    @NotBlank(message = "Attribute value slug is required")
    private String valueSlug;

}
