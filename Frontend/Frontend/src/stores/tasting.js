import {ref, computed} from 'vue'
import {defineStore} from 'pinia'

export const useTastingStore = defineStore('tasting', () => {

    const tasting_steps = [
        {
            "step": 1,
            "name": "informations",
            "title": "Informations sur le Vin",
            "fields": [
                {
                    "id": 1,
                    "type": "select-button",
                    "wineType": [],
                    "label": "Type",
                    "name": "type",
                    "values": [
                        {
                            "id": 1,
                            "value": "Rouge",
                            "wineType": "red",
                            "icon": "",
                            "color": "rgb(191, 15, 2)",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 2,
                            "value": "Blanc",
                            "wineType": "white",
                            "icon": "",
                            "color": "rgb(255, 241, 128)",
                            "iconColor": "rgb(255, 241, 128)"
                        },
                        {
                            "id": 3,
                            "value": "Rosé",
                            "wineType": "rose",
                            "icon": "",
                            "color": "rgb(255, 150, 186)",
                            "iconColor": "rgb(255, 150, 186)"
                        }
                    ],
                    "placeholder": "Type de vin",
                    "required": true,
                    "multi": false
                },
                {
                    "id": 2,
                    "type": "autocomplete",
                    "wineType": ["red", "white", "rose"],
                    "label": "Cépage(s)",
                    "name": "cepage",
                    "values": [
                        {
                            "id": 1,
                            "value": "CABERNET-SAUVIGNON",
                            "wineType": ["red", "rose"],
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {"id": 2, "value": "CARIGNAN", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 3, "value": "CINSAULT", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 4, "value": "MALBEC", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 5, "value": "GAMAY", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 6, "value": "GRENACHE", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {
                            "id": 7,
                            "value": "MERLOT",
                            "wineType": ["red", "rose"],
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {"id": 8, "value": "MOURVÈDRE", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 9, "value": "PINOT NOIR", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {
                            "id": 10,
                            "value": "SANGIOVESE",
                            "wineType": "red",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {"id": 11, "value": "SYRAH", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},

                        {
                            "id": 11,
                            "value": "Chardonnay",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 12,
                            "value": "pinot blanc",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 13,
                            "value": "sauvignon blanc",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 14,
                            "value": "grenache blanc",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {"id": 15, "value": "chenin", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {
                            "id": 16,
                            "value": "gewurztraminer",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 17,
                            "value": "riesling",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 18,
                            "value": "viognier",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 19,
                            "value": "sémillon",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 20,
                            "value": "marsanne",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },
                        {
                            "id": 21,
                            "value": "roussanne",
                            "wineType": "white",
                            "icon": "",
                            "iconColor": "rgb(191, 15, 2)"
                        },

                        {"id": 22, "value": "Syrah", "wineType": "rose", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 23, "value": "Grenache", "wineType": "rose", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 24, "value": "Cinsault", "wineType": "rose", "icon": "", "iconColor": "rgb(191, 15, 2)"}
                    ],
                    "placeholder": "Entrez les cépages",
                    "required": true,
                    "multi": true
                },
                {
                    "id": 3,
                    "type": "text",
                    "wineType": ["red", "white", "rose"],
                    "label": "Région",
                    "name": "region",
                    "placeholder": "Entrez la région",
                    "required": true
                },
                {
                    "id": 4,
                    "type": "text",
                    "wineType": ["red", "white", "rose"],
                    "label": "AOP/IGP/VDF",
                    "name": "aop_igp_vdf",
                    "required": true
                },
                {
                    "id": 5,
                    "type": "text",
                    "wineType": ["red", "white", "rose"],
                    "label": "Élevage",
                    "name": "elevage",
                    "placeholder": "Décrivez l'élevage",
                    "required": false
                },
                {
                    "id": 6,
                    "type": "select",
                    "wineType": ["red", "white", "rose"],
                    "label": "Import",
                    "name": "Import",
                    "values": [
                        "/",
                        "Espagne",
                        "Italie",
                        "USA"
                    ],
                    "required": true
                },
                {
                    "id": 7,
                    "type": "number",
                    "wineType": ["red", "white", "rose"],
                    "label": "Prix de Lancement (€)",
                    "name": "prix_lancement",
                    "placeholder": "Entrez le prix de lancement",
                    "required": true
                },
                {
                    "id": 8,
                    "type": "number",
                    "wineType": ["red", "white", "rose"],
                    "label": "Prix Actuel (€)",
                    "name": "prix_actuel",
                    "placeholder": "Entrez le prix actuel",
                    "required": true
                }
            ]
        },
        {
            "step": 2,
            "name": "visuel",
            "title": "Aspect Visuel",
            "fields": [
                {
                    "label": "Robe rouge",
                    "name": "robe_rouge",
                    "type": "select-button",
                    "wineType": ["red"],
                    "values": [
                        {
                            "id": 1,
                            "value": "Noir",
                            "wineType": ["red"],
                            "color": "#000",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "bleuâtre",
                            "wineType": ["red"],
                            "color": "#3b0014",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "violet",
                            "wineType": ["red"],
                            "color": "#901766",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "pourpre",
                            "wineType": ["red"],
                            "color": "#9a2049",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "grenat",
                            "wineType": ["red"],
                            "color": "#6e2445",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "rubis",
                            "wineType": ["red"],
                            "color": "#d73c3a",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 7,
                            "value": "incarnat et carmin",
                            "wineType": ["red"],
                            "color": "#b91b18",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 8,
                            "value": "brique",
                            "wineType": ["red"],
                            "color": "#be4731",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 9,
                            "value": "tuilé",
                            "wineType": ["red"],
                            "color": "#8c3116",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 10,
                            "value": "Orangé ",
                            "wineType": ["red"],
                            "color": "#c63d11",
                            "icon": "",
                            "iconColor": ""
                        },
                    ],
                    "required": true,
                    "multi": false
                },
                {
                    "label": "Robe blanche",
                    "name": "robe_blanche",
                    "type": "select-button",
                    "wineType": ["white"],
                    "values": [
                        {
                            "id": 1,
                            "value": "blanc",
                            "wineType": ["white"],
                            "color": "#f6f2e7",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "jaune-vert",
                            "wineType": ["white"],
                            "color": "#e1db85",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "jaune pâle",
                            "wineType": ["white"],
                            "color": "#f3e978",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "paille",
                            "wineType": ["white"],
                            "color": "#e6cf45",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "or",
                            "wineType": ["white"],
                            "color": "#d7b643",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "doré",
                            "wineType": ["white"],
                            "color": "#d19c36",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 7,
                            "value": "ambré",
                            "wineType": ["white"],
                            "color": "#d18539",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 8,
                            "value": "madérisé",
                            "wineType": ["white"],
                            "color": "#a78a3e",
                            "icon": "",
                            "iconColor": ""
                        }
                    ],
                    "required": true,
                    "multi": false
                },
                {
                    "label": "Disque",
                    "name": "disque",
                    "type": "select-button",
                    "wineType": ["red"],
                    "values": [
                        {"id": 1, "value": "violacé", "wineType": ["red"], "icon": "", "iconColor": ""},
                        {"id": 2, "value": "rosé", "wineType": ["red"], "icon": "", "iconColor": ""},
                        {"id": 3, "value": "brun", "wineType": ["red"], "icon": "", "iconColor": ""},
                        {"id": 4, "value": "tuilé", "wineType": ["red"], "icon": "", "iconColor": ""},
                        {"id": 5, "value": "orangé", "wineType": ["red"], "icon": "", "iconColor": ""}
                    ],
                    "required": true,
                    "multi": false
                },
                {
                    "label": "Robe rosé",
                    "name": "robe_rose",
                    "type": "select-button",
                    "wineType": ["rose"],
                    "values": [
                        {
                            "id": 1,
                            "value": "gris",
                            "wineType": ["rose"],
                            "color": "#f0cbb8",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "rosé",
                            "wineType": ["rose"],
                            "color": "#e7a39a",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "rose violacé",
                            "wineType": ["rose"],
                            "color": "#ee537b",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "rosé orangé",
                            "wineType": ["rose"],
                            "color": "#eb7d70",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "roux",
                            "wineType": ["rose"],
                            "color": "#e89e59",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "orangé",
                            "wineType": ["rose"],
                            "color": "#e27240",
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 7,
                            "value": "brun",
                            "wineType": ["rose"],
                            "color": "#c95a2c",
                            "icon": "",
                            "iconColor": ""
                        },
                    ],
                    "required": true,
                    "multi": false
                },
                {
                    "type": "select-button",
                    "label": "Intensité",
                    "name": "intensite",
                    "wineType": ["red", "white", "rose"],
                    "values": [
                        {"id": 1, "value": "pâle", "wineType": ["red", "white", "rose"], "icon": "", "iconColor": ""},
                        {"id": 2, "value": "légère", "wineType": ["red", "white", "rose"], "icon": "", "iconColor": ""},
                        {"id": 3, "value": "faible", "wineType": ["red", "white", "rose"], "icon": "", "iconColor": ""},
                        {
                            "id": 4,
                            "value": "peu intense",
                            "wineType": ["red", "white", "rose"],
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "moyenne",
                            "wineType": ["red", "white", "rose"],
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "soutenue",
                            "wineType": ["red", "white", "rose"],
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 7,
                            "value": "intense",
                            "wineType": ["red", "white", "rose"],
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 8,
                            "value": "profonde",
                            "wineType": ["red", "white", "rose"],
                            "icon": "",
                            "iconColor": ""
                        },
                        {"id": 9, "value": "opaque", "wineType": ["red", "white", "rose"], "icon": "", "iconColor": ""},
                    ],
                    "required": true,
                    "multi": true
                },
                {
                    "type": "select-button",
                    "label": "Limpidité",
                    "name": "limpidite",
                    "wineType": ["red", "white", "rose"],
                    "values": [
                        {
                            "id": 1,
                            "value": "voilée",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "trouble",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "louche",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "claire",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "limpide",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "parfaite",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        }
                    ],
                    "placeholder": "Type de vin",
                    "multi": false,
                    "required": true
                },
                {
                    "type": "select-button",
                    "label": "Brillance",
                    "name": "brillance",
                    "wineType": ["red", "white", "rose"],
                    "values": [
                        {
                            "id": 1,
                            "value": "éteint",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "terne",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "mat",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "cristalline",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "brillante",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "éclatante",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        }
                    ],
                    "multi": false,
                    "required": true
                },
                {
                    "type": "select-button",
                    "label": "Evolution",
                    "name": "evolution",
                    "wineType": ["red", "white", "rose"],
                    "values": [
                        {
                            "id": 1,
                            "value": "nulle",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 2,
                            "value": "légère",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 3,
                            "value": "moyenne",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 4,
                            "value": "évidente",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 5,
                            "value": "avancée",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 6,
                            "value": "forte",
                            "wineType": ["red", "white", "rose"],
                            "negatif": false,
                            "icon": "",
                            "iconColor": ""
                        },
                        {
                            "id": 7,
                            "value": "excessive",
                            "wineType": ["red", "white", "rose"],
                            "negatif": true,
                            "icon": "",
                            "iconColor": ""
                        }
                    ],
                    "multi": false,
                    "required": true
                },
                {
                    "id": 5,
                    "type": "textarea",
                    "wineType": ["red", "white", "rose"],
                    "label": "Remarques",
                    "name": "remarques",
                    "required": false
                }
            ]
        },
        {
            "step": 3,
            "title": "Nez (Arômes)",
            "name": "nez",
            "fields": [
                {
                    "type": "select",
                    "label": "Intensité",
                    "name": "intensite_nez",
                    "values": [
                        "fermée",
                        "retenue",
                        "faible",
                        "discrète",
                        "moyenne",
                        "bonne",
                        "très bonne",
                        "forte",
                        "puissante"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Qualité",
                    "name": "qualite_nez",
                    "options": [
                        "altérée",
                        "douteuse",
                        "franche",
                        "nette",
                        "parfaite"
                    ],
                    "required": true
                },
                {
                    "type": "checkbox_group",
                    "label": "Arômes Nature",
                    "name": "aromes_nature",
                    "options": [
                        "fruitée",
                        "florale",
                        "boisée",
                        "balsamique",
                        "épicée",
                        "empyreumatique",
                        "animale",
                        "végétale",
                        "alimentaire",
                        "minérale",
                        "chimique",
                        "anormale"
                    ],
                    "required": false
                }
            ]
        },
        {
            "step": 4,
            "title": "Bouche",
            "name": "bouche",
            "fields": [
                {
                    "type": "select",
                    "label": "Attaque",
                    "name": "attaque",
                    "options": [
                        "dure",
                        "mordante",
                        "acide",
                        "vive",
                        "souple",
                        "ronde",
                        "molle",
                        "plate"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Evolution",
                    "name": "evolution",
                    "options": [
                        "ample",
                        "puissante",
                        "généreuse",
                        "continue",
                        "tannique",
                        "acide",
                        "plate"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Structure",
                    "name": "structure",
                    "options": [
                        "concentrée",
                        "souple",
                        "charnue",
                        "décharnée",
                        "creuse",
                        "maigre",
                        "grossière",
                        "sèche",
                        "dure",
                        "filiforme"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Texture",
                    "name": "texture",
                    "options": [
                        "aqueuse",
                        "fluide",
                        "légère",
                        "concentrée",
                        "moelleuse",
                        "onctueuse",
                        "épaisse",
                        "astringente",
                        "veloutée",
                        "soyeuse",
                        "fondue"
                    ],
                    "required": false
                }
            ]
        },
        {
            "step": 5,
            "title": "Conclusion",
            "name": "conclusion",
            "fields": [
                {
                    "type": "select",
                    "label": "Note Finale",
                    "name": "note_finale",
                    "options": [
                        "très médiocre",
                        "médiocre",
                        "mauvais",
                        "passable",
                        "correct",
                        "bon",
                        "très bon",
                        "superbe",
                        "excellent",
                        "exceptionnel",
                        "légendaire"
                    ],
                    "required": true
                },
                {
                    "type": "number",
                    "label": "Caudalies",
                    "name": "caudalies",
                    "placeholder": "Nombre de caudalies",
                    "required": false
                },
                {
                    "type": "textarea",
                    "label": "Remarques",
                    "name": "remarques",
                    "placeholder": "Ajouter des remarques",
                    "required": false
                },
                {
                    "type": "text",
                    "label": "Accords Mets et Vins",
                    "name": "accords_mets_vins",
                    "placeholder": "Ex: Fromages, viandes",
                    "required": false
                }
            ]
        }
    ]

    // const myTastings = [
    //     {
    //         "vin": {
    //             "cepage": [],
    //             "region": "",
    //             "aop_igp_vdf": "",
    //             "elevage": "",
    //             "import": "",
    //             "prix_lancement": 0,
    //             "prix_actuel": 0,
    //             "aspect_visuel": {
    //                 "robe": {
    //                     "couleur": "",
    //                     "disque": "",
    //                     "intensité": {},
    //                     "limpidité": {},
    //                     "brillance": {},
    //                     "evolution": {},
    //                     "remarques": ""
    //
    //                 },
    //                 "teinte": "",
    //                 "disque": "",
    //                 "intensite": "",
    //                 "limpidite": "",
    //                 "brillance": "",
    //                 "evolution": "",
    //                 "remarques": ""
    //             },
    //             "nez": {
    //                 "intensite": "",
    //                 "qualite": "",
    //                 "aromes": {
    //                     "type": "",
    //                     "nature": []
    //                 },
    //                 "description": ""
    //             },
    //             "bouche": {
    //                 "attaque": "",
    //                 "evolution": "",
    //                 "fin_de_bouche": "",
    //                 "structure": "",
    //                 "texture": "",
    //                 "intensite": "",
    //                 "qualite": "",
    //                 "equilibre": "",
    //                 "aromes": [],
    //                 "longueur_en_bouche": "",
    //                 "persistance_aromatique": "",
    //                 "remarques": ""
    //             },
    //             "notes": {
    //                 "caudalies": 0,
    //                 "note_finale": {
    //                     "tres_mediocre": false,
    //                     "mediocre": false,
    //                     "mauvais": false,
    //                     "passable": false,
    //                     "correct": false,
    //                     "bon": false,
    //                     "tres_bon": false,
    //                     "superbe": false,
    //                     "excellent": false,
    //                     "exceptionnel": false,
    //                     "legendaire": false
    //                 },
    //                 "conclusion": ""
    //             },
    //             "temperature_ideale_de_consommation": 0,
    //             "date_ideale_de_consommation": "",
    //             "evolution_probable": "",
    //             "accords_mets_vins": []
    //         }
    //     }
    // ]

    // const doubleCount = computed(() => count.value * 2)

    function increment() {
        // count.value++
    }

    return {tasting_steps, increment}
})