-- PostgreSQL Data Population Script for Oenobox Wine Tasting Application
-- This script populates all the reference data needed for the tasting form

-- ===========================================
-- WINE TYPES
-- ===========================================
INSERT INTO wine_type (id, name, color_code, description) VALUES
(1, 'Rouge', '#BF0202', 'Vin rouge traditionnel'),
(2, 'Blanc', '#F1F180', 'Vin blanc frais et léger'),
(3, 'Rosé', '#FF96BA', 'Vin rosé fruité');

-- ===========================================
-- CEPAGES (GRAPE VARIETIES)
-- ===========================================
INSERT INTO cepage (id, name, wine_type_id, description) VALUES
-- Red wine cepages
(1, 'CABERNET-SAUVIGNON', 1, 'Cépage rouge robuste'),
(2, 'CARIGNAN', 1, 'Cépage rouge méridional'),
(3, 'CINSAULT', 1, 'Cépage rouge léger'),
(4, 'MALBEC', 1, 'Cépage rouge argentin'),
(5, 'GAMAY', 1, 'Cépage rouge de Beaujolais'),
(6, 'GRENACHE', 1, 'Cépage rouge du sud'),
(7, 'MERLOT', 1, 'Cépage rouge souple'),
(8, 'MOURVÈDRE', 1, 'Cépage rouge puissant'),
(9, 'PINOT NOIR', 1, 'Cépage rouge élégant'),
(10, 'SANGIOVESE', 1, 'Cépage rouge italien'),
(11, 'SYRAH', 1, 'Cépage rouge épicé'),

-- White wine cepages
(12, 'Chardonnay', 2, 'Cépage blanc universel'),
(13, 'pinot blanc', 2, 'Cépage blanc alsacien'),
(14, 'sauvignon blanc', 2, 'Cépage blanc vif'),
(15, 'grenache blanc', 2, 'Cépage blanc méridional'),
(16, 'chenin', 2, 'Cépage blanc de Loire'),
(17, 'gewurztraminer', 2, 'Cépage blanc aromatique'),
(18, 'riesling', 2, 'Cépage blanc allemand'),
(19, 'viognier', 2, 'Cépage blanc du Rhône'),
(20, 'sémillon', 2, 'Cépage blanc de Bordeaux'),
(21, 'marsanne', 2, 'Cépage blanc du Rhône'),
(22, 'roussanne', 2, 'Cépage blanc du Rhône'),

-- Rosé wine cepages
(23, 'Syrah', 3, 'Cépage rosé épicé'),
(24, 'Grenache', 3, 'Cépage rosé fruité'),
(25, 'Cinsault', 3, 'Cépage rosé léger');

-- ===========================================
-- TASTING STEPS
-- ===========================================
INSERT INTO tasting_step (id, step_number, name, title, description) VALUES
(1, 1, 'informations', 'Informations sur le Vin', 'Informations générales sur le vin à déguster'),
(2, 2, 'visuel', 'Aspect Visuel', 'Évaluation de l''aspect visuel du vin'),
(3, 3, 'nez', 'Nez', 'Analyse olfactive du vin'),
(4, 4, 'bouche', 'Bouche', 'Évaluation en bouche'),
(5, 5, 'longueur', 'Longueur en bouche', 'Persistance aromatique'),
(6, 6, 'conclusion', 'Conclusion', 'Jugement final et notes');

-- ===========================================
-- TASTING FIELDS
-- ===========================================
INSERT INTO tasting_field (id, step_id, field_type, name, label, placeholder, required, multi_select, wine_type_restriction) VALUES
-- Step 1: Informations
(1, 1, 'select-button', 'type', 'Type', 'Type de vin', true, false, '["red", "white", "rose"]'),
(2, 1, 'autocomplete', 'cepage', 'Cépage(s)', 'Entrez les cépages', true, true, '["red", "white", "rose"]'),
(3, 1, 'text', 'region', 'Région', 'Entrez la région', true, false, '["red", "white", "rose"]'),
(4, 1, 'text', 'aop_igp_vdf', 'AOP/IGP/VDF', null, true, false, '["red", "white", "rose"]'),
(5, 1, 'text', 'elevage', 'Élevage', 'Décrivez l''élevage', false, false, '["red", "white", "rose"]'),
(6, 1, 'select', 'import', 'Import', null, true, false, '["red", "white", "rose"]'),
(7, 1, 'number', 'prix_lancement', 'Prix de Lancement (€)', 'Entrez le prix de lancement', true, false, '["red", "white", "rose"]'),
(8, 1, 'number', 'prix_actuel', 'Prix Actuel (€)', 'Entrez le prix actuel', true, false, '["red", "white", "rose"]'),

