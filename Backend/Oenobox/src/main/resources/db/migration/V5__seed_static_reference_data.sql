-- Seed static reference data required for the tasting workflow.
-- This migration is idempotent and safe on non-empty environments.

-- Wine types
INSERT INTO wine_type (name, color_code, description)
VALUES
    ('Rouge', '#BF0202', 'Vin rouge traditionnel'),
    ('Blanc', '#F1F180', 'Vin blanc frais et leger'),
    ('Rose',  '#FF96BA', 'Vin rose fruite')
ON CONFLICT (name) DO UPDATE
SET color_code = EXCLUDED.color_code,
    description = EXCLUDED.description;

-- Cepages (minimal static set per wine type)
INSERT INTO cepage (name, wine_type_id, description)
SELECT v.cepage_name, wt.id, v.description
FROM (
    VALUES
        ('Rouge', 'CABERNET-SAUVIGNON', 'Cepage rouge robuste'),
        ('Rouge', 'MERLOT', 'Cepage rouge souple'),
        ('Rouge', 'PINOT NOIR', 'Cepage rouge elegant'),
        ('Blanc', 'CHARDONNAY', 'Cepage blanc universel'),
        ('Blanc', 'SAUVIGNON BLANC', 'Cepage blanc vif'),
        ('Blanc', 'RIESLING', 'Cepage blanc aromatique'),
        ('Rose', 'SYRAH', 'Cepage rose epice'),
        ('Rose', 'GRENACHE', 'Cepage rose fruite'),
        ('Rose', 'CINSAULT', 'Cepage rose leger')
) AS v(wine_type_name, cepage_name, description)
JOIN wine_type wt ON wt.name = v.wine_type_name
ON CONFLICT (name, wine_type_id) DO UPDATE
SET description = EXCLUDED.description;

-- Tasting steps
INSERT INTO tasting_step (step_number, name, title, description)
VALUES
    (1, 'informations', 'Informations sur le Vin', NULL),
    (2, 'visuel', 'Aspect Visuel', NULL),
    (3, 'nez', 'Nez', NULL),
    (4, 'bouche', 'Bouche', NULL),
    (5, 'longueur', 'Longueur en bouche', NULL)
ON CONFLICT (step_number) DO UPDATE
SET name = EXCLUDED.name,
    title = EXCLUDED.title,
    description = EXCLUDED.description;

