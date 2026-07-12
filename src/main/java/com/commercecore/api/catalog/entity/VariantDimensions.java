package com.commercecore.api.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Embeddable value object mapping physical shipping dimensions for product packaging.
 */
@Embeddable
@Getter
@Setter
public class VariantDimensions {

    @Column(name = "dimension_length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "dimension_width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "dimension_height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "dimension_unit", length = 20)
    private String unit;

}
