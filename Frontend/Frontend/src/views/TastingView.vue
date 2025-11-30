<script setup>
import {ref, reactive, onMounted, computed, toRaw} from 'vue';
import {useTastingStore} from "@/stores/tasting.js";

const store = useTastingStore();

const testTaste = ref({});

const selectedTasting = reactive({
  "vin": {
    "informations": {
      "type": {},
      "cepage": [],
      "region": "",
      "aop_igp_vdf": "",
      "elevage": "",
      "import": "",
      "prix_lancement": 0,
      "prix_actuel": 0,
    },
    "visuel": {
      "robe_blanche": {},
      "robe_rouge": {},
      "robe_rose": {},
      "disque": {},
      "intensite": {},
      "limpidite": {},
      "brillance": {},
      "evolution": {},
      "remarques": ""
    },
    "nez": {
      "intensite": {},
      "qualite": {},
      "type_aromes": [],
      "selectedCategories": [],
      "nature_aromes": [
        {
          "id": "1",
          "fruite": []
        },
        {
          "id": "2",
          "florale": []
        },
        {
          "id": "3",
          "boisee": []
        },
        {
          "id": "4",
          "balsamique": []
        },
        {
          "id": "5",
          "epicee": []
        },
        {
          "id": "6",
          "empyreumatique": []
        },
        {
          "id": "7",
          "animale": []
        },
        {
          "id": "8",
          "vegetale": []
        },
        {
          "id": "9",
          "alimentaire": []
        },
        {
          "id": "10",
          "minerale": []
        },
        {
          "id": "11",
          "chimique": []
        },
        {
          "id": "12",
          "anormale": []
        }
      ],
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
    "conclusion": {
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
    "autres": {
      "temperature_ideale_de_consommation": 0,
      "date_ideale_de_consommation": "",
      "evolution_probable": "",
      "accords_mets_vins": []
    }
  }
});

// State for edit mode
const isEditMode = ref(false);
const editingTastingId = ref(null);

const selected = ref({});
const selectedNotes = reactive({});

const myWines = reactive([]);

const currentStep = ref(1);

const steps = computed(() => store.tasting_steps);

const tabImg = ref("one");

const drawer = ref(false);

onMounted(async () => {
  document.body.addEventListener('mousemove', handleMouseMove)

  // Load tasting steps from API
  await store.loadTastingSteps()

  // Load existing tastings
  try {
    const tastings = await store.getTastings();
    myWines.splice(0, myWines.length, ...tastings);
  } catch (error) {
    console.error('Failed to load tastings:', error);
  }
});


const submitForm = async () => {
  try {
    console.log(toRaw(selectedTasting)); // This will log the full tasting data object

    // Convert form data to API format
    const apiData = convertFormDataToApiFormat(toRaw(selectedTasting));

    let savedTasting;

    if (isEditMode.value) {
      // Update existing tasting
      savedTasting = await store.updateTasting(editingTastingId.value, apiData);

      // Update in local list
      const index = myWines.findIndex(t => t.id === editingTastingId.value);
      if (index > -1) {
        myWines[index] = savedTasting;
      }

      alert('Dégustation mise à jour avec succès !');
    } else {
      // Create new tasting
      savedTasting = await store.createTasting(apiData);

      // Add to local list
      myWines.push(savedTasting);

      alert('Dégustation enregistrée avec succès !');
    }

    // Reset form
    resetForm();

  } catch (error) {
    console.error('Erreur lors de l\'enregistrement:', error);
    alert('Erreur lors de l\'enregistrement de la dégustation. Veuillez réessayer.');
  }
};

// Convert form data structure to API format
const convertFormDataToApiFormat = (formData) => {
  return {
    wineType: formData.vin.informations.type.wineType,
    cepages: formData.vin.informations.cepage,
    region: formData.vin.informations.region,
    aopIgpVdf: formData.vin.informations.aop_igp_vdf,
    elevage: formData.vin.informations.elevage,
    wineImport: formData.vin.informations.import,
    prixLancement: formData.vin.informations.prix_lancement,
    prixActuel: formData.vin.informations.prix_actuel,

    // Visual aspect - extract string values from objects
    robeRouge: typeof formData.vin.visuel.robe_rouge === 'object' ? formData.vin.visuel.robe_rouge?.value || '' : formData.vin.visuel.robe_rouge || '',
    robeBlanche: typeof formData.vin.visuel.robe_blanche === 'object' ? formData.vin.visuel.robe_blanche?.value || '' : formData.vin.visuel.robe_blanche || '',
    robeRose: typeof formData.vin.visuel.robe_rose === 'object' ? formData.vin.visuel.robe_rose?.value || '' : formData.vin.visuel.robe_rose || '',
    disque: formData.vin.visuel.disque,
    intensiteVisuelle: formData.vin.visuel.intensite,
    limpidite: formData.vin.visuel.limpidite,
    brillance: formData.vin.visuel.brillance,
    evolutionVisuelle: formData.vin.visuel.evolution,
    remarquesVisuelles: formData.vin.visuel.remarques,

    // Nose
    intensiteNez: formData.vin.nez.intensite,
    qualiteNez: formData.vin.nez.qualite,
    typeAromes: Array.isArray(formData.vin.nez.type_aromes)
        ? formData.vin.nez.type_aromes.join(', ')
        : '',
    descriptionNez: formData.vin.nez.description,

    // Convert aromas nature to Map format
    aromesNature: convertAromasNatureToMap(formData.vin.nez.nature_aromes),

    // Mouth
    attaque: formData.vin.bouche.attaque,
    evolutionBouche: formData.vin.bouche.evolution,
    structure: formData.vin.bouche.structure,
    texture: formData.vin.bouche.texture,
    persistanceAromatique: formData.vin.bouche.persistance_aromatique,
    caudaliesLongueur: formData.vin.bouche.caudalies,
    structureLongueur: formData.vin.bouche.longueur_en_bouche,

    // Conclusion
    noteFinale: formData.vin.conclusion.note_finale,
    caudaliesConclusion: formData.vin.conclusion.caudalies,
    remarquesConclusion: formData.vin.conclusion.conclusion,
    accordsMetsVins: formData.vin.autres.accords_mets_vins.join(', '),

    // Other
    temperatureIdeale: formData.vin.autres.temperature_ideale_de_consommation,
    dateIdealeConsommation: formData.vin.autres.date_ideale_de_consommation,
    evolutionProbable: formData.vin.autres.evolution_probable
  };
};

// Convert aromas nature array to Map format for API
const convertAromasNatureToMap = (aromasArray) => {
  const result = {};

  aromasArray.forEach((category, index) => {
    const categoryName = getCategoryNameByIndex(index);
    const notes = [];

    // Collect all notes from this category
    Object.values(category).forEach(noteArray => {
      if (Array.isArray(noteArray)) {
        notes.push(...noteArray);
      }
    });

    if (notes.length > 0) {
      result[categoryName] = notes;
    }
  });

  return result;
};

// Helper to get category name by index
const getCategoryNameByIndex = (index) => {
  const categories = ['fruite', 'florale', 'boisee', 'balsamique', 'epicee', 'empyreumatique', 'animale', 'vegetale', 'alimentaire', 'minerale', 'chimique', 'anormale'];
  return categories[index] || `category_${index}`;
};


// Reactive variables
const showForm = ref(false)       // Toggle between form and button
const newTitle = ref('')          // Stores the new title input
const items = ref([])             // List of items to display

// Validation rules for title input
const titleRules = [(v) => !!v || 'Title is required']

// Toggle the form display
const toggleForm = () => {
  showForm.value = !showForm.value
}

// Confirm and add the new title to the items list
const confirmNewItem = () => {
  if (newTitle.value) {
    items.value.push(newTitle.value)  // Add title to list
    newTitle.value = ''               // Clear input field
    showForm.value = false            // Hide form and show the button
  }
}

// Cancel adding a new item
const cancelNewItem = () => {
  newTitle.value = ''                // Reset the form
  showForm.value = false             // Hide the form and show the button
}

// Cancel editing
const cancelEdit = () => {
  resetForm();
}

// Helper functions for displaying tasting data
const getTastingTitle = (tasting) => {
  // Create a title from cepages and region
  const cepages = tasting.cepages && tasting.cepages.length > 0 ? tasting.cepages.join(', ') : 'Vin';
  return `${cepages} - ${tasting.region || 'Région inconnue'}`;
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
}

// Edit tasting functionality
const editTasting = async (tasting) => {
  try {
    // Fetch full tasting data
    const fullTasting = await store.getTastingById(tasting.id);

    // Populate form with tasting data
    populateFormWithTastingData(fullTasting);

    // Set edit mode
    isEditMode.value = true;
    editingTastingId.value = tasting.id;

    // Show form
    showForm.value = true;

    // Reset to first step
    currentStep.value = 1;

  } catch (error) {
    console.error('Failed to load tasting for editing:', error);
    alert('Erreur lors du chargement de la dégustation pour modification.');
  }
}

// Delete tasting functionality
const deleteTasting = async (tastingId) => {
  if (confirm('Êtes-vous sûr de vouloir supprimer cette dégustation ?')) {
    try {
      await store.deleteTasting(tastingId);

      // Remove from local list
      const index = myWines.findIndex(t => t.id === tastingId);
      if (index > -1) {
        myWines.splice(index, 1);
      }

      // If we were editing this tasting, reset form
      if (editingTastingId.value === tastingId) {
        resetForm();
      }

    } catch (error) {
      console.error('Failed to delete tasting:', error);
      alert('Erreur lors de la suppression de la dégustation.');
    }
  }
}

// Populate form with tasting data
const populateFormWithTastingData = (tasting) => {
  // Map API data structure to form structure
  selectedTasting.vin.informations.type = {wineType: tasting.wineType};
  selectedTasting.vin.informations.cepage = tasting.cepages || [];
  selectedTasting.vin.informations.region = tasting.region || '';
  selectedTasting.vin.informations.aop_igp_vdf = tasting.aopIgpVdf || '';
  selectedTasting.vin.informations.elevage = tasting.elevage || '';
  selectedTasting.vin.informations.import = tasting.wineImport || '';
  selectedTasting.vin.informations.prix_lancement = tasting.prixLancement || 0;
  selectedTasting.vin.informations.prix_actuel = tasting.prixActuel || 0;

  // Visual aspect
  selectedTasting.vin.visuel.robe_rouge = tasting.robeRouge || '';
  selectedTasting.vin.visuel.robe_blanche = tasting.robeBlanche || '';
  selectedTasting.vin.visuel.robe_rose = tasting.robeRose || '';
  selectedTasting.vin.visuel.disque = tasting.disque || '';
  selectedTasting.vin.visuel.intensite = tasting.intensiteVisuelle || '';
  selectedTasting.vin.visuel.limpidite = tasting.limpidite || '';
  selectedTasting.vin.visuel.brillance = tasting.brillance || '';
  selectedTasting.vin.visuel.evolution = tasting.evolutionVisuelle || '';
  selectedTasting.vin.visuel.remarques = tasting.remarquesVisuelles || '';

  // Nose
  selectedTasting.vin.nez.intensite = tasting.intensiteNez || '';
  selectedTasting.vin.nez.qualite = tasting.qualiteNez || '';
  selectedTasting.vin.nez.type_aromes = tasting.typeAromes
      ? tasting.typeAromes.split(',').map(s => s.trim()).filter(Boolean)
      : [];
  selectedTasting.vin.nez.description = tasting.descriptionNez || '';

  // Handle aromas nature mapping
  if (tasting.aromesNature) {
    // Map the aromas nature data to the form structure
    Object.entries(tasting.aromesNature).forEach(([category, notes]) => {
      const categoryIndex = getCategoryIndex(category);
      if (categoryIndex !== -1 && selectedTasting.vin.nez.nature_aromes[categoryIndex]) {
        selectedTasting.vin.nez.nature_aromes[categoryIndex][category.toLowerCase()] = notes || [];
      }
    });
  }

  // Mouth
  selectedTasting.vin.bouche.attaque = tasting.attaque || '';
  selectedTasting.vin.bouche.evolution = tasting.evolutionBouche || '';
  selectedTasting.vin.bouche.structure = tasting.structure || '';
  selectedTasting.vin.bouche.texture = tasting.texture || '';
  selectedTasting.vin.bouche.persistance_aromatique = tasting.persistanceAromatique || '';
  selectedTasting.vin.bouche.caudalies = tasting.caudaliesLongueur || '';
  selectedTasting.vin.bouche.longueur_en_bouche = tasting.structureLongueur || '';

  // Conclusion
  selectedTasting.vin.conclusion.caudalies = tasting.caudaliesConclusion || 0;
  selectedTasting.vin.conclusion.note_finale = tasting.noteFinale || '';
  selectedTasting.vin.conclusion.conclusion = tasting.remarquesConclusion || '';

  // Other
  selectedTasting.vin.autres.temperature_ideale_de_consommation = tasting.temperatureIdeale || 0;
  selectedTasting.vin.autres.date_ideale_de_consommation = tasting.dateIdealeConsommation || '';
  selectedTasting.vin.autres.evolution_probable = tasting.evolutionProbable || '';
  selectedTasting.vin.autres.accords_mets_vins = tasting.accordsMetsVins ? [tasting.accordsMetsVins] : [];
}

// Helper function to get category index
const getCategoryIndex = (category) => {
  const categories = ['fruite', 'florale', 'boisee', 'balsamique', 'epicee', 'empyreumatique', 'animale', 'vegetale', 'alimentaire', 'minerale', 'chimique', 'anormale'];
  return categories.indexOf(category.toLowerCase());
}

// Reset form to create mode
const resetForm = () => {
  isEditMode.value = false;
  editingTastingId.value = null;
  showForm.value = false;
  newTitle.value = '';
  currentStep.value = 1;

  // Reset form data
  Object.assign(selectedTasting, {
    "vin": {
      "informations": {
        "type": {},
        "cepage": [],
        "region": "",
        "aop_igp_vdf": "",
        "elevage": "",
        "import": "",
        "prix_lancement": 0,
        "prix_actuel": 0,
      },
      "visuel": {
        "robe_blanche": {},
        "robe_rouge": {},
        "robe_rose": {},
        "disque": {},
        "intensite": {},
        "limpidite": {},
        "brillance": {},
        "evolution": {},
        "remarques": ""
      },
      "nez": {
        "intensite": {},
        "qualite": {},
        "type_aromes": [],
        "selectedCategories": [],
        "nature_aromes": [
          {"id": "1", "fruite": []},
          {"id": "2", "florale": []},
          {"id": "3", "boisee": []},
          {"id": "4", "balsamique": []},
          {"id": "5", "epicee": []},
          {"id": "6", "empyreumatique": []},
          {"id": "7", "animale": []},
          {"id": "8", "vegetale": []},
          {"id": "9", "alimentaire": []},
          {"id": "10", "minerale": []},
          {"id": "11", "chimique": []},
          {"id": "12", "anormale": []}
        ],
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
      "conclusion": {
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
      "autres": {
        "temperature_ideale_de_consommation": 0,
        "date_ideale_de_consommation": "",
        "evolution_probable": "",
        "accords_mets_vins": []
      }
    }
  });
}


// Open the drawer when the mouse moves near the left edge of the screen
const handleMouseMove = (event) => {
  if (event.clientX <= 20) {        // Mouse near the left screen edge
    drawer.value = true             // Show the drawer
  }
}

// Hide the drawer when the mouse leaves the drawer area
const handleMouseLeave = () => {
  drawer.value = false              // Hide the drawer
}

// Vérifie si une option est sélectionnée
const isSelected = (val, groupId) => {
  console.log(selected.value);
  console.log(selected.value[groupId]?.some((item) => item.id === val.id) || false);
  return (
      selected.value[groupId]?.some((item) => item.id === val.id) || false
  );
};

// Function to update selected notes
const updateSelectedNotes = (group, val, selectedValues) => {
  // Ensure the nested object exists
  if (!selectedTasting.vin.nez.nature_aromes[val.id]) {
    selectedTasting.vin.nez.nature_aromes[val.id] = [];
  }

  // Store only selected values
  if (selectedValues.length > 0) {
    selectedTasting.vin.nez.nature_aromes[val.id] = selectedValues;
  } else {
    // If empty, reset the array to avoid storing unwanted data
    selectedTasting.vin.nez.nature_aromes[val.id] = [];
  }
};

const test = (...args) => {
  // console.log("___________");
  // console.log("test");
  console.log(args);
  // console.log(e);
  // console.log(selectedTasting);
  // console.log("___________");
  testTaste.value = args;
}

const filteredWineTypeValues = (items = []) => {
  const selectedWineType = toRaw(selectedTasting.vin.informations.type?.wineType);

  if (!selectedWineType) return items;

  return items.filter(item => {
    const wineType = toRaw(item?.wineType);

    if (!wineType) return true;              // option valid for all wine types
    if (Array.isArray(wineType)) return wineType.includes(selectedWineType);

    return wineType === selectedWineType;
  });
};


const isSameWineType = (item) => {
  const selectedWineType = selectedTasting.vin.informations.type?.wineType;
  if (!selectedWineType) return true;

  const itemWineType = item.wineType;
  if (!itemWineType || !itemWineType.length) return true;

  if (Array.isArray(itemWineType)) {
    return itemWineType.includes(selectedWineType);
  }

  return selectedWineType === itemWineType;
};

const getStepIcon = (stepName) => {
  const icons = {
    informations: 'mdi-information',
    visuel: 'mdi-eye',
    nez: 'mdi-nose',
    bouche: 'mdi-mouth',
    longueur: 'mdi-timer',
    conclusion: 'mdi-check-circle'
  };
  return icons[stepName] || 'mdi-circle';
};

const getFieldIcon = (fieldType) => {
  const icons = {
    text: 'mdi-text',
    textarea: 'mdi-text-long',
    autocomplete: 'mdi-magnify',
    select: 'mdi-menu-down',
    number: 'mdi-numeric',
    slider: 'mdi-tune'
  };
  return icons[fieldType] || 'mdi-circle';
};

const getCheckboxColor = (val) => {
  if (val.color) return val.color;
  if (val.negatif) return 'error';
  return 'wine-primary';
};

const getSliderLabel = (field, modelValue) => {
  if (field.name === 'persistance_aromatique') {
    const labels = ['nulle', 'courte', 'moyenne', 'bonne', 'longue', 'très longue', 'infinie'];
    return labels[Math.min(Math.floor(modelValue / 14.28), 6)] || 'moyenne';
  }
  return modelValue;
};

const ensureAromaBucket = (stepName, val) => {
  const aromas = selectedTasting.vin[stepName].nature_aromes;
  const index = Number(val.id) - 1;      // sécurise l'index numérique
  const key = val.value.toLowerCase();   // "fruitée", "florale", etc.

  // Si l'entrée du tableau n'existe pas, on la crée
  if (!aromas[index]) {
    aromas[index] = {};
  }

  // Si le tableau pour cette clé n'existe pas, on le crée
  if (!Array.isArray(aromas[index][key])) {
    aromas[index][key] = [];
  }
};

const getTypeAromesGroups = (field) => {
  if (!field?.groups || !Array.isArray(field.groups)) return [];
  // Chaque entry de field.groups EST un groupe
  return field.groups.map((g, index) => ({
    id: g.id ?? index + 1,
    required: !!g.required,
    options: g.groupValues || []
  }));
};

const onTypeAromeToggle = (group, value, checked) => {
  const current = Array.isArray(selectedTasting.vin.nez.type_aromes)
      ? [...selectedTasting.vin.nez.type_aromes]
      : [];

  // on retire toutes les valeurs de ce groupe ?
  // → si tu veux limiter à UNE valeur par groupe, on enlève d’abord les anciennes valeurs de ce groupe :
  const groupValues = group.options.map(o => o.value);
  let next = current.filter(v => !groupValues.includes(v));

  if (checked) {
    next.push(value);
  }

  selectedTasting.vin.nez.type_aromes = next;
};

</script>

<template>
  <div class="tasting-background">
    <v-navigation-drawer v-model="drawer" temporary @mouseleave="handleMouseLeave" class="wine-drawer">
      <div class="d-flex flex-column justify-center pa-4">
        <v-btn v-if="!showForm" class="ma-2 wine-btn-primary" @click="toggleForm" elevation="2">
          <v-icon class="mr-2">mdi-plus</v-icon>
          Nouvelle dégustation
        </v-btn>

        <v-card v-if="showForm" class="ma-2 wine-card" elevation="4">
          <v-card-title class="wine-card-title">
            <v-icon class="mr-2">mdi-file-document-plus</v-icon>
            Nouvelle fiche
          </v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                  variant="outlined"
                  label="Titre de la dégustation"
                  v-model="newTitle"
                  :rules="titleRules"
                  required
                  class="wine-text-field"
                  prepend-inner-icon="mdi-file-document-edit"
              />
            </v-form>
          </v-card-text>
          <v-card-actions class="pa-4">
            <v-btn @click="confirmNewItem" class="wine-btn-primary" :disabled="!newTitle" elevation="2">
              <v-icon class="mr-2">mdi-check</v-icon>
              Confirmer
            </v-btn>
            <v-btn @click="cancelNewItem" variant="outlined" class="wine-btn-secondary">
              <v-icon class="mr-2">mdi-close</v-icon>
              Annuler
            </v-btn>
          </v-card-actions>
        </v-card>

        <v-list class="wine-list">
          <v-list-item
              v-for="(tasting, index) in myWines"
              :key="tasting.id || index"
              @click="editTasting(tasting)"
              class="wine-list-item"
              :class="{ 'wine-list-item-active': editingTastingId === tasting.id }"
          >
            <v-icon class="mr-3">mdi-wine</v-icon>
            <v-list-item-content>
              <v-list-item-title class="wine-title">
                {{ getTastingTitle(tasting) }}
              </v-list-item-title>
              <v-list-item-subtitle class="wine-subtitle">
                {{ tasting.wineType }} • {{ tasting.region }} • {{ tasting.noteFinale }}
              </v-list-item-subtitle>
              <v-list-item-subtitle class="wine-date">
                {{ formatDate(tasting.createdAt) }}
              </v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-action>
              <v-btn icon small @click.stop="deleteTasting(tasting.id)">
                <v-icon color="error">mdi-delete</v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item>
        </v-list>
      </div>
    </v-navigation-drawer>

    <v-container width="100%" fluid class="tasting-container">
      <v-row class="tasting-row" no-gutters>
        <!-- Image Section - Desktop: left, Mobile: top -->
        <v-col cols="12" lg="4" class="image-section">
          <v-card class="wine-card image-card" elevation="8">
            <v-tabs v-model="tabImg" class="wine-tabs" bg-color="rgba(139, 69, 19, 0.1)">
              <v-tab value="one" class="wine-tab">
                <v-icon class="mr-2">mdi-bottle-wine</v-icon>
                <span class="d-none d-sm-inline">Bouteille</span>
              </v-tab>
              <v-tab value="two" class="wine-tab">
                <v-icon class="mr-2">mdi-glass-wine</v-icon>
                <span class="d-none d-sm-inline">Verre</span>
              </v-tab>
            </v-tabs>

            <v-card-text class="pa-0">
              <v-tabs-window v-model="tabImg">
                <v-tabs-window-item class="relative image-window" value="one">
                  <v-img
                      width="100%"
                      :height="$vuetify.display.xs ? '250px' : '100%'"
                      cover
                      src="https://cuisinedecheffe.com/87427-large_default/vin-rouge-bordeaux-le-bedat-aoc-hve-bouteille-750ml.jpg"
                      class="wine-image"
                  >
                    <div class="image-overlay"></div>
                  </v-img>
                  <v-btn
                      class="position-absolute bottom-0 right-0 ma-2 wine-btn-camera"
                      icon="mdi-camera-outline"
                      :size="$vuetify.display.xs ? 'default' : 'large'"
                      elevation="4"
                  >
                    <v-icon>mdi-camera-outline</v-icon>
                  </v-btn>
                </v-tabs-window-item>

                <v-tabs-window-item class="relative image-window" value="two">
                  <v-img
                      width="100%"
                      :height="$vuetify.display.xs ? '250px' : '100%'"
                      cover
                      src="https://lesraisinsdelajoie.fr/214-large_default/4-verres-a-bordeaux.jpg"
                      class="wine-image"
                  >
                    <div class="image-overlay"></div>
                  </v-img>
                  <v-btn
                      class="position-absolute bottom-0 right-0 ma-2 wine-btn-camera"
                      icon="mdi-camera-outline"
                      :size="$vuetify.display.xs ? 'default' : 'large'"
                      elevation="4"
                  >
                    <v-icon>mdi-camera-outline</v-icon>
                  </v-btn>
                </v-tabs-window-item>
              </v-tabs-window>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Form Section - Desktop: right, Mobile: bottom -->
        <v-col cols="12" class="form-section">
          <v-card class="wine-card form-card" elevation="8">
            <v-stepper
                v-model="currentStep"
                :items="steps"
                show-actions
                class="wine-stepper"
                elevation="0"
            >
              <template v-slot:[`item.${step.step}`] v-for="step in steps" :key="step.step">
                <div class="step-content">
                  <div class="step-header">
                    <v-icon class="step-icon mr-2" :icon="getStepIcon(step.name)"></v-icon>
                    <h2 class="step-title">{{ step.title }}</h2>
                  </div>

                  <div class="step-scroll-container">
                    <v-container class="step-container">
                      <v-row>
                        <v-col class="d-flex flex-column ga-3">
                          <template v-if="step.fields">
                            <template v-for="field in step.fields" :key="field.id">
                              <v-card
                                  class="field-card wine-card"
                                  elevation="2"
                                  :class="{ 'aroma-field': field.name === 'nature_aromes' }"
                              >
                                <v-card-title class="field-title pa-2">
                                  <v-icon class="mr-2" size="small" :icon="getFieldIcon(field.type)"></v-icon>
                                  <span class="field-label">{{ field.label }}</span>
                                </v-card-title>
                                <v-card-text class="pa-3">
                                  <template v-if="field.type === 'text'">
                                    <v-text-field
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        class="wine-text-field"
                                        prepend-inner-icon="mdi-text"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-if="field.type === 'textarea'">
                                    <v-textarea
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        variant="outlined"
                                        density="comfortable"
                                        class="wine-textarea"
                                        prepend-inner-icon="mdi-text-long"
                                        :rows="$vuetify.display.xs ? 2 : 3"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-if="field.type === 'autocomplete'">
                                    <v-autocomplete
                                        :label="field.label"
                                        density="comfortable"
                                        chips
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :items="filteredWineTypeValues(field.values)"
                                        item-title="value"
                                        item-value="id"
                                        variant="outlined"
                                        return-object
                                        :multiple="field.multi"
                                        class="wine-autocomplete"
                                        prepend-inner-icon="mdi-magnify"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-if="field.type === 'select'">
                                    <v-select
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        :items="field.values"
                                        class="wine-select"
                                        prepend-inner-icon="mdi-menu-down"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-if="field.type === 'number'">
                                    <v-text-field
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        prefix="€"
                                        type="number"
                                        class="wine-text-field"
                                        prepend-inner-icon="mdi-currency-eur"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-if="field.name === 'type_aromes'">
                                    <div class="type-aromes-groups">
                                      <div
                                          v-for="group in getTypeAromesGroups(field)"
                                          :key="group.id"
                                          class="type-aromes-group mb-3"
                                      >
                                        <div class="d-flex align-center mb-1">
        <span class="text-body-2 font-weight-medium">
          Groupe {{ group.id }}
        </span>
                                          <v-chip
                                              v-if="group.required"
                                              size="x-small"
                                              class="ml-2"
                                              color="error"
                                              label
                                          >
                                            obligatoire
                                          </v-chip>
                                        </div>

                                        <div class="checkbox-group">
                                          <v-checkbox
                                              v-for="opt in group.options"
                                              :key="opt.id"
                                              class="wine-checkbox mb-2"
                                              :label="opt.value"
                                              :true-icon="opt.icon || 'mdi-checkbox-marked'"
                                              :color="getCheckboxColor(opt)"
                                              :model-value="selectedTasting.vin.nez.type_aromes.includes(opt.value)"
                                              @update:model-value="checked => onTypeAromeToggle(group, opt.value, checked)"
                                              density="comfortable"
                                              hide-details
                                              :class="{ 'checkbox-mobile': $vuetify.display.xs }"
                                          />
                                        </div>
                                      </div>
                                    </div>
                                  </template>

                                  <template v-else-if="field.groups">
                                    <div class="checkbox-group">
                                      <template v-for="(val, index2) in field.groups[0].groupValues" :key="val.id">
                                        <v-checkbox
                                            :value="val"
                                            class="wine-checkbox mb-2"
                                            :color="getCheckboxColor(val)"
                                            :true-icon="val.icon ? val.icon : 'mdi-checkbox-marked'"
                                            v-model="selectedTasting.vin[step.name].selectedCategories"
                                            :multiple="field.groups[0].multi"
                                            :label="val.value"
                                            density="comfortable"
                                            hide-details
                                            :class="{ 'checkbox-mobile': $vuetify.display.xs }"
                                            @change="ensureAromaBucket(step.name, val)"
                                        />


                                        <v-expand-transition>
                                          <v-autocomplete
                                              v-if=" val.notes && Array.isArray(selectedTasting.vin[step.name].selectedCategories) && selectedTasting.vin[step.name].selectedCategories.includes(val) && selectedTasting.vin[step.name].nature_aromes[val.id - 1]"
                                              :label="`Notes pour ${val.value}`"
                                              density="comfortable"
                                              chips
                                              :items="val.notes"
                                              v-model="selectedTasting.vin[step.name].nature_aromes[val.id - 1][val.value.toLowerCase()]"
                                              item-title="libelle"
                                              item-value="libelle"
                                              variant="outlined"
                                              multiple
                                              class="wine-autocomplete aroma-select"
                                              prepend-inner-icon="mdi-fruit-grapes"
                                              :hide-details="$vuetify.display.xs"
                                          />

                                        </v-expand-transition>
                                      </template>
                                    </div>
                                  </template>

                                  <template v-if="field.type === 'slider'">
                                    <div class="slider-container">
                                      <v-slider
                                          v-model="selectedTasting.vin[step.name][field.name]"
                                          thumb-label="always"
                                          class="wine-slider"
                                          color="wine-primary"
                                      >
                                        <template v-slot:thumb-label="{ modelValue }">
                                          {{ getSliderLabel(field, modelValue) }}
                                        </template>
                                      </v-slider>
                                    </div>
                                  </template>
                                </v-card-text>
                              </v-card>
                            </template>
                          </template>
                        </v-col>
                      </v-row>
                    </v-container>
                  </div>
                </div>
              </template>
            </v-stepper>

            <v-card-actions class="pa-4 justify-center">
              <v-btn
                  v-if="isEditMode"
                  @click="cancelEdit"
                  variant="outlined"
                  class="wine-btn-secondary mr-2"
                  :size="$vuetify.display.xs ? 'default' : 'large'"
                  elevation="2"
              >
                <v-icon class="mr-2">mdi-close</v-icon>
                <span class="d-none d-sm-inline">Annuler</span>
                <span class="d-sm-none">Annuler</span>
              </v-btn>
              <v-btn
                  @click="submitForm"
                  class="wine-btn-primary submit-btn"
                  :size="$vuetify.display.xs ? 'default' : 'large'"
                  elevation="4"
                  block
              >
                <v-icon class="mr-2">{{ isEditMode ? 'mdi-content-save-edit' : 'mdi-content-save' }}</v-icon>
                <span class="d-none d-sm-inline">{{
                    isEditMode ? 'Mettre à jour' : 'Enregistrer la dégustation'
                  }}</span>
                <span class="d-sm-none">{{ isEditMode ? 'Mettre à jour' : 'Enregistrer' }}</span>
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.tasting-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 8px;
  display: flex;
  flex-direction: column;
}

.wine-drawer {
  background: linear-gradient(180deg, #8b4513 0%, #654321 100%);
  color: white;
}

.wine-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
  box-shadow: 0 4px 20px rgba(139, 69, 19, 0.1);
  transition: all 0.3s ease;
}

.wine-card-title {
  background: linear-gradient(90deg, #8b4513, #a0522d);
  color: white;
  border-radius: 10px 10px 0 0;
}

.wine-btn-primary {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  color: white;
  border-radius: 20px;
  text-transform: none;
  font-weight: 600;
  transition: all 0.3s ease;
  min-height: 44px;
}

.wine-btn-secondary {
  border-radius: 20px;
  text-transform: none;
  font-weight: 600;
  border-color: #8b4513;
  color: #8b4513;
}

.wine-list-item {
  border-radius: 6px;
  margin: 2px 0;
  transition: all 0.3s ease;
}

.wine-list-item-active {
  background: rgba(255, 255, 255, 0.2);
}

.tasting-container {
  flex: 1;
  max-width: 100%;
  margin: 0 auto;
  height: calc(100vh - 16px);
  display: flex;
  flex-direction: column;
}

.tasting-row {
  flex: 1;
  gap: 8px;
  height: 100%;
}

.image-section {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.image-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.wine-tabs {
  border-radius: 10px 10px 0 0;
  flex-shrink: 0;
}

.wine-tab {
  text-transform: none;
  font-weight: 600;
  min-height: 40px;
}

.wine-tab .v-tab__content {
  font-size: 0.875rem;
}

.image-window {
  flex: 1;
  border-radius: 0 0 10px 10px;
  overflow: hidden;
  position: relative;
}

.wine-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(139, 69, 19, 0.1), transparent);
  pointer-events: none;
}

.wine-btn-camera {
  background: rgba(255, 255, 255, 0.9);
  color: #8b4513;
  border-radius: 50%;
  min-width: 40px;
  width: 40px;
  height: 40px;
}

.form-section {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.form-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.wine-stepper {
  background: transparent;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.step-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(139, 69, 19, 0.2);
  flex-shrink: 0;
}

.stepper-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(139, 69, 19, 0.2);
  flex-shrink: 0;
}

.stepper-title {
  margin: 0;
  color: #8b4513;
  font-weight: 600;
}

.edit-indicator {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  color: white;
}

.step-icon {
  color: #8b4513;
  font-size: 24px;
  flex-shrink: 0;
}

.step-title {
  font-size: 20px;
  font-weight: 700;
  color: #8b4513;
  margin: 0 0 0 8px;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
  flex: 1;
}

.step-scroll-container {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.step-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.step-scroll-container::-webkit-scrollbar-track {
  background: rgba(139, 69, 19, 0.1);
  border-radius: 3px;
}

.step-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(139, 69, 19, 0.3);
  border-radius: 3px;
}

.step-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 69, 19, 0.5);
}

.step-container {
  padding: 0;
}

.field-card {
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.field-title {
  background: linear-gradient(90deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  color: #8b4513;
  font-weight: 600;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 6px 6px 0 0;
  display: flex;
  align-items: center;
}

.field-label {
  font-size: 14px;
  line-height: 1.2;
}

.aroma-field {
  border-left: 3px solid #8b4513;
}

.wine-text-field,
.wine-textarea,
.wine-autocomplete,
.wine-select {
  border-radius: 6px;
}

.wine-text-field :deep(.v-field) {
  border-radius: 6px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.wine-checkbox {
  border-radius: 6px;
  transition: all 0.3s ease;
  margin: 0;
}

.checkbox-mobile {
  width: 100%;
  margin-bottom: 8px;
}

.aroma-select {
  margin-top: 8px;
  margin-left: 16px;
  border-left: 2px solid rgba(139, 69, 19, 0.3);
  padding-left: 8px;
}

.slider-container {
  padding: 12px 0;
}

.wine-slider {
  --v-slider-track-color: rgba(139, 69, 19, 0.2);
  --v-slider-track-filled-color: #8b4513;
}

.submit-btn {
  min-width: 200px;
  height: 48px;
  font-size: 14px;
  text-transform: none;
  font-weight: 700;
  margin: 0 auto;
}

/* Responsive Design */
@media (min-width: 1264px) {
  .tasting-background {
    padding: 16px;
  }

  .tasting-container {
    height: calc(100vh - 32px);
  }

  .tasting-row {
    gap: 16px;
  }

  .step-content {
    padding: 24px;
  }

  .step-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
  }

  .step-icon {
    font-size: 32px;
  }

  .step-title {
    font-size: 28px;
    margin-left: 12px;
  }

  .field-card {
    margin-bottom: 16px;
  }

  .field-title {
    padding: 12px 16px;
    font-size: 16px;
  }

  .field-label {
    font-size: 16px;
  }

  .checkbox-group {
    gap: 8px;
  }

  .wine-checkbox {
    margin: 0 4px 4px 0;
  }

  .aroma-select {
    margin-left: 24px;
    padding-left: 12px;
  }

  .submit-btn {
    min-width: 250px;
    height: 56px;
    font-size: 16px;
  }
}

@media (max-width: 600px) {
  .tasting-background {
    padding: 4px;
  }

  .tasting-container {
    height: 100vh;
  }

  .tasting-row {
    gap: 4px;
  }

  .step-content {
    padding: 12px;
  }

  .step-header {
    margin-bottom: 12px;
    padding-bottom: 8px;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .step-icon {
    font-size: 20px;
  }

  .step-title {
    font-size: 18px;
    margin: 0;
  }

  .checkbox-group {
    flex-direction: column;
    gap: 4px;
  }

  .wine-checkbox {
    width: 100%;
    margin: 0 0 4px 0;
  }

  .aroma-select {
    margin-left: 0;
    border-left: none;
    padding-left: 0;
  }

  .field-card {
    margin-bottom: 8px;
  }

  .field-title {
    padding: 6px 8px;
    font-size: 13px;
  }

  .field-label {
    font-size: 13px;
  }

  .wine-btn-camera {
    min-width: 36px;
    width: 36px;
    height: 36px;
  }
}
</style>


