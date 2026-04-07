-- Additive migration to separate catalog, owned stock, physical placement, and tasting notes.
-- This migration keeps legacy tables intact and introduces domain-separated tables.

CREATE TABLE IF NOT EXISTS wine_vintages (
    id BIGSERIAL PRIMARY KEY,
    wine_id BIGINT NOT NULL,
    vintage_year INTEGER,
    bottle_format VARCHAR(50),
    alcohol_percent NUMERIC(4,2),
    drink_from_year INTEGER,
    drink_to_year INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wine_vintages_wine FOREIGN KEY (wine_id) REFERENCES wine(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS acquisition_lots (
    id BIGSERIAL PRIMARY KEY,
    household_id BIGINT,
    source VARCHAR(255),
    acquired_on DATE,
    total_price NUMERIC(10,2),
    currency VARCHAR(8) DEFAULT 'EUR',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stored_bottles (
    id BIGSERIAL PRIMARY KEY,
    wine_vintage_id BIGINT NOT NULL,
    acquisition_lot_id BIGINT,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    purchase_price NUMERIC(10,2),
    owned_since DATE,
    consumed_on DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stored_bottles_wine_vintage FOREIGN KEY (wine_vintage_id) REFERENCES wine_vintages(id),
    CONSTRAINT fk_stored_bottles_lot FOREIGN KEY (acquisition_lot_id) REFERENCES acquisition_lots(id)
);

CREATE TABLE IF NOT EXISTS stock_transactions (
    id BIGSERIAL PRIMARY KEY,
    stored_bottle_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    transaction_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    CONSTRAINT fk_stock_tx_stored_bottle FOREIGN KEY (stored_bottle_id) REFERENCES stored_bottles(id) ON DELETE CASCADE
);

ALTER TABLE bottle_placement
    ADD COLUMN IF NOT EXISTS stored_bottle_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'bottle_placement'
          AND constraint_name = 'fk_bottle_placement_stored_bottle'
    ) THEN
        ALTER TABLE bottle_placement
            ADD CONSTRAINT fk_bottle_placement_stored_bottle
                FOREIGN KEY (stored_bottle_id) REFERENCES stored_bottles(id);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS tasting_notes (
    id BIGSERIAL PRIMARY KEY,
    stored_bottle_id BIGINT,
    rating INTEGER,
    tasted_on DATE,
    appearance TEXT,
    nose TEXT,
    palate TEXT,
    finish TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasting_notes_stored_bottle FOREIGN KEY (stored_bottle_id) REFERENCES stored_bottles(id)
);

CREATE INDEX IF NOT EXISTS idx_wine_vintages_wine_id ON wine_vintages(wine_id);
CREATE INDEX IF NOT EXISTS idx_stored_bottles_wine_vintage_id ON stored_bottles(wine_vintage_id);
CREATE INDEX IF NOT EXISTS idx_bottle_placement_stored_bottle_id ON bottle_placement(stored_bottle_id);
CREATE INDEX IF NOT EXISTS idx_tasting_notes_stored_bottle_id ON tasting_notes(stored_bottle_id);