-- Tasting fields
INSERT INTO tasting_field (step_id, field_type, name, label, placeholder, required, multi_select, wine_type_restriction)
SELECT ts.id, f.field_type, f.name, f.label, f.placeholder, f.required, f.multi_select, f.wine_type_restriction::jsonb
FROM tasting_step ts
JOIN (
    VALUES
        ('informations', 'select-button', 'type', 'Type', 'Type de vin', true, false, '["red", "white", "rose"]'),
        ('informations', 'autocomplete', 'cepage', 'Cepage(s)', 'Entrez les cepages', true, true, '["red", "white", "rose"]'),
        ('informations', 'text', 'region', 'Region', 'Entrez la region', true, false, '["red", "white", "rose"]'),
        ('informations', 'text', 'aop_igp_vdf', 'AOP/IGP/VDF', NULL, true, false, '["red", "white", "rose"]'),
        ('informations', 'text', 'elevage', 'Elevage', 'Decrivez l''elevage', false, false, '["red", "white", "rose"]'),
        ('informations', 'select', 'import', 'Import', NULL, true, false, '["red", "white", "rose"]'),
        ('informations', 'number', 'prix_lancement', 'Prix de Lancement (EUR)', 'Entrez le prix de lancement', true, false, '["red", "white", "rose"]'),
        ('informations', 'number', 'prix_actuel', 'Prix Actuel (EUR)', 'Entrez le prix actuel', true, false, '["red", "white", "rose"]'),

        ('visuel', 'select-button', 'robe_rouge', 'Robe rouge', NULL, true, false, '["red"]'),
        ('visuel', 'select-button', 'robe_blanche', 'Robe blanche', NULL, true, false, '["white"]'),
        ('visuel', 'select-button', 'disque', 'Disque', NULL, true, false, '["red"]'),
        ('visuel', 'select-button', 'robe_rose', 'Robe rose', NULL, true, false, '["rose"]'),
        ('visuel', 'select-button', 'intensite', 'Intensite', NULL, true, false, '["red", "white", "rose"]'),
        ('visuel', 'select-button', 'limpidite', 'Limpidite', NULL, true, false, '["red", "white", "rose"]'),
        ('visuel', 'select-button', 'brillance', 'Brillance', NULL, true, false, '["red", "white", "rose"]'),
        ('visuel', 'select-button', 'evolution', 'Evolution', NULL, true, false, '["red", "white", "rose"]'),
        ('visuel', 'textarea', 'remarques', 'Remarques', NULL, false, false, '["red", "white", "rose"]'),

        ('nez', 'select-button', 'intensite', 'Intensite', NULL, true, false, '["red", "white", "rose"]'),
        ('nez', 'select-button', 'qualite', 'Qualite', NULL, true, false, '["red", "white", "rose"]'),
        ('nez', 'select-button', 'type_aromes', 'Aromes-Type', NULL, true, false, '["red", "white", "rose"]'),
        ('nez', 'select-button', 'nature_aromes', 'Aromes-Nature', 'Type de vin', true, true, '["red", "white", "rose"]'),

        ('bouche', 'select', 'attaque', 'Attaque', NULL, true, false, '["red", "white", "rose"]'),
        ('bouche', 'select', 'evolution', 'Evolution', NULL, true, false, '["red", "white", "rose"]'),
        ('bouche', 'select', 'structure', 'Structure', NULL, true, false, '["red", "white", "rose"]'),
        ('bouche', 'select', 'texture', 'Texture', NULL, false, false, '["red", "white", "rose"]'),

        ('longueur', 'slider', 'persistance_aromatique', 'Persistance aromatique', NULL, true, false, '["red", "white", "rose"]'),
        ('longueur', 'select', 'caudalies', 'Caudalies', NULL, true, false, '["red", "white", "rose"]'),
        ('longueur', 'select', 'structure', 'Structure', NULL, true, false, '["red", "white", "rose"]')
) AS f(step_name, field_type, name, label, placeholder, required, multi_select, wine_type_restriction)
ON ts.name = f.step_name
ON CONFLICT (step_id, name) DO UPDATE
SET field_type = EXCLUDED.field_type,
    label = EXCLUDED.label,
    placeholder = EXCLUDED.placeholder,
    required = EXCLUDED.required,
    multi_select = EXCLUDED.multi_select,
    wine_type_restriction = EXCLUDED.wine_type_restriction;

-- Tasting field options
INSERT INTO tasting_field_option (field_id, value, label, color_code, is_negative, sort_order, icon, wine_type_restriction, group_index, group_required)
SELECT tf.id, o.value, o.label, o.color_code, o.is_negative, o.sort_order, o.icon,
       CASE WHEN o.wine_type_restriction IS NULL THEN NULL ELSE o.wine_type_restriction::jsonb END,
       o.group_index, o.group_required
