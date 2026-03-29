-- Align cave-related table column names with current JPA/Hibernate mappings.
-- Entities map:
-- - Space.coordX/coordY to coordx/coordy
-- - StorageUnit.posX/posY to posx/posy

DO $$
BEGIN
    -- coord_x -> coordx
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coord_x'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coordx'
    ) THEN
        ALTER TABLE space RENAME COLUMN coord_x TO coordx;
    END IF;

    -- coord_y -> coordy
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coord_y'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coordy'
    ) THEN
        ALTER TABLE space RENAME COLUMN coord_y TO coordy;
    END IF;

    -- Ensure columns exist for environments where neither form is present.
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coordx'
    ) THEN
        ALTER TABLE space ADD COLUMN coordx DOUBLE PRECISION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'space'
          AND column_name = 'coordy'
    ) THEN
        ALTER TABLE space ADD COLUMN coordy DOUBLE PRECISION;
    END IF;
END $$;

DO $$
BEGIN
    -- pos_x -> posx
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'pos_x'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'posx'
    ) THEN
        ALTER TABLE storage_unit RENAME COLUMN pos_x TO posx;
    END IF;

    -- pos_y -> posy
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'pos_y'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'posy'
    ) THEN
        ALTER TABLE storage_unit RENAME COLUMN pos_y TO posy;
    END IF;

    -- Ensure columns exist for environments where neither form is present.
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'posx'
    ) THEN
        ALTER TABLE storage_unit ADD COLUMN posx DOUBLE PRECISION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'storage_unit'
          AND column_name = 'posy'
    ) THEN
        ALTER TABLE storage_unit ADD COLUMN posy DOUBLE PRECISION;
    END IF;
END $$;
