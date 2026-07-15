-- ==============================================================================
-- CommerceCore — Create CMS Pages Table and Associate Content Blocks
-- ==============================================================================

CREATE TABLE pages (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    seo_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_pages_slug UNIQUE (slug),
    CONSTRAINT fk_pages_seo FOREIGN KEY (seo_id) REFERENCES seo_metadata(id) ON DELETE SET NULL
);

ALTER TABLE content_blocks ADD COLUMN page_id UUID;
ALTER TABLE content_blocks ADD CONSTRAINT fk_content_blocks_pages FOREIGN KEY (page_id) REFERENCES pages(id) ON DELETE CASCADE;

CREATE INDEX idx_pages_slug ON pages(slug);
CREATE INDEX idx_content_blocks_page ON content_blocks(page_id);
