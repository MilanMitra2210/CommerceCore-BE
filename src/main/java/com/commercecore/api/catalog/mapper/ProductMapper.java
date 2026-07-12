package com.commercecore.api.catalog.mapper;

import com.commercecore.api.catalog.dto.BadgeDto;
import com.commercecore.api.catalog.dto.ProductInfoTabDto;
import com.commercecore.api.catalog.dto.ProductRequest;
import com.commercecore.api.catalog.dto.ProductResponse;
import com.commercecore.api.catalog.dto.ProductVariantDto;
import com.commercecore.api.catalog.entity.Badge;
import com.commercecore.api.catalog.entity.Product;
import com.commercecore.api.catalog.entity.ProductInfoTab;
import com.commercecore.api.catalog.entity.ProductVariant;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.mapper.SeoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * MapStruct interface to automatically convert Product aggregates.
 */
@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, SeoMapper.class})
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "subCategory.id", target = "subCategoryId")
    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapBlocksToDto")
    @Mapping(target = "sku", source = "product", qualifiedByName = "calculateSku")
    @Mapping(target = "price", source = "product", qualifiedByName = "calculatePrice")
    @Mapping(target = "compareAtPrice", source = "product", qualifiedByName = "calculateCompareAtPrice")
    @Mapping(target = "inventory", source = "product", qualifiedByName = "calculateInventory")
    @Mapping(target = "discount", source = "product", qualifiedByName = "calculateDiscount")
    @Mapping(target = "discountType", source = "product", qualifiedByName = "calculateDiscountType")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "seo", source = "seo")
    @Mapping(target = "contentBlocks", source = "contentBlocks", qualifiedByName = "mapDtoToBlocks")
    @Mapping(target = "variants", ignore = true) // Handled in Service
    @Mapping(target = "badges", ignore = true)   // Handled in Service
    @Mapping(target = "infoTabs", ignore = true)  // Handled in Service
    @Mapping(target = "filterValues", ignore = true)
    Product toEntity(ProductRequest request);

    // Badge mapping helpers
    BadgeDto toBadgeDto(Badge badge);

    // Info tab mapping helpers
    ProductInfoTabDto toTabDto(ProductInfoTab tab);

    @Mapping(target = "product", ignore = true)
    ProductInfoTab toTabEntity(ProductInfoTabDto dto);

    // ---- ContentBlock JSONB Conversion Helpers ----

    @Named("mapBlocksToDto")
    default List<Map<String, Object>> mapBlocksToDto(Set<ContentBlock> blocks) {
        if (blocks == null) {
            return Collections.emptyList();
        }
        return blocks.stream()
                .map(b -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", b.getId() != null ? b.getId().toString() : null);
                    map.put("code", b.getBlockKey());
                    map.put("block", b.getContent());
                    return map;
                }).toList();
    }

    @Named("mapDtoToBlocks")
    default Set<ContentBlock> mapDtoToBlocks(List<Map<String, Object>> blocksDto) {
        if (blocksDto == null) {
            return Collections.emptySet();
        }
        Set<ContentBlock> set = new LinkedHashSet<>();
        for (Map<String, Object> map : blocksDto) {
            ContentBlock block = new ContentBlock();
            if (map.get("id") != null) {
                block.setId(UUID.fromString(map.get("id").toString()));
            }
            block.setBlockKey((String) map.get("code"));
            block.setContent((Map<String, Object>) map.get("block"));
            set.add(block);
        }
        return set;
    }

    // ---- Computed Property Mapping Helpers ----

    @Named("calculateSku")
    default String calculateSku(Product product) {
        ProductVariant variant = getDefaultVariant(product);
        return variant != null ? variant.getSku() : null;
    }

    @Named("calculatePrice")
    default BigDecimal calculatePrice(Product product) {
        ProductVariant variant = getDefaultVariant(product);
        return variant != null ? variant.getPrice() : BigDecimal.ZERO;
    }

    @Named("calculateCompareAtPrice")
    default BigDecimal calculateCompareAtPrice(Product product) {
        ProductVariant variant = getDefaultVariant(product);
        return variant != null ? variant.getCompareAtPrice() : null;
    }

    @Named("calculateInventory")
    default int calculateInventory(Product product) {
        if (product.getVariants() == null) {
            return 0;
        }
        return product.getVariants().stream()
                .mapToInt(ProductVariant::getInventoryQuantity)
                .sum();
    }

    @Named("calculateDiscount")
    default BigDecimal calculateDiscount(Product product) {
        ProductVariant variant = getDefaultVariant(product);
        if (variant == null || variant.getCompareAtPrice() == null || variant.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = variant.getCompareAtPrice().subtract(variant.getPrice());
        return diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
    }

    @Named("calculateDiscountType")
    default String calculateDiscountType(Product product) {
        BigDecimal discount = calculateDiscount(product);
        return discount.compareTo(BigDecimal.ZERO) > 0 ? "flat" : "none";
    }

    default ProductVariant getDefaultVariant(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            return null;
        }
        return product.getVariants().stream()
                .filter(ProductVariant::isDefault)
                .findFirst()
                .orElse(product.getVariants().iterator().next());
    }

}
