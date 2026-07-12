package com.commercecore.api.catalog.mapper;

import com.commercecore.api.catalog.dto.ProductVariantDto;
import com.commercecore.api.catalog.dto.VariantMediaDto;
import com.commercecore.api.catalog.entity.ProductVariant;
import com.commercecore.api.catalog.entity.VariantMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct interface to automatically map ProductVariant entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "dimensionLength", source = "dimensions.length")
    @Mapping(target = "dimensionWidth", source = "dimensions.width")
    @Mapping(target = "dimensionHeight", source = "dimensions.height")
    @Mapping(target = "dimensionUnit", source = "dimensions.unit")
    @Mapping(target = "attributeSelections", ignore = true) // Resolved manually in service layer
    ProductVariantDto toDto(ProductVariant variant);

    @Mapping(target = "dimensions.length", source = "dimensionLength")
    @Mapping(target = "dimensions.width", source = "dimensionWidth")
    @Mapping(target = "dimensions.height", source = "dimensionHeight")
    @Mapping(target = "dimensions.unit", source = "dimensionUnit")
    @Mapping(target = "attributeValues", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariant toEntity(ProductVariantDto dto);

    // Media mapping helpers
    VariantMediaDto toMediaDto(VariantMedia media);

    @Mapping(target = "variant", ignore = true)
    VariantMedia toMediaEntity(VariantMediaDto dto);

}
