-- Create tasting-domain tables that are required by current JPA entities
-- but are not created by existing Flyway migrations.

CREATE TABLE IF NOT EXISTS tastings (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    wine_type VARCHAR(255),
    region VARCHAR(255),
    aop_igp_vdf VARCHAR(255),
    elevage VARCHAR(255),
    wine_import VARCHAR(255),
    prix_lancement DOUBLE PRECISION,
    prix_actuel DOUBLE PRECISION,
    robe_rouge VARCHAR(255),
    robe_blanche VARCHAR(255),
    robe_rose VARCHAR(255),
    disque VARCHAR(255),
    intensite_visuelle VARCHAR(255),
    limpidite VARCHAR(255),
    brillance VARCHAR(255),
    evolution_visuelle VARCHAR(255),
    remarques_visuelles VARCHAR(255),
    intensite_nez VARCHAR(255),
    qualite_nez VARCHAR(255),
    type_aromes VARCHAR(255),
    description_nez VARCHAR(255),
    attaque VARCHAR(255),
    evolution_bouche VARCHAR(255),
    structure VARCHAR(255),
    texture VARCHAR(255),
    persistance_aromatique VARCHAR(255),
    caudalies_longueur VARCHAR(255),
    structure_longueur VARCHAR(255),
    note_finale VARCHAR(255),
    caudalies_conclusion INTEGER,
    remarques_conclusion VARCHAR(255),
    accords_mets_vins VARCHAR(255),
    temperature_ideale DOUBLE PRECISION,
    date_ideale_consommation VARCHAR(255),
    evolution_probable VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tasting_cepages (
    id BIGSERIAL PRIMARY KEY,
    tasting_id BIGINT NOT NULL,
    cepage TEXT,
    CONSTRAINT fk_tasting_cepages_tasting
        FOREIGN KEY (tasting_id) REFERENCES tastings(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasting_aromes_nature (
    category VARCHAR(255) NOT NULL,
    tasting_id BIGINT NOT NULL,
    PRIMARY KEY (category, tasting_id),
    CONSTRAINT fk_tasting_aromes_nature_tasting
        FOREIGN KEY (tasting_id) REFERENCES tastings(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasting_aromes_nature_notes (
    category VARCHAR(255) NOT NULL,
    tasting_id BIGINT NOT NULL,
    note TEXT,
    CONSTRAINT fk_tasting_aromes_nature_notes_parent
        FOREIGN KEY (category, tasting_id)
            REFERENCES tasting_aromes_nature(category, tasting_id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tasting_cepages_tasting_id
    ON tasting_cepages(tasting_id);

CREATE INDEX IF NOT EXISTS idx_tasting_aromes_nature_tasting_id
    ON tasting_aromes_nature(tasting_id);
