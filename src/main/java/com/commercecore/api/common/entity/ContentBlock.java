package com.commercecore.api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Shared entity storing content blocks as a JSONB list of key-value structures.
 *
 * <p>Content blocks allow storefront administrators to construct custom layouts
 * (banners, text blocks, grids) on categories and products.
 */
@Entity
@Table(name = "content_blocks")
@Getter
@Setter
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "blocks", columnDefinition = "jsonb", nullable = false)
    private List<Map<String, Object>> blocks = new ArrayList<>();

}