-- Step 2: Visuel
(9, 2, 'select-button', 'robe_rouge', 'Robe rouge', null, true, false, '["red"]'),
(10, 2, 'select-button', 'robe_blanche', 'Robe blanche', null, true, false, '["white"]'),
(11, 2, 'select-button', 'disque', 'Disque', null, true, false, '["red"]'),
(12, 2, 'select-button', 'robe_rose', 'Robe rosé', null, true, false, '["rose"]'),
(13, 2, 'select-button', 'intensite', 'Intensité', null, true, false, '["red", "white", "rose"]'),
(14, 2, 'select-button', 'limpidite', 'Limpidité', null, true, false, '["red", "white", "rose"]'),
(15, 2, 'select-button', 'brillance', 'Brillance', null, true, false, '["red", "white", "rose"]'),
(16, 2, 'select-button', 'evolution', 'Evolution', null, true, false, '["red", "white", "rose"]'),
(17, 2, 'textarea', 'remarques', 'Remarques', null, false, false, '["red", "white", "rose"]'),

-- Step 3: Nez
(18, 3, 'select-button', 'intensite', 'Intensité', null, true, false, '["red", "white", "rose"]'),
(19, 3, 'select-button', 'qualite', 'Qualité', null, true, false, '["red", "white", "rose"]'),
(20, 3, 'select-button', 'type_aromes', 'Arômes-Type', null, true, false, '["red", "white", "rose"]'),
(21, 3, 'select-button', 'nature_aromes', 'Aromes-Nature', 'Type de vin', true, true, '["red", "white", "rose"]'),

-- Step 4: Bouche
(22, 4, 'select', 'attaque', 'Attaque', null, true, false, '["red", "white", "rose"]'),
(23, 4, 'select', 'evolution', 'Evolution', null, true, false, '["red", "white", "rose"]'),
(24, 4, 'select', 'structure', 'Structure', null, true, false, '["red", "white", "rose"]'),
(25, 4, 'select', 'texture', 'Texture', null, false, false, '["red", "white", "rose"]'),

-- Step 5: Longueur
(26, 5, 'slider', 'persistance_aromatique', 'Persistance aromatique', null, true, false, '["red", "white", "rose"]'),
(27, 5, 'select', 'caudalies', 'Caudalies', null, true, false, '["red", "white", "rose"]'),
(28, 5, 'select', 'structure', 'Structure', null, true, false, '["red", "white", "rose"]'),

-- Step 6: Conclusion
(29, 6, 'select', 'note_finale', 'Note Finale', null, true, false, '["red", "white", "rose"]'),
(30, 6, 'number', 'caudalies', 'Caudalies', 'Nombre de caudalies', false, false, '["red", "white", "rose"]'),
(31, 6, 'textarea', 'remarques', 'Remarques', 'Ajouter des remarques', false, false, '["red", "white", "rose"]'),
(32, 6, 'text', 'accords_mets_vins', 'Accords Mets et Vins', 'Ex: Fromages, viandes', false, false, '["red", "white", "rose"]');

-- ===========================================
-- TASTING FIELD OPTIONS
-- ===========================================

-- Wine Type Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(1, 1, 'Rouge', 'Rouge', '#BF0202', false, 1),
(2, 1, 'Blanc', 'Blanc', '#F1F180', false, 2),
(3, 1, 'Rosé', 'Rosé', '#FF96BA', false, 3);

-- Import Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(4, 6, '/', '/', null, false, 1),
(5, 6, 'Espagne', 'Espagne', null, false, 2),
(6, 6, 'Italie', 'Italie', null, false, 3),
(7, 6, 'USA', 'USA', null, false, 4);

-- Red Wine Robe Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(8, 9, 'Noir', 'Noir', '#000000', false, 1),
(9, 9, 'bleuâtre', 'bleuâtre', '#3B0014', false, 2),
(10, 9, 'violet', 'violet', '#901766', false, 3),
(11, 9, 'pourpre', 'pourpre', '#9A2049', false, 4),
(12, 9, 'grenat', 'grenat', '#6E2445', false, 5),
(13, 9, 'rubis', 'rubis', '#D73C3A', false, 6),
(14, 9, 'incarnat et carmin', 'incarnat et carmin', '#B91B18', false, 7),
(15, 9, 'brique', 'brique', '#BE4731', false, 8),
(16, 9, 'tuilé', 'tuilé', '#8C3116', false, 9),
(17, 9, 'Orangé ', 'Orangé', '#C63D11', false, 10);

