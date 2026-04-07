-- Extend shared wine catalog with richer descriptive fields used by the wines module.
-- All fields are nullable to preserve backward compatibility for existing rows.

ALTER TABLE wine
    ADD COLUMN IF NOT EXISTS producer VARCHAR(255),
    ADD COLUMN IF NOT EXISTS country VARCHAR(255),
    ADD COLUMN IF NOT EXISTS appellation VARCHAR(255),
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS aroma_notes JSONB DEFAULT '[]'::jsonb,
    ADD COLUMN IF NOT EXISTS food_pairings JSONB DEFAULT '[]'::jsonb,
    ADD COLUMN IF NOT EXISTS image_url TEXT,
    ADD COLUMN IF NOT EXISTS data_source VARCHAR(100),
    ADD COLUMN IF NOT EXISTS external_id VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_wine_name_lower ON wine (LOWER(name));
CREATE INDEX IF NOT EXISTS idx_wine_region_lower ON wine (LOWER(region));
CREATE INDEX IF NOT EXISTS idx_wine_external_id ON wine (external_id);
