-- ==============================================================================
-- CommerceCore — Create SEO, Content Blocks, and Categories Tables
-- ==============================================================================

CREATE TABLE seo_metadata (
    id UUID PRIMARY KEY,
    meta_title VARCHAR(255),
    meta_description TEXT,
    meta_keywords VARCHAR(512),
    meta_robots VARCHAR(100),
    meta_image_id UUID -- Will reference media.id in the future
);

CREATE TABLE content_blocks (
    id UUID PRIMARY KEY,
    blocks JSONB NOT NULL DEFAULT '[]'::jsonb
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
    content_blocks_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL,
    CONSTRAINT fk_categories_content_blocks FOREIGN KEY (content_blocks_id) REFERENCES content_blocks(id) ON DELETE SET NULL
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
    content_blocks_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_sub_categories_slug UNIQUE (slug),
    CONSTRAINT fk_sub_categories_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_categories_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL,
    CONSTRAINT fk_sub_categories_content_blocks FOREIGN KEY (content_blocks_id) REFERENCES content_blocks(id) ON DELETE SET NULL
);

-- Indexing for taxonomic lookup performance
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_sub_categories_slug ON sub_categories(slug);
CREATE INDEX idx_sub_categories_category ON sub_categories(category_id);
