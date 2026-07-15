package com.commercecore.api.cms.dto;

import com.commercecore.api.common.dto.ContentBlockDto;
import com.commercecore.api.common.dto.SeoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO representing a CMS Page details returned to client.
 */
@Getter
@Setter
public class CMSPageResponse {

    private UUID id;
    private String name;
    private String slug;

    @JsonProperty("isActive")
    private boolean isActive;

    private SeoDto seo;
    private List<ContentBlockDto> contentBlocks;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

}
