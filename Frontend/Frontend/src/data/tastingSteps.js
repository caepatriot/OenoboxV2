export const tastingStepsData = [
  {
    "step": 1,
    "name": "informations",
    "title": "Informations sur le Vin",
    "fields": [
      {
        "id": 1,
        "label": "Type",
        "name": "type",
        "placeholder": "Type de vin",
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "Rouge",
                "wineType": "red",
                "negatif": false,
                "icon": "",
                "color": "rgb(191, 15, 2)",
                "iconColor": "rgb(191, 15, 2)"
              },
              {
                "id": 2,
                "value": "Blanc",
                "wineType": "white",
                "negatif": false,
                "icon": "",
                "color": "rgb(255, 241, 128)",
                "iconColor": "rgb(255, 241, 128)"
              },
              {
                "id": 3,
                "value": "Rosé",
                "wineType": "rose",
                "negatif": false,
                "icon": "",
                "color": "rgb(255, 150, 186)",
                "iconColor": "rgb(255, 150, 186)"
              }
            ],
            "multi": false,
            "required": true
          }
        ]
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
          {"id": 2, "value": "CARIGNAN", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {"id": 3, "value": "CINSAULT", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {"id": 4, "value": "MALBEC", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {"id": 5, "value": "GAMAY", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {"id": 6, "value": "GRENACHE", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {
            "id": 7,
            "value": "MERLOT",
            "wineType": ["red", "rose"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 8,
            "value": "MOURVÈDRE",
            "wineType": ["red"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 9,
            "value": "PINOT NOIR",
            "wineType": ["red"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 10,
            "value": "SANGIOVESE",
            "wineType": ["red"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {"id": 11, "value": "SYRAH", "wineType": ["red"], "icon": "", "iconColor": "rgb(191, 15, 2)"},

          {
            "id": 12,
            "value": "Chardonnay",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 13,
            "value": "pinot blanc",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 14,
            "value": "sauvignon blanc",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 15,
            "value": "grenache blanc",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 16,
            "value": "chenin",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 17,
            "value": "gewurztraminer",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 18,
            "value": "riesling",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 19,
            "value": "viognier",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 20,
            "value": "sémillon",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 21,
            "value": "marsanne",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 22,
            "value": "roussanne",
            "wineType": ["white"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },

          {"id": 23, "value": "Syrah", "wineType": ["rose"], "icon": "", "iconColor": "rgb(191, 15, 2)"},
          {
            "id": 24,
            "value": "Grenache",
            "wineType": ["rose"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          },
          {
            "id": 25,
            "value": "Cinsault",
            "wineType": ["rose"],
            "icon": "",
            "iconColor": "rgb(191, 15, 2)"
          }
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
        "id": 1,
        "label": "Robe rouge",
        "name": "robe_rouge",
        "wineType": ["red"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
              {
                "id": 1,
                "value": "Noir",
                "wineType": ["red"],
                "negatif": false,
                "color": "#000",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "bleuâtre",
                "wineType": ["red"],
                "negatif": false,
                "color": "#3b0014",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "violet",
                "wineType": ["red"],
                "negatif": false,
                "color": "#901766",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 4,
                "value": "pourpre",
                "wineType": ["red"],
                "negatif": false,
                "color": "#9a2049",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 5,
                "value": "grenat",
                "wineType": ["red"],
                "negatif": false,
                "color": "#6e2445",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 6,
                "value": "rubis",
                "wineType": ["red"],
                "negatif": false,
                "color": "#d73c3a",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 7,
                "value": "incarnat et carmin",
                "wineType": ["red"],
                "negatif": false,
                "color": "#b91b18",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 8,
                "value": "brique",
                "wineType": ["red"],
                "negatif": false,
                "color": "#be4731",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 9,
                "value": "tuilé",
                "wineType": ["red"],
                "negatif": false,
                "color": "#8c3116",
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 10,
                "value": "Orangé ",
                "wineType": ["red"],
                "negatif": false,
                "color": "#c63d11",
                "icon": "",
                "iconColor": ""
              }
            ]
          }
        ]
      },
      {
        "id": 2,
        "label": "Robe blanche",
        "name": "robe_blanche",
        "wineType": ["white"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["white"],
            "multi": false,
            "required": true,
            "groupValues": [

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
            ]
          }
        ]
      },

      {
        "id": 3,
        "label": "Disque",
        "name": "disque",
        "wineType": ["red"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red"],
            "multi": false,
            "required": true,
            "groupValues": [
              {"id": 1, "value": "violacé", "wineType": ["red"], "icon": "", "iconColor": ""},
              {"id": 2, "value": "rosé", "wineType": ["red"], "icon": "", "iconColor": ""},
              {"id": 3, "value": "brun", "wineType": ["red"], "icon": "", "iconColor": ""},
              {"id": 4, "value": "tuilé", "wineType": ["red"], "icon": "", "iconColor": ""},
              {"id": 5, "value": "orangé", "wineType": ["red"], "icon": "", "iconColor": ""}
            ]
          }
        ]
      },
      {
        "id": 4,
        "label": "Robe rosé",
        "name": "robe_rose",
        "wineType": ["rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["rose"],
            "multi": false,
            "required": true,
            "groupValues": [
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
              }
            ]
          }
        ]
      },
      {
        "id": 5,
        "label": "Intensité",
        "name": "intensite",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
              {
                "id": 1,
                "value": "pâle",
                "wineType": ["red", "white", "rose"],
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "légère",
                "wineType": ["red", "white", "rose"],
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "faible",
                "wineType": ["red", "white", "rose"],
                "icon": "",
                "iconColor": ""
              },
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
              {
                "id": 9,
                "value": "opaque",
                "wineType": ["red", "white", "rose"],
                "icon": "",
                "iconColor": ""
              }
            ]
          }
        ]
      },
      {
        "id": 6,
        "label": "Limpidité",
        "name": "limpidite",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
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
            ]
          }
        ]
      },
      {
        "id": 7,
        "label": "Brillance",
        "name": "brillance",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
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
            ]
          }
        ]
      },
      {
        "id": 8,
        "label": "Evolution",
        "name": "evolution",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
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
            ]
          }
        ]
      },
      {
        "id": 9,
        "type": "textarea",
        "label": "Remarques",
        "name": "remarques",
        "wineType": ["red", "white", "rose"],
        "required": false
      }
    ]
  },
  {
    "step": 3,
    "title": "Nez",
    "name": "nez",
    "fields": [
      {
        "id": 1,
        "label": "Intensité",
        "name": "intensite",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
              {
                "id": 1,
                "value": "fermée",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "retenue",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "faible",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 4,
                "value": "discrète",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 5,
                "value": "moyenne",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 6,
                "value": "bonne",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 7,
                "value": "très bonne",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 8,
                "value": "forte",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 9,
                "value": "puissante",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              }
            ]
          }
        ]
      },
      {
        "id": 2,
        "label": "Qualité",
        "name": "qualite",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
              {
                "id": 1,
                "value": "altérée",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "douteuse",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "franche",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 4,
                "value": "nette",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 5,
                "value": "parfaite",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              }
            ]
          }
        ]
      },
      {
        "id": 3,
        "label": "Arômes-Type",
        "name": "type_aromes",
        "wineType": ["red", "white", "rose"],
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "multi": false,
            "required": true,
            "groupValues": [
              {
                "id": 1,
                "value": "simple",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "complexe",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              }
            ]
          },
          {
            "id": 2,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "grossier",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "fin",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "élégant",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              }
            ],
            "multi": true,
            "required": false
          },
          {
            "id": 3,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "racé",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              }
            ],
            "multi": false,
            "required": false
          },
          {
            "id": 4,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "vif",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "frais",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "neutre",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 4,
                "value": "suave",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 5,
                "value": "lourd",
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
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "désagréable",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "commun",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "agréable",
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
            "id": 6,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "épanoui",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "évolué",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "passé",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              }
            ],
            "multi": false,
            "required": false
          },
          {
            "id": 7,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "rôti",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              }
            ],
            "multi": false,
            "required": false
          },
          {
            "id": 8,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "franc",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 2,
                "value": "strict",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 3,
                "value": "violent",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "",
                "iconColor": ""
              }
            ],
            "multi": false,
            "required": false
          }
        ]
      },
      {
        "id": 4,
        "label": "Aromes-Nature",
        "name": "nature_aromes",
        "wineType": ["red", "white", "rose"],
        "placeholder": "Type de vin",
        "groups": [
          {
            "id": 1,
            "type": "select-button",
            "wineType": ["red", "white", "rose"],
            "groupValues": [
              {
                "id": 1,
                "value": "fruitée",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi mdi-food-apple",
                "iconColor": "",
                "notes": [
                  {
                    "id": 1,
                    "libelle": "Citrons",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 2,
                    "libelle": "Pamplemousse",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 3,
                    "libelle": "Orange",
                    "details": "XXXXXXXXXX"
                  }
                ]
              },
              {
                "id": 2,
                "value": "florale",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-flower",
                "iconColor": "",
                "notes": [
                  {
                    "id": 4,
                    "libelle": "Aubépine",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 5,
                    "libelle": "Acacia",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 6,
                    "libelle": "Tilleul",
                    "details": "XXXXXXXXXX"
                  }
                ]
              },
              {
                "id": 3,
                "value": "boisée",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-forest",
                "iconColor": "",
                "notes": [
                  {
                    "id": 7,
                    "libelle": "Chêne",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 8,
                    "libelle": "Cèdre",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 9,
                    "libelle": "Réglisse",
                    "details": "XXXXXXXXXX"
                  }
                ]
              },
              {
                "id": 4,
                "value": "balsamique",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-leaf-maple",
                "iconColor": "",
                "notes": [
                  {
                    "id": 10,
                    "libelle": "Cire d'abeille",
                    "details": "XXXXXXXXXX"
                  },
                  {
                    "id": 11,
                    "libelle": "Résine",
                    "details": "XXXXXXXXXX"
                  }
                ]
              },
              {
                "id": 5,
                "value": "épicée",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-chili-mild",
                "iconColor": ""
              },
              {
                "id": 6,
                "value": "empyreumatique",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-fire-circle",
                "iconColor": ""
              },
              {
                "id": 7,
                "value": "animale",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-cow",
                "iconColor": ""
              },
              {
                "id": 8,
                "value": "végétale",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-grass",
                "iconColor": ""
              },
              {
                "id": 9,
                "value": "alimentaire",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-food-fork-drink",
                "iconColor": ""
              },
              {
                "id": 10,
                "value": "minérale",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "",
                "iconColor": ""
              },
              {
                "id": 11,
                "value": "chimique",
                "wineType": ["red", "white", "rose"],
                "negatif": false,
                "icon": "mdi-atom-variant",
                "iconColor": ""
              },
              {
                "id": 12,
                "value": "anormale",
                "wineType": ["red", "white", "rose"],
                "negatif": true,
                "icon": "mdi-close",
                "iconColor": ""
              }
            ],
            "multi": true,
            "required": true
          }
        ]
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
    "title": "Longueur en bouche",
    "name": "longueur",
    "fields": [
      {
        "type": "slider",
        "label": "Persistance aromatique",
        "name": "persistance_aromatique",
        "options": [
          "nulle",
          "courte",
          "moyenne",
          "bonne",
          "longue",
          "très longue",
          "infinie"
        ],
        "required": true
      },
      {
        "type": "select",
        "label": "Caudalies",
        "name": "caudalies",
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
      }
    ]
  }
]
