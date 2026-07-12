-- ==============================================================================
-- CommerceCore — Create Products, Variants, Attributes, and Filter System Tables
-- ==============================================================================

-- 1. Dynamic Filters Schema
CREATE TABLE filter_groups (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    filter_key VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL,
    is_single_select BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uq_filter_group_key_per_scope UNIQUE (category_id, filter_key),
    CONSTRAINT fk_filter_groups_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE filter_options (
    id UUID PRIMARY KEY,
    filter_group_id UUID NOT NULL,
    media_id UUID, -- No FK constraint yet as media table is not created
    label VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    sub_group VARCHAR(100),
    color_code VARCHAR(50),
    display_order INTEGER NOT NULL DEFAULT 0,
    show_in_navbar BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_filter_options_groups FOREIGN KEY (filter_group_id) REFERENCES filter_groups(id) ON DELETE CASCADE
);

-- 2. Dynamic Attribute Schema
CREATE TABLE attributes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    data_type VARCHAR(30) NOT NULL DEFAULT 'text',
    input_type VARCHAR(30) NOT NULL DEFAULT 'select',
    unit VARCHAR(50),
    is_variant_defining BOOLEAN NOT NULL DEFAULT TRUE,
    is_filterable BOOLEAN NOT NULL DEFAULT TRUE,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    CONSTRAINT uq_attribute_slug UNIQUE (slug)
);

CREATE TABLE attribute_values (
    id UUID PRIMARY KEY,
    attribute_id UUID NOT NULL,
    value VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    color_hex VARCHAR(7),
    sort_value NUMERIC(12,3),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uq_attribute_value_slug UNIQUE (attribute_id, slug),
    CONSTRAINT fk_attribute_values_attributes FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE
);

-- 3. Product & Variant Schema
CREATE TABLE products (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    sub_category_id UUID,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    is_bestseller BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    seo_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_products_sub_categories FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_products_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL
);

CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    barcode VARCHAR(100),
    title VARCHAR(255),
    price NUMERIC(12, 2) NOT NULL,
    compare_at_price NUMERIC(12, 2),
    cost_price NUMERIC(12, 2),
    currency_code VARCHAR(3) NOT NULL DEFAULT 'INR',
    inventory_quantity INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER NOT NULL DEFAULT 0,
    allow_backorder BOOLEAN NOT NULL DEFAULT FALSE,
    weight_value NUMERIC(10, 3),
    weight_unit VARCHAR(20),
    dimension_length NUMERIC(10, 2),
    dimension_width NUMERIC(10, 2),
    dimension_height NUMERIC(10, 2),
    dimension_unit VARCHAR(20),
    info_tabs JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_purchasable BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_product_variant_sku UNIQUE (sku),
    CONSTRAINT uq_product_variant_sku_per_product UNIQUE (product_id, sku),
    CONSTRAINT fk_product_variants_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 4. Reusable Badges Schema
CREATE TABLE badges (
    id UUID PRIMARY KEY,
    label VARCHAR(100) NOT NULL,
    color_code VARCHAR(50),
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uq_badges_label UNIQUE (label)
);

CREATE TABLE product_badges (
    product_id UUID NOT NULL,
    badge_id UUID NOT NULL,
    PRIMARY KEY (product_id, badge_id),
    CONSTRAINT fk_product_badges_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_badges_badges FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE
);

-- 5. Junction & Specification Associations
CREATE TABLE product_info_tabs (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_info_tabs_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE variant_attribute_values (
    id UUID PRIMARY KEY,
    variant_id UUID NOT NULL,
    attribute_id UUID NOT NULL,
    attribute_value_id UUID NOT NULL,
    CONSTRAINT uq_variant_attribute UNIQUE (variant_id, attribute_id),
    CONSTRAINT uq_variant_attribute_value UNIQUE (variant_id, attribute_value_id),
    CONSTRAINT fk_variant_attr_variants FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    CONSTRAINT fk_variant_attr_attributes FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE,
    CONSTRAINT fk_variant_attr_values FOREIGN KEY (attribute_value_id) REFERENCES attribute_values(id) ON DELETE CASCADE
);

CREATE TABLE product_filter_values (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    filter_group_id UUID NOT NULL,
    filter_option_id UUID NOT NULL,
    CONSTRAINT uq_product_filter_value UNIQUE (product_id, filter_group_id, filter_option_id),
    CONSTRAINT fk_prod_filters_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_filters_groups FOREIGN KEY (filter_group_id) REFERENCES filter_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_filters_options FOREIGN KEY (filter_option_id) REFERENCES filter_options(id) ON DELETE CASCADE
);

CREATE TABLE variant_media (
    id UUID PRIMARY KEY,
    variant_id UUID NOT NULL,
    media_id UUID NOT NULL, -- No FK constraint yet as media table is not created
    role VARCHAR(50) NOT NULL DEFAULT 'gallery',
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_variant_media_variants FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

-- 6. Indices for Search & Facet Optimization
CREATE INDEX idx_products_listing ON products(deleted, is_active, category_id, display_order);
CREATE INDEX idx_products_sub_category ON products(sub_category_id);
CREATE INDEX idx_product_variants_listing ON product_variants(product_id, is_active, is_purchasable, is_default);
CREATE INDEX idx_product_variants_price ON product_variants(price);
CREATE INDEX idx_variant_attribute_value_filter ON variant_attribute_values(attribute_id, attribute_value_id);
CREATE INDEX idx_product_filter_values_lookup ON product_filter_values(product_id, filter_group_id, filter_option_id);
CREATE INDEX idx_variant_media_order ON variant_media(variant_id, role, display_order);
