-- ==============================================================================
-- CommerceCore — Create SEO, Categories, and Content Blocks Tables
-- ==============================================================================

CREATE TABLE seo_metadata (
    id UUID PRIMARY KEY,
    meta_title VARCHAR(255),
    meta_description TEXT,
    meta_keywords VARCHAR(512),
    meta_robots VARCHAR(100),
    meta_image_id UUID -- Will reference media.id in the future
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_upcoming BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    seo_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL
);

CREATE TABLE sub_categories (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    seo_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_sub_categories_slug UNIQUE (slug),
    CONSTRAINT fk_sub_categories_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_categories_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL
);

CREATE TABLE content_blocks (
    id UUID PRIMARY KEY,
    block_key VARCHAR(100) NOT NULL,
    content JSONB NOT NULL,
    category_id UUID,
    subcategory_id UUID,
    CONSTRAINT fk_content_blocks_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_blocks_sub_categories FOREIGN KEY (subcategory_id) REFERENCES sub_categories(id) ON DELETE CASCADE
);

-- Indexing for taxonomic lookup performance
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_sub_categories_slug ON sub_categories(slug);
CREATE INDEX idx_sub_categories_category ON sub_categories(category_id);
CREATE INDEX idx_content_blocks_category ON content_blocks(category_id);
CREATE INDEX idx_content_blocks_subcategory ON content_blocks(subcategory_id);
