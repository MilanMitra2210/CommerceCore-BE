package com.commercecore.api.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Helper DTO mapping media parameters to variants.
 */
@Getter
@Setter
public class VariantMediaDto {

    @NotNull(message = "Media ID is required")
    private UUID mediaId;

    private String url;
    private String altText;
    private String role = "gallery";
    private int displayOrder = 0;

}