-- White Wine Robe Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(18, 10, 'blanc', 'blanc', '#F6F2E7', false, 1),
(19, 10, 'jaune-vert', 'jaune-vert', '#E1DB85', false, 2),
(20, 10, 'jaune pâle', 'jaune pâle', '#F3E978', false, 3),
(21, 10, 'paille', 'paille', '#E6CF45', false, 4),
(22, 10, 'or', 'or', '#D7B643', false, 5),
(23, 10, 'doré', 'doré', '#D19C36', false, 6),
(24, 10, 'ambré', 'ambré', '#D18539', false, 7),
(25, 10, 'madérisé', 'madérisé', '#A78A3E', false, 8);

-- Disque Options (Red Wine)
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(26, 11, 'violacé', 'violacé', null, false, 1),
(27, 11, 'rosé', 'rosé', null, false, 2),
(28, 11, 'brun', 'brun', null, false, 3),
(29, 11, 'tuilé', 'tuilé', null, false, 4),
(30, 11, 'orangé', 'orangé', null, false, 5);

-- Rosé Wine Robe Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(31, 12, 'gris', 'gris', '#F0CBB8', false, 1),
(32, 12, 'rosé', 'rosé', '#E7A39A', false, 2),
(33, 12, 'rose violacé', 'rose violacé', '#EE537B', false, 3),
(34, 12, 'rosé orangé', 'rosé orangé', '#EB7D70', false, 4),
(35, 12, 'roux', 'roux', '#E89E59', false, 5),
(36, 12, 'orangé', 'orangé', '#E27240', false, 6),
(37, 12, 'brun', 'brun', '#C95A2C', false, 7);

-- Intensity Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(38, 13, 'pâle', 'pâle', null, false, 1),
(39, 13, 'légère', 'légère', null, false, 2),
(40, 13, 'faible', 'faible', null, false, 3),
(41, 13, 'peu intense', 'peu intense', null, false, 4),
(42, 13, 'moyenne', 'moyenne', null, false, 5),
(43, 13, 'soutenue', 'soutenue', null, false, 6),
(44, 13, 'intense', 'intense', null, false, 7),
(45, 13, 'profonde', 'profonde', null, false, 8),
(46, 13, 'opaque', 'opaque', null, false, 9);

-- Limpidity Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(47, 14, 'voilée', 'voilée', null, true, 1),
(48, 14, 'trouble', 'trouble', null, true, 2),
(49, 14, 'louche', 'louche', null, true, 3),
(50, 14, 'claire', 'claire', null, false, 4),
(51, 14, 'limpide', 'limpide', null, false, 5),
(52, 14, 'parfaite', 'parfaite', null, false, 6);

-- Brilliance Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(53, 15, 'éteint', 'éteint', null, true, 1),
(54, 15, 'terne', 'terne', null, true, 2),
(55, 15, 'mat', 'mat', null, true, 3),
(56, 15, 'cristalline', 'cristalline', null, false, 4),
(57, 15, 'brillante', 'brillante', null, false, 5),
(58, 15, 'éclatante', 'éclatante', null, false, 6);

-- Evolution Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(59, 16, 'nulle', 'nulle', null, false, 1),
(60, 16, 'légère', 'légère', null, false, 2),
(61, 16, 'moyenne', 'moyenne', null, false, 3),
(62, 16, 'évidente', 'évidente', null, false, 4),
(63, 16, 'avancée', 'avancée', null, false, 5),
(64, 16, 'forte', 'forte', null, false, 6),
(65, 16, 'excessive', 'excessive', null, true, 7);

-- Nose Intensity Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(66, 18, 'fermée', 'fermée', null, false, 1),
(67, 18, 'retenue', 'retenue', null, false, 2),
(68, 18, 'faible', 'faible', null, false, 3),
(69, 18, 'discrète', 'discrète', null, false, 4),
(70, 18, 'moyenne', 'moyenne', null, false, 5),
(71, 18, 'bonne', 'bonne', null, false, 6),
(72, 18, 'très bonne', 'très bonne', null, false, 7),
(73, 18, 'forte', 'forte', null, false, 8),
(74, 18, 'puissante', 'puissante', null, false, 9);

-- Nose Quality Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(75, 19, 'altérée', 'altérée', null, true, 1),
(76, 19, 'douteuse', 'douteuse', null, true, 2),
(77, 19, 'franche', 'franche', null, false, 3),
(78, 19, 'nette', 'nette', null, false, 4),
(79, 19, 'parfaite', 'parfaite', null, false, 5);

