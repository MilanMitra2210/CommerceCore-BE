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
 * Entity representing an individual selectable choice option within a FilterGroup.
 */
@Entity
@Table(name = "filter_options")
@Getter
@Setter
public class FilterOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_group_id", nullable = false)
    private FilterGroup filterGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    private com.commercecore.api.media.entity.Media media;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    @Column(name = "sub_group", length = 100)
    private String subGroup;

    @Column(name = "color_code", length = 50)
    private String colorCode;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "show_in_navbar", nullable = false)
    private boolean showInNavbar = false;

}
