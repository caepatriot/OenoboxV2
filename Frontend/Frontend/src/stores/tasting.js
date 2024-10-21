import {ref, computed} from 'vue'
import {defineStore} from 'pinia'

export const useTastingStore = defineStore('tasting', () => {

    const tasting_steps = [
        {
            "step": 1,
            "title": "Informations sur le Vin",
            "fields": [
                {
                    "id": 1,
                    "type": "select-button",
                    "label": "Type",
                    "name": "type",
                    "values": [
                        {"id": 1, "value": "Rouge", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 2, "value": "Blanc", "wineType": "white", "icon": "", "iconColor": "rgb(255, 241, 128)"},
                        {"id": 3, "value": "Rosé", "wineType": "rose", "icon": "", "iconColor": "rgb(255, 150, 186)"}
                    ],
                    "placeholder": "Type de vin",
                    "required": true,
                    "multi": false
                },
                {
                    "id": 2,
                    "type": "autocomplete",
                    "label": "Cépage(s)",
                    "name": "cepage",
                    "values": [
                        {"id": 1, "value": "CABERNET-SAUVIGNON", "wineType": ["red", "rose"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 2, "value": "CARIGNAN", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 3, "value": "CINSAULT", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 4, "value": "MALBEC", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 5, "value": "GAMAY", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 6, "value": "GRENACHE", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 7, "value": "MERLOT", "wineType": ["red", "rose"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 8, "value": "MOURVÈDRE", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 9, "value": "PINOT NOIR", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 10, "value": "SANGIOVESE", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 11, "value": "SYRAH", "wineType": "red", "icon": "", "iconColor": "rgb(191, 15, 2)"},

                        {"id": 11, "value": "Chardonnay", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 12, "value": "pinot blanc", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 13, "value": "sauvignon blanc", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 14, "value": "grenache blanc", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 15, "value": "chenin", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 16, "value": "gewurztraminer", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 17, "value": "riesling", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 18, "value": "viognier", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 19, "value": "sémillon", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 20, "value": "marsanne", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 21, "value": "roussanne", "wineType": "white", "icon": "", "iconColor": "rgb(191, 15, 2)"},

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
                    "label": "Région",
                    "name": "region",
                    "placeholder": "Entrez la région",
                    "required": true
                },
                {
                    "id": 4,
                    "type": "text",
                    "label": "AOP/IGP/VDF",
                    "name": "aop_igp_vdf",
                    "required": true
                },
                {
                    "id": 5,
                    "type": "text",
                    "label": "Élevage",
                    "name": "elevage",
                    "placeholder": "Décrivez l'élevage",
                    "required": false
                },
                {
                    "id": 6,
                    "type": "select",
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
                    "label": "Prix de Lancement (€)",
                    "name": "prix_lancement",
                    "placeholder": "Entrez le prix de lancement",
                    "required": true
                },
                {
                    "id": 8,
                    "type": "number",
                    "label": "Prix Actuel (€)",
                    "name": "prix_actuel",
                    "placeholder": "Entrez le prix actuel",
                    "required": true
                }
            ]
        },
        {
            "step": 2,
            "title": "Aspect Visuel",
            "fields": [
                {
                    // "type": "select",
                    "label": "Robe",
                    "name": "robe",


                    "type": "select-button",
                    "options": [
                        {"id": 1, "type": "Rouge", "icon": "", "iconColor": "rgb(191, 15, 2)"},
                        {"id": 2, "type": "Blanc", "icon": "", "iconColor": "rgb(255, 241, 128)"},
                        {"id": 3, "type": "Rosé", "icon": "", "iconColor": "rgb(255, 150, 186)"}
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Intensité",
                    "name": "intensite",
                    "values": [
                        {
                            "titre": "pâle"
                        },
                        {
                            "titre": "légère"
                        },
                        {
                            "titre": "faible"
                        },
                        {
                            "titre": "peu intense"
                        },
                        {
                            "titre": "moyenne"
                        },
                        {
                            "titre": "soutenue"
                        },
                        {
                            "titre": "intense"
                        },
                        {
                            "titre": "profonde"
                        },
                        {
                            "titre": "opaque"
                        },
                    ],
                    "required": true,
                    "multi": true
                },
                {
                    "type": "select",
                    "label": "Limpidité",
                    "name": "limpidite",
                    "values": [
                        {
                            "voilée": "voilée",
                            "negatif": true
                        },
                        {
                            "voilée": "trouble",
                            "negatif": true
                        },
                        {
                            "voilée": "louche",
                            "negatif": true
                        },
                        {
                            "voilée": "claire",
                            "negatif": false
                        },
                        {
                            "voilée": "limpide",
                            "negatif": false
                        },
                        {
                            "voilée": "parfaite",
                            "negatif": false
                        },
                    ],
                    "placeholder": "Type de vin",
                    "multi": false,


                    // "values": [
                    //     {
                    //         "type": "Rouge",
                    //         "disques": [
                    //             {
                    //                 "titre": "violacé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "rosé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "brun",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "tuilé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "orangé",
                    //                 "couleur": ""
                    //             },
                    //         ],
                    //         "couleurs": [
                    //             {
                    //                 "titre": "noir",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "bleuâtre",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "violet",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "pourpre",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "grenat",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "rubis",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "incarnat et carmin",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "brique",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "tuilé",
                    //                 "couleur": ""
                    //             }
                    //         ]
                    //     },
                    //     {
                    //         "type": "Blanc",
                    //         "couleurs": [
                    //             {
                    //                 "titre": "blanc",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "jaune-vert",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "jaune pâle",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "paille",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "or",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "doré",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "ambré",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "madérisé",
                    //                 "couleur": ""
                    //             }
                    //         ]
                    //     },
                    //     {
                    //         "type": "Rosé",
                    //         "couleurs": [
                    //             {
                    //                 "titre": "gris",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "rosé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "rose violacé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "rosé orangé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "roux",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "orangé",
                    //                 "couleur": ""
                    //             },
                    //             {
                    //                 "titre": "brun",
                    //                 "couleur": ""
                    //             }
                    //         ]
                    //     },
                    // ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Brillance",
                    "name": "brillance",
                    "values": [
                        {
                            "voilée": "éteint",
                            "negatif": true
                        },
                        {
                            "voilée": "terne",
                            "negatif": true
                        },
                        {
                            "voilée": "mat",
                            "negatif": true
                        },
                        {
                            "voilée": "cristalline",
                            "negatif": false
                        },
                        {
                            "voilée": "brillante",
                            "negatif": false
                        },
                        {
                            "voilée": "éclatante",
                            "negatif": false
                        },
                    ],
                    "required": true
                }
            ]
        },
        {
            "step": 3,
            "title": "Nez (Arômes)",
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

    const myTastings = [
        {
            "vin": {
                "cepage": [],
                "region": "",
                "aop_igp_vdf": "",
                "elevage": "",
                "import": "",
                "prix_lancement": 0,
                "prix_actuel": 0,
                "aspect_visuel": {
                    "robe": {
                        "couleur": "",
                        "disque": "",
                        "intensité": {},
                        "limpidité": {},
                        "brillance": {},
                        "evolution": {},
                        "remarques": ""

                    },
                    "teinte": "",
                    "disque": "",
                    "intensite": "",
                    "limpidite": "",
                    "brillance": "",
                    "evolution": "",
                    "remarques": ""
                },
                "nez": {
                    "intensite": "",
                    "qualite": "",
                    "aromes": {
                        "type": "",
                        "nature": []
                    },
                    "description": ""
                },
                "bouche": {
                    "attaque": "",
                    "evolution": "",
                    "fin_de_bouche": "",
                    "structure": "",
                    "texture": "",
                    "intensite": "",
                    "qualite": "",
                    "equilibre": "",
                    "aromes": [],
                    "longueur_en_bouche": "",
                    "persistance_aromatique": "",
                    "remarques": ""
                },
                "notes": {
                    "caudalies": 0,
                    "note_finale": {
                        "tres_mediocre": false,
                        "mediocre": false,
                        "mauvais": false,
                        "passable": false,
                        "correct": false,
                        "bon": false,
                        "tres_bon": false,
                        "superbe": false,
                        "excellent": false,
                        "exceptionnel": false,
                        "legendaire": false
                    },
                    "conclusion": ""
                },
                "temperature_ideale_de_consommation": 0,
                "date_ideale_de_consommation": "",
                "evolution_probable": "",
                "accords_mets_vins": []
            }
        }
    ]

    // const doubleCount = computed(() => count.value * 2)

    function increment() {
        // count.value++
    }

    return {tasting_steps, increment}
})