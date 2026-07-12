-- ==============================================================================
-- CommerceCore — Create Folders and Media Tables, and Bind Foreign Key Constraints
-- ==============================================================================

-- 1. Folders Schema
CREATE TABLE folders (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id UUID,
    CONSTRAINT fk_folders_parent FOREIGN KEY (parent_id) REFERENCES folders(id) ON DELETE CASCADE
);

-- 2. Media Schema
CREATE TABLE media (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    alt_text VARCHAR(255),
    key VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    size BIGINT,
    mime_type VARCHAR(100),
    folder_id UUID,
    width INTEGER,
    height INTEGER,
    CONSTRAINT uq_media_key UNIQUE (key),
    CONSTRAINT fk_media_folders FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE SET NULL
);

-- 3. Alter existing tables to bind foreign key constraints
ALTER TABLE filter_options
    ADD CONSTRAINT fk_filter_options_media FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE SET NULL;

ALTER TABLE variant_media
    ADD CONSTRAINT fk_variant_media_media FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE;
