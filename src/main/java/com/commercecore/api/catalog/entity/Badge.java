package com.commercecore.api.catalog.entity;

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
 * Entity representing dynamic global reusable badges.
 */
@Entity
@Table(name = "badges")
@Getter
@Setter
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String label;

    @Column(name = "color_code", length = 50)
    private String colorCode;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

}