-- Aroma Type Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(80, 20, 'simple', 'simple', null, false, 1),
(81, 20, 'complexe', 'complexe', null, false, 2),
(82, 20, 'grossier', 'grossier', null, true, 3),
(83, 20, 'fin', 'fin', null, false, 4),
(84, 20, 'élégant', 'élégant', null, false, 5),
(85, 20, 'racé', 'racé', null, true, 6),
(86, 20, 'vif', 'vif', null, false, 7),
(87, 20, 'frais', 'frais', null, false, 8),
(88, 20, 'neutre', 'neutre', null, false, 9),
(89, 20, 'suave', 'suave', null, false, 10),
(90, 20, 'lourd', 'lourd', null, true, 11),
(91, 20, 'désagréable', 'désagréable', null, true, 12),
(92, 20, 'commun', 'commun', null, false, 13),
(93, 20, 'agréable', 'agréable', null, false, 14),
(94, 20, 'épanoui', 'épanoui', null, false, 15),
(95, 20, 'évolué', 'évolué', null, false, 16),
(96, 20, 'passé', 'passé', null, true, 17),
(97, 20, 'rôti', 'rôti', null, false, 18),
(98, 20, 'franc', 'franc', null, false, 19),
(99, 20, 'strict', 'strict', null, false, 20),
(100, 20, 'violent', 'violent', null, true, 21);

-- Aroma Nature Options (Categories)
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order, icon) VALUES
(101, 21, 'fruitée', 'fruitée', null, false, 1, 'mdi-food-apple'),
(102, 21, 'florale', 'florale', null, false, 2, 'mdi-flower'),
(103, 21, 'boisée', 'boisée', null, false, 3, 'mdi-forest'),
(104, 21, 'balsamique', 'balsamique', null, false, 4, 'mdi-leaf-maple'),
(105, 21, 'épicée', 'épicée', null, false, 5, 'mdi-chili-mild'),
(106, 21, 'empyreumatique', 'empyreumatique', null, false, 6, 'mdi-fire-circle'),
(107, 21, 'animale', 'animale', null, false, 7, 'mdi-cow'),
(108, 21, 'végétale', 'végétale', null, false, 8, 'mdi-grass'),
(109, 21, 'alimentaire', 'alimentaire', null, false, 9, 'mdi-food-fork-drink'),
(110, 21, 'minérale', 'minérale', null, false, 10, null),
(111, 21, 'chimique', 'chimique', null, false, 11, 'mdi-atom-variant'),
(112, 21, 'anormale', 'anormale', null, true, 12, 'mdi-close');

-- Mouth Attack Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(113, 22, 'dure', 'dure', null, false, 1),
(114, 22, 'mordante', 'mordante', null, false, 2),
(115, 22, 'acide', 'acide', null, false, 3),
(116, 22, 'vive', 'vive', null, false, 4),
(117, 22, 'souple', 'souple', null, false, 5),
(118, 22, 'ronde', 'ronde', null, false, 6),
(119, 22, 'molle', 'molle', null, false, 7),
(120, 22, 'plate', 'plate', null, false, 8);

-- Mouth Evolution Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(121, 23, 'ample', 'ample', null, false, 1),
(122, 23, 'puissante', 'puissante', null, false, 2),
(123, 23, 'généreuse', 'généreuse', null, false, 3),
(124, 23, 'continue', 'continue', null, false, 4),
(125, 23, 'tannique', 'tannique', null, false, 5),
(126, 23, 'acide', 'acide', null, false, 6),
(127, 23, 'plate', 'plate', null, false, 7);

-- Mouth Structure Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(128, 24, 'concentrée', 'concentrée', null, false, 1),
(129, 24, 'souple', 'souple', null, false, 2),
(130, 24, 'charnue', 'charnue', null, false, 3),
(131, 24, 'décharnée', 'décharnée', null, false, 4),
(132, 24, 'creuse', 'creuse', null, false, 5),
(133, 24, 'maigre', 'maigre', null, false, 6),
(134, 24, 'grossière', 'grossière', null, false, 7),
(135, 24, 'sèche', 'sèche', null, false, 8),
(136, 24, 'dure', 'dure', null, false, 9),
(137, 24, 'filiforme', 'filiforme', null, false, 10);

-- Mouth Texture Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(138, 25, 'aqueuse', 'aqueuse', null, false, 1),
(139, 25, 'fluide', 'fluide', null, false, 2),
(140, 25, 'légère', 'légère', null, false, 3),
(141, 25, 'concentrée', 'concentrée', null, false, 4),
(142, 25, 'moelleuse', 'moelleuse', null, false, 5),
(143, 25, 'onctueuse', 'onctueuse', null, false, 6),
(144, 25, 'épaisse', 'épaisse', null, false, 7),
(145, 25, 'astringente', 'astringente', null, false, 8),
(146, 25, 'veloutée', 'veloutée', null, false, 9),
(147, 25, 'soyeuse', 'soyeuse', null, false, 10),
(148, 25, 'fondue', 'fondue', null, false, 11);

