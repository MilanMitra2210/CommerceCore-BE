package com.commercecore.api.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO representing product specification detail tabs.
 */
@Getter
@Setter
public class ProductInfoTabDto {

    private UUID id;

    @NotBlank(message = "Tab title is required")
    private String title;

    @NotBlank(message = "Tab content details are required")
    private String content;

    private int displayOrder = 0;

}
