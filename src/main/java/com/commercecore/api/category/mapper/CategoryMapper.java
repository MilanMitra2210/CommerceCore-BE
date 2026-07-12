package com.commercecore.api.category.mapper;

import com.commercecore.api.category.dto.CategoryRequest;
import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.entity.Category;
import com.commercecore.api.category.entity.SubCategory;
import com.commercecore.api.common.mapper.ContentBlockMapper;
import com.commercecore.api.common.mapper.SeoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct interface to automatically map Category and SubCategory entities.
 *
 * <p>Uses {@link SeoMapper} and {@link ContentBlockMapper} to delegate nested conversions.
 */
@Mapper(componentModel = "spring", uses = {SeoMapper.class, ContentBlockMapper.class})
public interface CategoryMapper {

    // ---- Category Mapping ----

    CategoryResponse toResponse(Category category);

    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryRequest request);

    // ---- SubCategory Mapping ----

    @Mapping(source = "category.id", target = "categoryId")
    SubCategoryResponse toResponse(SubCategory subCategory);

    @Mapping(target = "category", ignore = true)
    SubCategory toEntity(SubCategoryRequest request);

}
