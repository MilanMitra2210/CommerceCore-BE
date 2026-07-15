-- ==============================================================================
-- CommerceCore — Add Subcategory Column to Filter Groups Table
-- ==============================================================================

ALTER TABLE filter_groups ADD COLUMN sub_category_id UUID REFERENCES sub_categories(id) ON DELETE CASCADE;
CREATE INDEX idx_filter_groups_subcategory ON filter_groups(sub_category_id);
