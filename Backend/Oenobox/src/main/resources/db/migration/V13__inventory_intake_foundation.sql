-- Complete the intake middle layer between catalog and cellar.
-- Adds lot line-items, links stored bottles to lot items, and adds catalog submissions.

CREATE TABLE IF NOT EXISTS acquisition_lot_items (
    id BIGSERIAL PRIMARY KEY,
    acquisition_lot_id BIGINT NOT NULL,
    wine_vintage_id BIGINT NOT NULL,
    quantity_received INTEGER NOT NULL,
    quantity_available INTEGER NOT NULL,
    unit_price NUMERIC(10,2),
    currency VARCHAR(8),
    bottle_format VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lot_items_lot FOREIGN KEY (acquisition_lot_id) REFERENCES acquisition_lots(id) ON DELETE CASCADE,
    CONSTRAINT fk_lot_items_wine_vintage FOREIGN KEY (wine_vintage_id) REFERENCES wine_vintages(id)
);

ALTER TABLE stored_bottles
    ADD COLUMN IF NOT EXISTS acquisition_lot_item_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'stored_bottles'
          AND constraint_name = 'fk_stored_bottles_lot_item'
    ) THEN
        ALTER TABLE stored_bottles
            ADD CONSTRAINT fk_stored_bottles_lot_item
                FOREIGN KEY (acquisition_lot_item_id) REFERENCES acquisition_lot_items(id);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS catalog_submissions (
    id BIGSERIAL PRIMARY KEY,
    raw_label_text TEXT,
    submitted_image_path TEXT,
    proposed_data_json TEXT,
    normalized_fingerprint VARCHAR(255),
    source_type VARCHAR(50),
    confidence_score NUMERIC(5,2),
    submitter VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    merged_wine_id BIGINT,
    merged_wine_vintage_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_catalog_submissions_wine FOREIGN KEY (merged_wine_id) REFERENCES wine(id),
    CONSTRAINT fk_catalog_submissions_wine_vintage FOREIGN KEY (merged_wine_vintage_id) REFERENCES wine_vintages(id)
);

CREATE INDEX IF NOT EXISTS idx_acquisition_lot_items_lot_id ON acquisition_lot_items(acquisition_lot_id);
CREATE INDEX IF NOT EXISTS idx_acquisition_lot_items_wine_vintage_id ON acquisition_lot_items(wine_vintage_id);
CREATE INDEX IF NOT EXISTS idx_stored_bottles_lot_item_id ON stored_bottles(acquisition_lot_item_id);
CREATE INDEX IF NOT EXISTS idx_catalog_submissions_status ON catalog_submissions(status);
CREATE INDEX IF NOT EXISTS idx_catalog_submissions_fingerprint ON catalog_submissions(normalized_fingerprint);
