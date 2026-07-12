package com.commercecore.api.category.entity;

import com.commercecore.api.common.entity.BaseEntity;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.entity.SeoMetadata;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entity representing a sub-category taxonomy belonging to a parent Category.
 */
@Entity
@Table(name = "sub_categories")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class SubCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "seo_id")
    private SeoMetadata seo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Set<ContentBlock> contentBlocks = new LinkedHashSet<>();

}
