package com.commercecore.api.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Common DTO representing search engine optimization (SEO) metadata.
 */
@Getter
@Setter
public class SeoDto {

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String metaRobots;
    private UUID metaImageId;

}
