package com.commercecore.api.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO representing reusable merchandising badges.
 */
@Getter
@Setter
public class BadgeDto {

    private UUID id;

    @NotBlank(message = "Badge label is required")
    private String label;

    private String colorCode;
    private int displayOrder = 0;

}
