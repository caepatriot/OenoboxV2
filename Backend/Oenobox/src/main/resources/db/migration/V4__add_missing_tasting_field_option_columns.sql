-- Align tasting_field_option table with current JPA entity mapping.

ALTER TABLE tasting_field_option
    ADD COLUMN IF NOT EXISTS wine_type_restriction JSONB;

ALTER TABLE tasting_field_option
    ADD COLUMN IF NOT EXISTS group_index SMALLINT;

ALTER TABLE tasting_field_option
    ADD COLUMN IF NOT EXISTS group_required BOOLEAN DEFAULT FALSE;
