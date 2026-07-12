package com.commercecore.api.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Junction entity mapping products to configured category faceted filter options.
 */
@Entity
@Table(name = "product_filter_values")
@Getter
@Setter
public class ProductFilterValue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_group_id", nullable = false)
    private FilterGroup filterGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_option_id", nullable = false)
    private FilterOption filterOption;

}
