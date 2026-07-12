package com.commercecore.api.category.entity;

import com.commercecore.api.common.entity.BaseEntity;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.entity.SeoMetadata;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entity representing a product category taxonomy.
 *
 * <p>Inherits UUID key, soft-delete, and auditing fields from {@link BaseEntity}.
 */
@Entity
@Table(name = "categories")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "is_upcoming", nullable = false)
    private boolean upcoming = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "seo_id")
    private SeoMetadata seo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Set<ContentBlock> contentBlocks = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private Set<SubCategory> subCategories = new LinkedHashSet<>();

}
