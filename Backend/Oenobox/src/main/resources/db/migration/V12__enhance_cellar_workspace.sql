ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS template_key VARCHAR(100);
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS layout_mode VARCHAR(30) DEFAULT 'formula';
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS row_count INTEGER;
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS column_count INTEGER;
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS default_space_capacity INTEGER DEFAULT 1;
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0;
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS preferred_wine_group VARCHAR(100);
ALTER TABLE storage_unit ADD COLUMN IF NOT EXISTS notes TEXT;

ALTER TABLE space ADD COLUMN IF NOT EXISTS slot_index INTEGER;
ALTER TABLE space ADD COLUMN IF NOT EXISTS slot_label VARCHAR(100);
ALTER TABLE space ADD COLUMN IF NOT EXISTS preferred_wine_group VARCHAR(100);
ALTER TABLE space ADD COLUMN IF NOT EXISTS section_name VARCHAR(100);
ALTER TABLE space ADD COLUMN IF NOT EXISTS disabled BOOLEAN DEFAULT FALSE;
ALTER TABLE space ADD COLUMN IF NOT EXISTS notes TEXT;

ALTER TABLE wine ADD COLUMN IF NOT EXISTS producer VARCHAR(255);
ALTER TABLE wine ADD COLUMN IF NOT EXISTS country VARCHAR(255);
ALTER TABLE wine ADD COLUMN IF NOT EXISTS appellation VARCHAR(255);
ALTER TABLE wine ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE wine ADD COLUMN IF NOT EXISTS image_url TEXT;

UPDATE storage_unit
SET default_space_capacity = COALESCE(default_space_capacity, 1),
    display_order = COALESCE(display_order, 0),
    layout_mode = COALESCE(layout_mode, 'formula');

UPDATE space
SET disabled = COALESCE(disabled, FALSE),
    slot_index = COALESCE(slot_index, ((COALESCE(pos_row, 1) - 1) * 100) + COALESCE(pos_column, 1)),
    slot_label = COALESCE(slot_label, CONCAT('R', COALESCE(pos_row, 1), '-S', COALESCE(pos_column, 1)));

CREATE INDEX IF NOT EXISTS idx_storage_unit_cave_display_order ON storage_unit(cave_id, display_order);
CREATE INDEX IF NOT EXISTS idx_space_unit_slot_index ON space(unit_id, slot_index);
CREATE INDEX IF NOT EXISTS idx_space_preferred_wine_group ON space(preferred_wine_group);
