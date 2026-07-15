package com.commercecore.api.catalog.mapper;

import com.commercecore.api.catalog.dto.FilterGroupRequest;
import com.commercecore.api.catalog.dto.FilterGroupResponse;
import com.commercecore.api.catalog.dto.FilterOptionRequest;
import com.commercecore.api.catalog.dto.FilterOptionResponse;
import com.commercecore.api.catalog.entity.FilterGroup;
import com.commercecore.api.catalog.entity.FilterOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface FilterGroupMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "subCategory.id", target = "subCategoryId")
    @Mapping(source = "subCategory.name", target = "subCategoryName")
    @Mapping(source = "options", target = "optionsCount", qualifiedByName = "calculateOptionsCount")
    FilterGroupResponse toResponse(FilterGroup entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "options", ignore = true) // Handle options mapping manually in service for full control
    FilterGroup toEntity(FilterGroupRequest request);

    @Mapping(source = "media.id", target = "mediaId")
    FilterOptionResponse toResponse(FilterOption entity);

    @Mapping(target = "filterGroup", ignore = true)
    @Mapping(target = "media", ignore = true) // Handle media relation manually in service
    FilterOption toEntity(FilterOptionRequest request);

    @Named("calculateOptionsCount")
    default int calculateOptionsCount(Collection<FilterOption> options) {
        return options != null ? options.size() : 0;
    }

}
