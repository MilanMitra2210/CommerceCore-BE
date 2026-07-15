-- Drop standard unique constraints on soft-deleted tables and convert them to partial unique indexes
ALTER TABLE products DROP CONSTRAINT uq_products_slug;
CREATE UNIQUE INDEX uq_products_slug ON products (slug) WHERE (deleted = false);

ALTER TABLE product_variants DROP CONSTRAINT uq_product_variant_sku;
CREATE UNIQUE INDEX uq_product_variant_sku ON product_variants (sku) WHERE (deleted = false);

ALTER TABLE product_variants DROP CONSTRAINT uq_product_variant_sku_per_product;
CREATE UNIQUE INDEX uq_product_variant_sku_per_product ON product_variants (product_id, sku) WHERE (deleted = false);
