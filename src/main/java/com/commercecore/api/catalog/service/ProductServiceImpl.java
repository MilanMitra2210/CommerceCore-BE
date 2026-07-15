package com.commercecore.api.catalog.service;

import com.commercecore.api.catalog.dto.ProductRequest;
import com.commercecore.api.catalog.dto.ProductResponse;
import com.commercecore.api.catalog.dto.ProductVariantDto;
import com.commercecore.api.catalog.dto.VariantAttributeSelectionDto;
import java.util.Optional;
import com.commercecore.api.catalog.entity.Attribute;
import com.commercecore.api.catalog.entity.AttributeValue;
import com.commercecore.api.catalog.entity.Badge;
import com.commercecore.api.catalog.entity.FilterGroup;
import com.commercecore.api.catalog.entity.FilterOption;
import com.commercecore.api.catalog.entity.Product;
import com.commercecore.api.catalog.entity.ProductFilterValue;
import com.commercecore.api.catalog.entity.ProductInfoTab;
import com.commercecore.api.catalog.entity.ProductVariant;
import com.commercecore.api.catalog.entity.VariantAttributeValue;
import com.commercecore.api.catalog.entity.VariantMedia;
import com.commercecore.api.catalog.mapper.ProductMapper;
import com.commercecore.api.catalog.mapper.ProductVariantMapper;
import com.commercecore.api.catalog.repository.AttributeRepository;
import com.commercecore.api.catalog.repository.BadgeRepository;
import com.commercecore.api.catalog.repository.FilterGroupRepository;
import com.commercecore.api.catalog.repository.ProductRepository;
import com.commercecore.api.catalog.repository.ProductVariantRepository;
import com.commercecore.api.category.entity.Category;
import com.commercecore.api.category.entity.SubCategory;
import com.commercecore.api.category.repository.CategoryRepository;
import com.commercecore.api.category.repository.SubCategoryRepository;
import com.commercecore.api.common.entity.ContentBlock;
import com.commercecore.api.common.entity.SeoMetadata;
import com.commercecore.api.media.entity.Media;
import com.commercecore.api.media.repository.MediaRepository;
import com.commercecore.api.common.exception.DuplicateResourceException;
import com.commercecore.api.common.exception.ResourceNotFoundException;
import com.commercecore.api.common.mapper.SeoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BadgeRepository badgeRepository;
    private final AttributeRepository attributeRepository;
    private final FilterGroupRepository filterGroupRepository;
    private final ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final SeoMapper seoMapper;
    private final MediaRepository mediaRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            CategoryRepository categoryRepository,
            SubCategoryRepository subCategoryRepository,
            BadgeRepository badgeRepository,
            AttributeRepository attributeRepository,
            FilterGroupRepository filterGroupRepository,
            ProductMapper productMapper,
            ProductVariantMapper productVariantMapper,
            SeoMapper seoMapper,
            MediaRepository mediaRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.badgeRepository = badgeRepository;
        this.attributeRepository = attributeRepository;
        this.filterGroupRepository = filterGroupRepository;
        this.productMapper = productMapper;
        this.productVariantMapper = productVariantMapper;
        this.seoMapper = seoMapper;
        this.mediaRepository = mediaRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product name={}", request.getName());

        if (productRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Product", "slug", request.getSlug());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", request.getSubCategoryId()));
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSubCategory(subCategory);

        // Persist parent record first to generate the Product ID
        Product savedProduct = productRepository.save(product);

        // Bind parent category_id references on dynamic ContentBlocks
        if (savedProduct.getContentBlocks() != null) {
            for (ContentBlock block : savedProduct.getContentBlocks()) {
                block.setCategoryId(savedProduct.getId()); // Reusing categoryId column as polymorph link
            }
            savedProduct = productRepository.save(savedProduct);
        }

        // Map and save child ProductInfoTabs
        if (request.getInfoTabs() != null) {
            for (var tabDto : request.getInfoTabs()) {
                ProductInfoTab tab = productMapper.toTabEntity(tabDto);
                tab.setProduct(savedProduct);
                savedProduct.getInfoTabs().add(tab);
            }
        }

        // Map and save Reusable Badges
        if (request.getBadgeLabels() != null) {
            for (String label : request.getBadgeLabels()) {
                Badge badge = badgeRepository.findByLabelIgnoreCase(label)
                        .orElseGet(() -> {
                            Badge newBadge = new Badge();
                            newBadge.setLabel(label);
                            return badgeRepository.save(newBadge);
                        });
                savedProduct.getBadges().add(badge);
            }
        }

        // Map and save Product Variants
        if (request.getVariants() != null) {
            log.info("Processing {} variants", request.getVariants().size());
            for (ProductVariantDto varDto : request.getVariants()) {
                log.info("Variant SKU={}, colorName={}, colorHex={}, media={}, attributeSelections={}",
                        varDto.getSku(),
                        varDto.getColorName(),
                        varDto.getColorHex(),
                        varDto.getMedia() != null ? varDto.getMedia().size() : "null",
                        varDto.getAttributeSelections() != null ? varDto.getAttributeSelections().size() : "null");
                if (varDto.getAttributeSelections() != null) {
                    for (var sel : varDto.getAttributeSelections()) {
                        log.info("  Attribute: slug={}, valueSlug={}, value={}, label={}, colorHex={}",
                                sel.getAttributeSlug(), sel.getValueSlug(), sel.getValue(), sel.getLabel(), sel.getColorHex());
                    }
                }
                if (varDto.getMedia() != null) {
                    for (var m : varDto.getMedia()) {
                        log.info("  Media: mediaId={}, role={}, displayOrder={}", m.getMediaId(), m.getRole(), m.getDisplayOrder());
                    }
                }
                if (productVariantRepository.existsBySku(varDto.getSku())) {
                    throw new DuplicateResourceException("ProductVariant", "sku", varDto.getSku());
                }

                ProductVariant variant = productVariantMapper.toEntity(varDto);
                variant.setProduct(savedProduct);

                // Set variant parent references on media list
                if (varDto.getMedia() != null) {
                    for (var mediaDto : varDto.getMedia()) {
                        VariantMedia media = productVariantMapper.toMediaEntity(mediaDto);
                        media.setVariant(variant);
                        if (mediaDto.getMediaId() != null) {
                            media.setMedia(mediaRepository.findById(mediaDto.getMediaId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Media", mediaDto.getMediaId())));
                        }
                        variant.getMedia().add(media);
                    }
                }

                // Bind dynamic AttributeSelection values
                List<VariantAttributeSelectionDto> allSelections = new ArrayList<>();
                if (varDto.getAttributeSelections() != null) {
                    allSelections.addAll(varDto.getAttributeSelections());
                }

                // Append color selection if colorName flat property is present
                if (varDto.getColorName() != null && !varDto.getColorName().trim().isEmpty()) {
                    VariantAttributeSelectionDto colorSel = new VariantAttributeSelectionDto();
                    colorSel.setAttributeSlug("color");
                    colorSel.setAttributeName("Color");
                    colorSel.setValue(varDto.getColorName());
                    colorSel.setLabel(varDto.getColorName());
                    colorSel.setValueSlug(slugify(varDto.getColorName()));
                    colorSel.setColorHex(varDto.getColorHex());
                    allSelections.add(colorSel);
                }

                if (!allSelections.isEmpty()) {
                    for (VariantAttributeSelectionDto selDto : allSelections) {
                        AttributeValue attrVal = resolveOrCreateAttributeValue(
                                selDto.getAttributeSlug(),
                                selDto.getValueSlug(),
                                selDto.getLabel(),
                                selDto.getValue(),
                                selDto.getColorHex()
                        );
                        Attribute attr = attrVal.getAttribute();

                        VariantAttributeValue vav = new VariantAttributeValue();
                        vav.setVariant(variant);
                        vav.setAttribute(attr);
                        vav.setAttributeValue(attrVal);
                        variant.getAttributeValues().add(vav);
                    }
                }

                savedProduct.getVariants().add(variant);
            }
        }

        // Bind Faceted category filters selections
        if (request.getFilterOptionIds() != null) {
            for (UUID optionId : request.getFilterOptionIds()) {
                FilterGroup matchedGroup = filterGroupRepository.findByCategoryIdOrderByDisplayOrderAsc(category.getId()).stream()
                        .filter(g -> g.getOptions().stream().anyMatch(o -> o.getId().equals(optionId)))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("FilterOption", optionId));
                
                FilterOption option = matchedGroup.getOptions().stream()
                        .filter(o -> o.getId().equals(optionId))
                        .findFirst()
                        .get();

                ProductFilterValue pfv = new ProductFilterValue();
                pfv.setProduct(savedProduct);
                pfv.setFilterGroup(matchedGroup);
                pfv.setFilterOption(option);
                savedProduct.getFilterValues().add(pfv);
            }
        }

        Product finalProduct = productRepository.save(savedProduct);
        return mapEntityToResponse(finalProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        log.info("Updating product id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (!product.getSlug().equals(request.getSlug()) && productRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Product", "slug", request.getSlug());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory", request.getSubCategoryId()));
        }

        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setName(request.getName());
        product.setSlug(request.getSlug());
        product.setDescription(request.getDescription());
        product.setBestseller(request.isBestseller());
        product.setFeatured(request.isFeatured());
        product.setActive(request.isActive());
        product.setDisplayOrder(request.getDisplayOrder());

        // Update SEO Metadata
        if (request.getSeo() != null) {
            if (product.getSeo() == null) {
                product.setSeo(seoMapper.toEntity(request.getSeo()));
            } else {
                SeoMetadata seo = product.getSeo();
                seo.setMetaTitle(request.getSeo().getMetaTitle());
                seo.setMetaDescription(request.getSeo().getMetaDescription());
                seo.setMetaKeywords(request.getSeo().getMetaKeywords());
                seo.setMetaRobots(request.getSeo().getMetaRobots());
                seo.setMetaImageId(request.getSeo().getMetaImageId());
            }
        } else {
            product.setSeo(null);
        }

        // Update Content Blocks
        product.getContentBlocks().clear();
        if (request.getContentBlocks() != null) {
            for (Map<String, Object> blockDto : request.getContentBlocks()) {
                ContentBlock block = new ContentBlock();
                block.setBlockKey((String) blockDto.get("code"));
                block.setContent((Map<String, Object>) blockDto.get("block"));
                block.setCategoryId(product.getId()); // Polymorph mapping
                product.getContentBlocks().add(block);
            }
        }

        // Update Specifications InfoTabs
        product.getInfoTabs().clear();
        if (request.getInfoTabs() != null) {
            for (var tabDto : request.getInfoTabs()) {
                ProductInfoTab tab = productMapper.toTabEntity(tabDto);
                tab.setProduct(product);
                product.getInfoTabs().add(tab);
            }
        }

        // Update Badges
        product.getBadges().clear();
        if (request.getBadgeLabels() != null) {
            for (String label : request.getBadgeLabels()) {
                Badge badge = badgeRepository.findByLabelIgnoreCase(label)
                        .orElseGet(() -> {
                            Badge newBadge = new Badge();
                            newBadge.setLabel(label);
                            return badgeRepository.save(newBadge);
                        });
                product.getBadges().add(badge);
            }
        }

        // Update Variants list (merge to avoid unique constraint violations on flush)
        List<ProductVariant> incomingVariants = new ArrayList<>();
        if (request.getVariants() != null) {
            for (ProductVariantDto varDto : request.getVariants()) {
                ProductVariant variant = null;
                // Check if SKU is already taken by another variant
                Optional<ProductVariant> existingWithSku = productVariantRepository.findBySku(varDto.getSku());
                if (existingWithSku.isPresent()) {
                    if (varDto.getId() == null || !existingWithSku.get().getId().equals(varDto.getId())) {
                        throw new DuplicateResourceException("ProductVariant", "sku", varDto.getSku());
                    }
                }

                if (varDto.getSku() != null) {
                    variant = product.getVariants().stream()
                            .filter(v -> varDto.getSku().equals(v.getSku()))
                            .findFirst()
                            .orElse(null);
                }

                if (variant == null) {
                    if (productVariantRepository.existsBySku(varDto.getSku())) {
                        throw new DuplicateResourceException("ProductVariant", "sku", varDto.getSku());
                    }
                    variant = productVariantMapper.toEntity(varDto);
                    variant.setProduct(product);
                } else {
                    // Update existing variant fields
                    variant.setSku(varDto.getSku());
                    variant.setBarcode(varDto.getBarcode());
                    variant.setTitle(varDto.getTitle());
                    variant.setPrice(varDto.getPrice());
                    variant.setCompareAtPrice(varDto.getCompareAtPrice());
                    variant.setCostPrice(varDto.getCostPrice());
                    variant.setCurrencyCode(varDto.getCurrencyCode() != null ? varDto.getCurrencyCode() : "INR");
                    variant.setInventoryQuantity(varDto.getInventoryQuantity());
                    variant.setLowStockThreshold(varDto.getLowStockThreshold());
                    variant.setAllowBackorder(varDto.isAllowBackorder());
                    variant.setWeightValue(varDto.getWeightValue());
                    variant.setWeightUnit(varDto.getWeightUnit());
                    variant.setDefault(varDto.isDefault());
                    variant.setActive(varDto.isActive());
                    variant.setPurchasable(varDto.isPurchasable());
                    variant.setDisplayOrder(varDto.getDisplayOrder());

                    // Dimensions
                    if (varDto.getDimensionLength() != null || varDto.getDimensionWidth() != null || varDto.getDimensionHeight() != null) {
                        if (variant.getDimensions() == null) {
                            variant.setDimensions(new com.commercecore.api.catalog.entity.VariantDimensions());
                        }
                        variant.getDimensions().setLength(varDto.getDimensionLength());
                        variant.getDimensions().setWidth(varDto.getDimensionWidth());
                        variant.getDimensions().setHeight(varDto.getDimensionHeight());
                        variant.getDimensions().setUnit(varDto.getDimensionUnit());
                    } else {
                        variant.setDimensions(null);
                    }
                }

                if (varDto.getMedia() != null) {
                    List<VariantMedia> incomingMedia = new ArrayList<>();
                    for (var mediaDto : varDto.getMedia()) {
                        VariantMedia m = productVariantMapper.toMediaEntity(mediaDto);
                        m.setVariant(variant);
                        if (mediaDto.getMediaId() != null) {
                            m.setMedia(mediaRepository.findById(mediaDto.getMediaId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Media", mediaDto.getMediaId())));
                        }
                        incomingMedia.add(m);
                    }

                    // Remove old ones not in incoming
                    if (variant.getMedia() != null) {
                        variant.getMedia().removeIf(m -> incomingMedia.stream()
                                .noneMatch(im -> im.getMedia().getId().equals(m.getMedia().getId())));
                    }

                    // Add or update incoming ones
                    for (VariantMedia im : incomingMedia) {
                        VariantMedia existing = variant.getMedia().stream()
                                .filter(m -> m.getMedia().getId().equals(im.getMedia().getId()))
                                .findFirst()
                                .orElse(null);

                        if (existing == null) {
                            variant.getMedia().add(im);
                        } else {
                            existing.setDisplayOrder(im.getDisplayOrder());
                            existing.setRole(im.getRole());
                        }
                    }
                }

                // Bind dynamic AttributeSelection values
                List<VariantAttributeSelectionDto> allSelections = new ArrayList<>();
                if (varDto.getAttributeSelections() != null) {
                    allSelections.addAll(varDto.getAttributeSelections());
                }

                // Append color selection if colorName flat property is present
                if (varDto.getColorName() != null && !varDto.getColorName().trim().isEmpty()) {
                    VariantAttributeSelectionDto colorSel = new VariantAttributeSelectionDto();
                    colorSel.setAttributeSlug("color");
                    colorSel.setAttributeName("Color");
                    colorSel.setValue(varDto.getColorName());
                    colorSel.setLabel(varDto.getColorName());
                    colorSel.setValueSlug(slugify(varDto.getColorName()));
                    colorSel.setColorHex(varDto.getColorHex());
                    allSelections.add(colorSel);
                }

                // Remove old attribute values that are not in allSelections
                if (variant.getAttributeValues() != null) {
                    variant.getAttributeValues().removeIf(vav -> allSelections.stream()
                            .noneMatch(sel -> sel.getAttributeSlug().equals(vav.getAttribute().getSlug())));
                }

                // Add or update attribute values
                if (!allSelections.isEmpty()) {
                    for (VariantAttributeSelectionDto selDto : allSelections) {
                        AttributeValue attrVal = resolveOrCreateAttributeValue(
                                selDto.getAttributeSlug(),
                                selDto.getValueSlug(),
                                selDto.getLabel(),
                                selDto.getValue(),
                                selDto.getColorHex()
                        );
                        Attribute attr = attrVal.getAttribute();

                        // Find if it already exists
                        VariantAttributeValue existingVav = variant.getAttributeValues().stream()
                                .filter(vav -> vav.getAttribute().getSlug().equals(selDto.getAttributeSlug()))
                                .findFirst()
                                .orElse(null);

                        if (existingVav == null) {
                            VariantAttributeValue vav = new VariantAttributeValue();
                            vav.setVariant(variant);
                            vav.setAttribute(attr);
                            vav.setAttributeValue(attrVal);
                            variant.getAttributeValues().add(vav);
                        } else {
                            // Update attribute value choice reference
                            existingVav.setAttributeValue(attrVal);
                        }
                    }
                }

                incomingVariants.add(variant);
            }
        }

        // Remove variants that are not present in request
        product.getVariants().removeIf(v -> incomingVariants.stream().noneMatch(iv -> iv.getSku() != null && iv.getSku().equals(v.getSku())));

        // Add new ones
        for (ProductVariant iv : incomingVariants) {
            if (iv.getId() == null || product.getVariants().stream().noneMatch(v -> v.getId().equals(iv.getId()))) {
                product.getVariants().add(iv);
            }
        }

        // Update Filters options
        product.getFilterValues().clear();
        if (request.getFilterOptionIds() != null) {
            for (UUID optionId : request.getFilterOptionIds()) {
                FilterGroup matchedGroup = filterGroupRepository.findByCategoryIdOrderByDisplayOrderAsc(category.getId()).stream()
                        .filter(g -> g.getOptions().stream().anyMatch(o -> o.getId().equals(optionId)))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("FilterOption", optionId));
                
                FilterOption option = matchedGroup.getOptions().stream()
                        .filter(o -> o.getId().equals(optionId))
                        .findFirst()
                        .get();

                ProductFilterValue pfv = new ProductFilterValue();
                pfv.setProduct(product);
                pfv.setFilterGroup(matchedGroup);
                pfv.setFilterOption(option);
                product.getFilterValues().add(pfv);
            }
        }

        Product updated = productRepository.save(product);
        return mapEntityToResponse(updated);
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapEntityToResponse(product);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return mapEntityToResponse(product);
    }

    @Override
    public Page<ProductResponse> getProducts(String search, Pageable pageable) {
        Page<Product> page;
        if (search != null && !search.trim().isEmpty()) {
            page = productRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        return page.map(this::mapEntityToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapEntityToResponse);
    }

    @Override
    public Page<ProductResponse> getProductsBySubCategory(UUID subCategoryId, Pageable pageable) {
        return productRepository.findBySubCategoryId(subCategoryId, pageable)
                .map(this::mapEntityToResponse);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Soft deleting product id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
    }

    // ---- Helper to resolve dynamic attributes selections in response ----

    private ProductResponse mapEntityToResponse(Product product) {
        ProductResponse response = productMapper.toResponse(product);
        if (product.getVariants() != null && response.getVariants() != null) {
            List<ProductVariant> variants = new ArrayList<>(product.getVariants());
            List<ProductVariantDto> variantDtos = response.getVariants();

            for (ProductVariantDto dto : variantDtos) {
                ProductVariant entity = variants.stream()
                        .filter(v -> v.getSku() != null && v.getSku().equals(dto.getSku()))
                        .findFirst()
                        .orElse(null);

                if (entity != null && entity.getAttributeValues() != null) {
                    List<VariantAttributeSelectionDto> attributeSelections = entity.getAttributeValues().stream()
                            .map(v -> {
                                VariantAttributeSelectionDto sel = new VariantAttributeSelectionDto();
                                sel.setAttributeSlug(v.getAttribute().getSlug());
                                sel.setAttributeName(v.getAttribute().getName());
                                sel.setValue(v.getAttributeValue().getValue());
                                sel.setLabel(v.getAttributeValue().getLabel());
                                sel.setValueSlug(v.getAttributeValue().getSlug());
                                sel.setColorHex(v.getAttributeValue().getColorHex());
                                sel.setSortValue(v.getAttributeValue().getSortValue());
                                return sel;
                            }).collect(Collectors.toList());
                    dto.setAttributeSelections(attributeSelections);

                    // Populate flat colorName and colorHex for the frontend if present in attributes
                    for (VariantAttributeValue vav : entity.getAttributeValues()) {
                        if ("color".equals(vav.getAttribute().getSlug())) {
                            dto.setColorName(vav.getAttributeValue().getValue());
                            dto.setColorHex(vav.getAttributeValue().getColorHex());
                        }
                    }
                }
            }
        }
        return response;
    }

    private String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    private AttributeValue resolveOrCreateAttributeValue(String attrSlug, String valueSlug, String label, String value, String colorHex) {
        Attribute attr = attributeRepository.findBySlug(attrSlug)
                .orElseGet(() -> {
                    Attribute newAttr = new Attribute();
                    newAttr.setSlug(attrSlug);
                    String name = attrSlug.substring(0, 1).toUpperCase() + attrSlug.substring(1);
                    newAttr.setName(name);
                    newAttr.setDataType("text");
                    newAttr.setInputType("select");
                    newAttr.setVariantDefining(true);
                    newAttr.setFilterable(true);
                    return attributeRepository.save(newAttr);
                });

        AttributeValue attrVal = attr.getValues().stream()
                .filter(v -> v.getSlug().equals(valueSlug))
                .findFirst()
                .orElse(null);

        if (attrVal == null) {
            attrVal = new AttributeValue();
            attrVal.setAttribute(attr);
            attrVal.setSlug(valueSlug);
            attrVal.setLabel(label != null ? label : valueSlug.toUpperCase());
            attrVal.setValue(value != null ? value : valueSlug);
            attrVal.setColorHex(colorHex);
            attrVal.setDisplayOrder(0);
            attr.getValues().add(attrVal);
            attributeRepository.save(attr);

            // Re-fetch to get populated id if generated on save
            attr = attributeRepository.findBySlug(attrSlug).orElse(attr);
            attrVal = attr.getValues().stream()
                    .filter(v -> v.getSlug().equals(valueSlug))
                    .findFirst()
                    .orElse(attrVal);
        } else if (colorHex != null && !colorHex.equals(attrVal.getColorHex())) {
            attrVal.setColorHex(colorHex);
            attributeRepository.save(attr);
        }

        return attrVal;
    }

}
