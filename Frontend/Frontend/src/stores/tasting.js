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
                    "options": [
                        {"id" : 1, "type": "Rouge", "icon": "mdi-glass-wine", "iconColor" : "#CD0E00"},
                        {"id" : 2, "type": "Blanc", "icon": "mdi-glass-wine", "iconColor" : "#dcc70a"},
                        {"id" : 3, "type": "Rosé", "icon": "mdi-glass-wine", "iconColor" : "#fe7da9"}
                    ],
                    "placeholder": "Type de vin",
                    "required": true,
                    "multi": false
                },
                {
                    "id": 2,
                    "type": "text",
                    "label": "Cépage(s)",
                    "name": "cepages",
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
                    "type": "select",
                    "label": "AOP/IGP/VDF",
                    "name": "aop_igp_vdf",
                    "options": [
                        "/",
                        "AOP",
                        "IGP",
                        "VDF"
                    ],
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
                    "options": [
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
                    "type": "select",
                    "label": "Robe",
                    "name": "robe",
                    "options": [
                        "blanc",
                        "jaune-vert",
                        "jaune pâle",
                        "paille",
                        "or",
                        "doré",
                        "ambré",
                        "madérisé",
                        "noir",
                        "bleuâtre",
                        "violet",
                        "pourpre",
                        "grenat",
                        "rubis",
                        "incarnat",
                        "carmin",
                        "brique",
                        "tuilé"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Disque",
                    "name": "disque",
                    "options": [
                        "violacé",
                        "rosé",
                        "brun",
                        "tuilé",
                        "orangé"
                    ],
                    "required": false
                },
                {
                    "type": "select",
                    "label": "Intensité",
                    "name": "intensite",
                    "options": [
                        "pâle",
                        "légère",
                        "faible",
                        "peu intense",
                        "moyenne",
                        "soutenue",
                        "intense",
                        "profonde",
                        "opaque"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Limpidité",
                    "name": "limpidite",
                    "options": [
                        "voilée",
                        "trouble",
                        "louche",
                        "claire",
                        "limpide",
                        "parfaite"
                    ],
                    "required": true
                },
                {
                    "type": "select",
                    "label": "Brillance",
                    "name": "brillance",
                    "options": [
                        "éteint",
                        "terne",
                        "mat",
                        "cristalline",
                        "brillante",
                        "éclatante"
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
                    "options": [
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

    const vins = [
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
                    "robe": "",
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