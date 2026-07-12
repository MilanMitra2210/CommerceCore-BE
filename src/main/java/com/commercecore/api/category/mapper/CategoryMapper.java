package com.commercecore.api.category.mapper;

import com.commercecore.api.category.dto.CategoryRequest;
import com.commercecore.api.category.dto.CategoryResponse;
import com.commercecore.api.category.dto.SubCategoryRequest;
import com.commercecore.api.category.dto.SubCategoryResponse;
import com.commercecore.api.category.entity.Category;
import com.commercecore.api.category.entity.SubCategory;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.mapper.SeoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MapStruct interface to automatically map Category and SubCategory entities.
 *
 * <p>Uses {@link SeoMapper} to handle nested SEO conversions.
 */
@Mapper(componentModel = "spring", uses = {SeoMapper.class})
public interface CategoryMapper {

    // ---- Category Mapping ----

    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapContentBlocksToDto")
    CategoryResponse toResponse(Category category);

    @Mapping(target = "seo", source = "seo")
    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapDtoToContentBlocks")
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryRequest request);

    // ---- SubCategory Mapping ----

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapContentBlocksToDto")
    SubCategoryResponse toResponse(SubCategory subCategory);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "seo", source = "seo")
    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapDtoToContentBlocks")
    SubCategory toEntity(SubCategoryRequest request);

    // ---- Custom JSONB Content Block Mapping Helpers ----

    @Named("mapContentBlocksToDto")
    default List<Map<String, Object>> mapContentBlocksToDto(ContentBlock contentBlock) {
        if (contentBlock == null) {
            return new ArrayList<>();
        }
        return contentBlock.getBlocks();
    }

    @Named("mapDtoToContentBlocks")
    default ContentBlock mapDtoToContentBlocks(List<Map<String, Object>> blocksDto) {
        if (blocksDto == null) {
            return null;
        }
        ContentBlock contentBlock = new ContentBlock();
        contentBlock.setBlocks(blocksDto);
        return contentBlock;
    }

}