FROM tasting_field tf
JOIN tasting_step ts ON ts.id = tf.step_id
JOIN (
    VALUES
        ('informations', 'type', 'Rouge', 'Rouge', '#BF0202', false, 1, NULL, NULL, NULL, NULL),
        ('informations', 'type', 'Blanc', 'Blanc', '#F1F180', false, 2, NULL, NULL, NULL, NULL),
        ('informations', 'type', 'Rose', 'Rose', '#FF96BA', false, 3, NULL, NULL, NULL, NULL),
        ('informations', 'import', '/', '/', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('informations', 'import', 'Espagne', 'Espagne', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('informations', 'import', 'Italie', 'Italie', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('informations', 'import', 'USA', 'USA', NULL, false, 4, NULL, NULL, NULL, NULL),

        ('visuel', 'robe_rouge', 'Noir', 'Noir', '#000000', false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_rouge', 'Rubis', 'Rubis', '#D73C3A', false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_rouge', 'Grenat', 'Grenat', '#6E2445', false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_blanche', 'Blanc', 'Blanc', '#F6F2E7', false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_blanche', 'Paille', 'Paille', '#E6CF45', false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_blanche', 'Dore', 'Dore', '#D19C36', false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'disque', 'Violace', 'Violace', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'disque', 'Rose', 'Rose', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'disque', 'Tuile', 'Tuile', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_rose', 'Gris', 'Gris', '#F0CBB8', false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_rose', 'Rose', 'Rose', '#E7A39A', false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'robe_rose', 'Orange', 'Orange', '#E27240', false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'intensite', 'Faible', 'Faible', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'intensite', 'Moyenne', 'Moyenne', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'intensite', 'Intense', 'Intense', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'limpidite', 'Trouble', 'Trouble', NULL, true, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'limpidite', 'Claire', 'Claire', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'limpidite', 'Limpide', 'Limpide', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'brillance', 'Terne', 'Terne', NULL, true, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'brillance', 'Brillante', 'Brillante', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'brillance', 'Eclatante', 'Eclatante', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('visuel', 'evolution', 'Legere', 'Legere', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('visuel', 'evolution', 'Moyenne', 'Moyenne', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('visuel', 'evolution', 'Avancee', 'Avancee', NULL, false, 3, NULL, NULL, NULL, NULL),

        ('nez', 'intensite', 'Faible', 'Faible', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('nez', 'intensite', 'Moyenne', 'Moyenne', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('nez', 'intensite', 'Forte', 'Forte', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('nez', 'qualite', 'Alteree', 'Alteree', NULL, true, 1, NULL, NULL, NULL, NULL),
        ('nez', 'qualite', 'Franche', 'Franche', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('nez', 'qualite', 'Parfaite', 'Parfaite', NULL, false, 3, NULL, NULL, NULL, NULL),

        ('nez', 'type_aromes', 'vif', 'vif', NULL, false, 1, NULL, NULL, 1, true),
        ('nez', 'type_aromes', 'frais', 'frais', NULL, false, 2, NULL, NULL, 1, true),
        ('nez', 'type_aromes', 'neutre', 'neutre', NULL, false, 3, NULL, NULL, 1, true),
        ('nez', 'type_aromes', 'suave', 'suave', NULL, false, 4, NULL, NULL, 1, true),
        ('nez', 'type_aromes', 'lourd', 'lourd', NULL, true, 5, NULL, NULL, 1, true),
        ('nez', 'type_aromes', 'desagreable', 'desagreable', NULL, true, 6, NULL, NULL, 2, true),
        ('nez', 'type_aromes', 'commun', 'commun', NULL, false, 7, NULL, NULL, 2, true),
        ('nez', 'type_aromes', 'agreable', 'agreable', NULL, false, 8, NULL, NULL, 2, true),
        ('nez', 'type_aromes', 'epanoui', 'epanoui', NULL, false, 9, NULL, NULL, 3, false),
        ('nez', 'type_aromes', 'evolue', 'evolue', NULL, false, 10, NULL, NULL, 3, false),
        ('nez', 'type_aromes', 'passe', 'passe', NULL, true, 11, NULL, NULL, 3, false),
        ('nez', 'type_aromes', 'roti', 'roti', NULL, false, 12, NULL, NULL, 4, false),
        ('nez', 'type_aromes', 'franc', 'franc', NULL, false, 13, NULL, NULL, 5, false),
        ('nez', 'type_aromes', 'strict', 'strict', NULL, false, 14, NULL, NULL, 5, false),
        ('nez', 'type_aromes', 'violent', 'violent', NULL, true, 15, NULL, NULL, 5, false),

        ('nez', 'nature_aromes', 'fruitee', 'fruitee', NULL, false, 1, 'mdi-food-apple', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'florale', 'florale', NULL, false, 2, 'mdi-flower', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'boisee', 'boisee', NULL, false, 3, 'mdi-forest', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'balsamique', 'balsamique', NULL, false, 4, 'mdi-leaf-maple', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'epicee', 'epicee', NULL, false, 5, 'mdi-chili-mild', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'empyreumatique', 'empyreumatique', NULL, false, 6, 'mdi-fire-circle', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'animale', 'animale', NULL, false, 7, 'mdi-cow', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'vegetale', 'vegetale', NULL, false, 8, 'mdi-grass', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'alimentaire', 'alimentaire', NULL, false, 9, 'mdi-food-fork-drink', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'minerale', 'minerale', NULL, false, 10, NULL, NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'chimique', 'chimique', NULL, false, 11, 'mdi-atom-variant', NULL, NULL, NULL),
        ('nez', 'nature_aromes', 'anormale', 'anormale', NULL, true, 12, 'mdi-close', NULL, NULL, NULL),

        ('bouche', 'attaque', 'Acide', 'Acide', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('bouche', 'attaque', 'Souple', 'Souple', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('bouche', 'attaque', 'Ronde', 'Ronde', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('bouche', 'evolution', 'Ample', 'Ample', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('bouche', 'evolution', 'Puissante', 'Puissante', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('bouche', 'evolution', 'Continue', 'Continue', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('bouche', 'structure', 'Concentree', 'Concentree', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('bouche', 'structure', 'Souple', 'Souple', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('bouche', 'structure', 'Charnue', 'Charnue', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('bouche', 'texture', 'Fluide', 'Fluide', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('bouche', 'texture', 'Veloutee', 'Veloutee', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('bouche', 'texture', 'Soyeuse', 'Soyeuse', NULL, false, 3, NULL, NULL, NULL, NULL),

        ('longueur', 'persistance_aromatique', 'Courte', 'Courte', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('longueur', 'persistance_aromatique', 'Bonne', 'Bonne', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('longueur', 'persistance_aromatique', 'Longue', 'Longue', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('longueur', 'caudalies', 'Ample', 'Ample', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('longueur', 'caudalies', 'Puissante', 'Puissante', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('longueur', 'caudalies', 'Continue', 'Continue', NULL, false, 3, NULL, NULL, NULL, NULL),
        ('longueur', 'structure', 'Concentree', 'Concentree', NULL, false, 1, NULL, NULL, NULL, NULL),
        ('longueur', 'structure', 'Souple', 'Souple', NULL, false, 2, NULL, NULL, NULL, NULL),
        ('longueur', 'structure', 'Charnue', 'Charnue', NULL, false, 3, NULL, NULL, NULL, NULL)
) AS o(step_name, field_name, value, label, color_code, is_negative, sort_order, icon, wine_type_restriction, group_index, group_required)
ON ts.name = o.step_name AND tf.name = o.field_name
ON CONFLICT (field_id, value) DO UPDATE
SET label = EXCLUDED.label,
    color_code = EXCLUDED.color_code,
    is_negative = EXCLUDED.is_negative,
    sort_order = EXCLUDED.sort_order,
    icon = EXCLUDED.icon,
    wine_type_restriction = EXCLUDED.wine_type_restriction,
    group_index = EXCLUDED.group_index,
    group_required = EXCLUDED.group_required;

-- Aroma notes for aroma categories
INSERT INTO aroma_note (category_option_id, name, description)
SELECT tfo.id, n.note_name, n.description
FROM tasting_field_option tfo
JOIN tasting_field tf ON tf.id = tfo.field_id
JOIN tasting_step ts ON ts.id = tf.step_id
JOIN (
    VALUES
        ('fruitee', 'Citron', 'Note citronnee fraiche'),
        ('fruitee', 'Orange', 'Note d''orange douce'),
        ('fruitee', 'Pamplemousse', 'Note d''agrume acidule'),
        ('florale', 'Acacia', 'Fleur printaniere'),
        ('florale', 'Tilleul', 'Fleur d''ete'),
        ('boisee', 'Chene', 'Bois de chene vanille'),
        ('boisee', 'Cedre', 'Bois resineux'),
        ('epicee', 'Poivre', 'Note poivree'),
        ('epicee', 'Cannelle', 'Note epicee chaude'),
        ('minerale', 'Silex', 'Note pierre a fusil'),
        ('anormale', 'Bouchon', 'Defaut de bouchon')
) AS n(category_value, note_name, description)
ON tfo.value = n.category_value
WHERE ts.name = 'nez' AND tf.name = 'nature_aromes'
ON CONFLICT (category_option_id, name) DO UPDATE
SET description = EXCLUDED.description;
