package com.commercecore.api.cms.dto;

import com.commercecore.api.common.dto.ContentBlockDto;
import com.commercecore.api.common.dto.SeoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for creating or updating a CMS Page.
 */
@Getter
@Setter
public class CMSPageRequest {

    @NotBlank(message = "Page name is required")
    private String name;

    @NotBlank(message = "Page slug is required")
    @Pattern(regexp = "^[a-z0-9-_/]+$", message = "Slug must be lowercase alphanumeric, dashes, underscores, or slashes")
    private String slug;

    @JsonProperty("isActive")
    private boolean isActive = true;

    @Valid
    private SeoDto seo;

    private List<ContentBlockDto> contentBlocks;

}