-- Persistence Options (Slider)
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(149, 26, 'nulle', 'nulle', null, false, 1),
(150, 26, 'courte', 'courte', null, false, 2),
(151, 26, 'moyenne', 'moyenne', null, false, 3),
(152, 26, 'bonne', 'bonne', null, false, 4),
(153, 26, 'longue', 'longue', null, false, 5),
(154, 26, 'très longue', 'très longue', null, false, 6),
(155, 26, 'infinie', 'infinie', null, false, 7);

-- Caudalies Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(156, 27, 'ample', 'ample', null, false, 1),
(157, 27, 'puissante', 'puissante', null, false, 2),
(158, 27, 'généreuse', 'généreuse', null, false, 3),
(159, 27, 'continue', 'continue', null, false, 4),
(160, 27, 'tannique', 'tannique', null, false, 5),
(161, 27, 'acide', 'acide', null, false, 6),
(162, 27, 'plate', 'plate', null, false, 7);

-- Length Structure Options (duplicate of mouth structure)
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(163, 28, 'concentrée', 'concentrée', null, false, 1),
(164, 28, 'souple', 'souple', null, false, 2),
(165, 28, 'charnue', 'charnue', null, false, 3),
(166, 28, 'décharnée', 'décharnée', null, false, 4),
(167, 28, 'creuse', 'creuse', null, false, 5),
(168, 28, 'maigre', 'maigre', null, false, 6),
(169, 28, 'grossière', 'grossière', null, false, 7),
(170, 28, 'sèche', 'sèche', null, false, 8),
(171, 28, 'dure', 'dure', null, false, 9),
(172, 28, 'filiforme', 'filiforme', null, false, 10);

-- Final Note Options
INSERT INTO tasting_field_option (id, field_id, value, label, color_code, is_negative, sort_order) VALUES
(173, 29, 'très médiocre', 'très médiocre', null, true, 1),
(174, 29, 'médiocre', 'médiocre', null, true, 2),
(175, 29, 'mauvais', 'mauvais', null, true, 3),
(176, 29, 'passable', 'passable', null, false, 4),
(177, 29, 'correct', 'correct', null, false, 5),
(178, 29, 'bon', 'bon', null, false, 6),
(179, 29, 'très bon', 'très bon', null, false, 7),
(180, 29, 'superbe', 'superbe', null, false, 8),
(181, 29, 'excellent', 'excellent', null, false, 9),
(182, 29, 'exceptionnel', 'exceptionnel', null, false, 10),
(183, 29, 'légendaire', 'légendaire', null, false, 11);

-- ===========================================
-- AROMA NOTES (Sub-options for aroma categories)
-- ===========================================
INSERT INTO aroma_note (id, category_option_id, name, description) VALUES
-- Fruitée notes
(1, 101, 'Citron', 'Note citronnée fraîche'),
(2, 101, 'Pamplemousse', 'Note d''agrume acidulé'),
(3, 101, 'Orange', 'Note d''orange douce'),

-- Florale notes
(4, 102, 'Aubépine', 'Fleur blanche délicate'),
(5, 102, 'Acacia', 'Fleur printanière'),
(6, 102, 'Tilleul', 'Fleur d''été'),

-- Boisée notes
(7, 103, 'Chêne', 'Bois de chêne vanillé'),
(8, 103, 'Cèdre', 'Bois résineux'),
(9, 103, 'Réglisse', 'Racine sucrée'),

-- Balsamique notes
(10, 104, 'Cire d''abeille', 'Cire naturelle'),
(11, 104, 'Résine', 'Résine de pin');

-- ===========================================
-- SEQUENCE RESET
-- ===========================================
-- Reset sequences to match the highest ID used
SELECT setval('wine_type_id_seq', (SELECT MAX(id) FROM wine_type));
SELECT setval('cepage_id_seq', (SELECT MAX(id) FROM cepage));
SELECT setval('tasting_step_id_seq', (SELECT MAX(id) FROM tasting_step));
SELECT setval('tasting_field_id_seq', (SELECT MAX(id) FROM tasting_field));
SELECT setval('tasting_field_option_id_seq', (SELECT MAX(id) FROM tasting_field_option));
SELECT setval('aroma_note_id_seq', (SELECT MAX(id) FROM aroma_note));