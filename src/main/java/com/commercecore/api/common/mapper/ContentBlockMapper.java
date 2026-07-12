package com.commercecore.api.common.mapper;

import com.commercecore.api.common.dto.ContentBlockDto;
import com.commercecore.api.common.entity.ContentBlock;
import org.mapstruct.Mapper;

/**
 * MapStruct interface to convert ContentBlock entities.
 */
@Mapper(componentModel = "spring")
public interface ContentBlockMapper {

    ContentBlockDto toDto(ContentBlock block);

    ContentBlock toEntity(ContentBlockDto dto);

}
