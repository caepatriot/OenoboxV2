-- PostgreSQL Schema Creation for Oenobox Wine Tasting Application
-- This script creates all the necessary tables for the tasting form

-- ===========================================
-- WINE TYPES TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS wine_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    color_code VARCHAR(7),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- CEPAGES (GRAPE VARIETIES) TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS cepage (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    wine_type_id BIGINT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wine_type_id) REFERENCES wine_type(id) ON DELETE CASCADE,
    UNIQUE(name, wine_type_id)
);

-- ===========================================
-- TASTING STEPS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS tasting_step (
    id BIGSERIAL PRIMARY KEY,
    step_number INTEGER NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- TASTING FIELDS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS tasting_field (
    id BIGSERIAL PRIMARY KEY,
    step_id BIGINT NOT NULL,
    field_type VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    placeholder TEXT,
    required BOOLEAN DEFAULT FALSE,
    multi_select BOOLEAN DEFAULT FALSE,
    wine_type_restriction JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES tasting_step(id) ON DELETE CASCADE,
    UNIQUE(step_id, name)
);

-- ===========================================
-- TASTING FIELD OPTIONS TABLE
-- ===========================================
CREATE TABLE IF NOT EXISTS tasting_field_option (
    id BIGSERIAL PRIMARY KEY,
    field_id BIGINT NOT NULL,
    value VARCHAR(100) NOT NULL,
    label VARCHAR(100) NOT NULL,
    color_code VARCHAR(7),
    is_negative BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (field_id) REFERENCES tasting_field(id) ON DELETE CASCADE,
    UNIQUE(field_id, value)
);

-- ===========================================
-- AROMA NOTES TABLE (Sub-options for aroma categories)
-- ===========================================
CREATE TABLE IF NOT EXISTS aroma_note (
    id BIGSERIAL PRIMARY KEY,
    category_option_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_option_id) REFERENCES tasting_field_option(id) ON DELETE CASCADE,
    UNIQUE(category_option_id, name)
);

-- ===========================================
-- TASTING RECORDS TABLE (Main tasting data)
-- ===========================================
CREATE TABLE IF NOT EXISTS tasting (
    id BIGSERIAL PRIMARY KEY,
    wine_name VARCHAR(255),
    wine_type_id BIGINT,
    cepage_ids JSONB, -- Array of cepage IDs
    region VARCHAR(255),
    aop_igp_vdf VARCHAR(255),
    elevage TEXT,
    import_country VARCHAR(100),
    launch_price DECIMAL(10,2),
    current_price DECIMAL(10,2),

    -- Visual step
    robe_red JSONB, -- Selected options
    robe_white JSONB,
    disque JSONB,
    robe_rose JSONB,
    intensity JSONB,
    limpidity JSONB,
    brilliance JSONB,
    evolution JSONB,
    visual_remarks TEXT,

    -- Nose step
    nose_intensity JSONB,
    nose_quality JSONB,
    aroma_type JSONB,
    aroma_nature JSONB, -- Selected categories and their notes
    nose_remarks TEXT,

    -- Mouth step
    mouth_attack JSONB,
    mouth_evolution JSONB,
    mouth_structure JSONB,
    mouth_texture JSONB,

    -- Length step
    persistence_aromatic DECIMAL(3,1), -- Slider value 0-100
    caudalies JSONB,
    length_structure JSONB,

    -- Conclusion step
    final_note JSONB,
    caudalies_count INTEGER,
    conclusion_remarks TEXT,
    food_pairings TEXT,

    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),

    FOREIGN KEY (wine_type_id) REFERENCES wine_type(id)
);

-- ===========================================
-- INDEXES FOR PERFORMANCE
-- ===========================================
CREATE INDEX IF NOT EXISTS idx_cepage_wine_type ON cepage(wine_type_id);
CREATE INDEX IF NOT EXISTS idx_tasting_field_step ON tasting_field(step_id);
CREATE INDEX IF NOT EXISTS idx_tasting_field_option_field ON tasting_field_option(field_id);
CREATE INDEX IF NOT EXISTS idx_aroma_note_category ON aroma_note(category_option_id);
CREATE INDEX IF NOT EXISTS idx_tasting_wine_type ON tasting(wine_type_id);
CREATE INDEX IF NOT EXISTS idx_tasting_created_at ON tasting(created_at);
CREATE INDEX IF NOT EXISTS idx_tasting_created_by ON tasting(created_by);

-- ===========================================
-- TRIGGERS FOR UPDATED_AT
-- ===========================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_wine_type_updated_at BEFORE UPDATE ON wine_type FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cepage_updated_at BEFORE UPDATE ON cepage FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasting_step_updated_at BEFORE UPDATE ON tasting_step FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasting_field_updated_at BEFORE UPDATE ON tasting_field FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasting_field_option_updated_at BEFORE UPDATE ON tasting_field_option FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_aroma_note_updated_at BEFORE UPDATE ON aroma_note FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tasting_updated_at BEFORE UPDATE ON tasting FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();