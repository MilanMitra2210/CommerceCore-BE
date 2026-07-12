package com.commercecore.api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Shared entity storing search engine optimization (SEO) metadata.
 *
 * <p>Linked via one-to-one associations from categories, products, etc.
 */
@Entity
@Table(name = "seo_metadata")
@Getter
@Setter
public class SeoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = 1000)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 512)
    private String metaKeywords;

    @Column(name = "meta_robots", length = 100)
    private String metaRobots;

    @Column(name = "meta_image_id")
    private UUID metaImageId; // References media.id in the future

}
