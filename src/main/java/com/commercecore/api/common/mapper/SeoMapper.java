package com.commercecore.api.common.mapper;

import com.commercecore.api.common.dto.SeoDto;
import com.commercecore.api.common.entity.SeoMetadata;
import org.mapstruct.Mapper;

/**
 * MapStruct interface to convert SeoMetadata Entities to DTOs and vice versa.
 */
@Mapper(componentModel = "spring")
public interface SeoMapper {

    SeoDto toDto(SeoMetadata seo);

    SeoMetadata toEntity(SeoDto dto);

}
