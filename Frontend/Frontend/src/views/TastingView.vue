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
      "type_aromes": {},
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

const selected = ref({});
const selectedNotes = reactive({});

const myWines = reactive([]);

const currentStep = ref(1);
let steps = ref([]);

const tabImg = ref("one");

const drawer = ref(false);

onMounted(() => {
  document.body.addEventListener('mousemove', handleMouseMove)
  steps.value = store.tasting_steps;
});

const submitForm = () => {
  console.log(toRaw(selectedTasting)); // This will log the full tasting data object
  myWines.push(selectedTasting);
  items.value.push(selectedTasting)
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

// Handle the click event for list items
const handleItemClick = (item) => {
  alert(`Item clicked: ${item}`)
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

const getSelectedNotes = (group, value, event) => {

  if (!selectedNotes[group]) {
    console.log(event);
    console.log(value);
    console.log(selectedNotes[group.id]);
    selectedNotes[group] = {};
  }
  if (!selectedNotes[group][value]) {
    selectedNotes[group][value] = [];
  }
  return selectedNotes[group][value];
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

const filteredWineTypeValues = (items) => {
  const selectedWineType = toRaw(selectedTasting.vin.informations.type['1']?.wineType);

  if (!selectedWineType) return items;

  return items.filter(item => {
    let wineType = toRaw(item?.wineType);

    if (Array.isArray(item?.wineType)) {
      return wineType.includes(selectedWineType);
    }
    return wineType === selectedWineType;
  });
}

const isSameWineType = (item) => {
  const selectedWineType = selectedTasting.vin.informations.type?.wineType;

  if (!selectedWineType) {
    return true;
  }

  if (!item.wineType.length) {
    return true;
  }

  if (Array.isArray(item.wineType)) {
    return item.wineType.includes(selectedWineType);
  }

  return selectedWineType === item.wineType;
}

</script>

<template>

  <v-navigation-drawer v-model="drawer" temporary @mouseleave="handleMouseLeave">

    <div class="d-flex flex-column justify-center">

      <v-btn v-if="!showForm" class="ma-2" @click="toggleForm" color="primary">Nouvelle dégustation</v-btn>


      <v-card v-if="showForm" class="ma-2">
        <v-card-title>Nouvelle fiche</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field variant="outlined" label="Title" v-model="newTitle" :rules="titleRules" required/>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="confirmNewItem" color="primary" :disabled="!newTitle">Confirm</v-btn>
          <v-btn @click="cancelNewItem" color="secondary">Cancel</v-btn>
        </v-card-actions>
      </v-card>


      <v-list>
        <v-list-item v-for="(item, index) in items" :key="index" @click="handleItemClick(item)" class="clickable-item">
          {{ item }}
        </v-list-item>
      </v-list>
    </div>
  </v-navigation-drawer>


  <v-container v-if="steps && steps.length">
    <v-row no-gutters>
      <v-col>
        <v-row no-gutters>
          <v-col class="v-xs-col-5">

            <v-card>
              <v-tabs v-model="tabImg">
                <v-tab value="one">
                  <v-icon class="mr-2" icon="mdi-bottle-wine"></v-icon>
                  Bouteille
                </v-tab>
                <v-tab value="two">
                  <v-icon class="mr-2" icon="mdi-glass-wine"></v-icon>
                  Verre
                </v-tab>
              </v-tabs>

              <v-card-text>
                <v-tabs-window v-model="tabImg">

                  <v-tabs-window-item class="relative" value="one">
                    <v-img width="100%" max-height="100%" aspect-ratio="1/1" cover
                           src="httpss://cuisinedecheffe.com/87427-large_default/vin-rouge-bordeaux-le-bedat-aoc-hve-bouteille-750ml.jpg">
                    </v-img>
                    <v-btn class="position-absolute bottom-0 right-0 ma-2" icon="mdi-camera-outline"
                           size="large"></v-btn>
                  </v-tabs-window-item>

                  <v-tabs-window-item class="relative" value="two">
                    <v-img width="100%" max-height="100%" aspect-ratio="1/1" cover
                           src="httpss://lesraisinsdelajoie.fr/214-large_default/4-verres-a-bordeaux.jpg"></v-img>
                    <v-btn class="position-absolute bottom-0 right-0 ma-2" icon="mdi-camera-outline"
                           size="large"></v-btn>
                  </v-tabs-window-item>

                </v-tabs-window>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col>
            <v-sheet class="pa-4">

              <v-stepper editable v-model="currentStep" :items="steps" show-actions>
                <template v-slot:[`item.${step.step}`] v-for="step in steps" :key="step.step">

                  <h3 class="text-h6">{{ step.title }}</h3>

                  <v-container>
                    <v-row>
                      <v-col class="d-flex flex-column ga-3">

                        <template v-if="step.fields">

                          <template class="d-flex flex-wrap ga-3" v-for="field in step.fields" :key="field.id">
                            <v-row>
                              <h3>{{ field.label }}</h3>
                            </v-row>
                            <v-row>

                              <template v-if="field.type === 'text'">
                                <!--                                                                <label>{{ field.label }}</label>-->
                                <v-text-field density="compact" variant="outlined"
                                              v-model="selectedTasting.vin[step.name][field.name]" hide-details="auto"
                                              :label="field.label"></v-text-field>
                                <v-divider></v-divider>
                              </template>

                              <template v-if="field.type === 'textarea'">
                                <!--                                <label>{{ field.label }}</label>-->
                                <v-textarea v-model="selectedTasting.vin[step.name][field.name]" :label="field.label"
                                            variant="outlined" density="compact" hide-details="auto"></v-textarea>
                                <v-divider></v-divider>
                              </template>

                              <template v-if="field.type === 'autocomplete'">
                                <v-autocomplete :label="field.label" density="compact" chips
                                                v-model="selectedTasting.vin[step.name][field.name]"
                                                :items="filteredWineTypeValues(field.values)" item-title="value"
                                                item-value="id"
                                                hide-details="true" variant="outlined" return-object
                                                :multiple="field.multi">
                                </v-autocomplete>
                                <v-divider></v-divider>
                              </template>

                              <template v-if="field.type === 'select'">
                                <v-select density="compact" variant="outlined"
                                          v-model="selectedTasting.vin[step.name][field.name]"
                                          v-if="field.type === 'select'"
                                          :label="field.label" :items="field.values" hide-details="true"></v-select>
                                <v-divider></v-divider>
                              </template>

                              <template v-if="field.type === 'number'">
                                <v-text-field density="compact" variant="outlined"
                                              v-model="selectedTasting.vin[step.name][field.name]" :label="field.label"
                                              prefix="€"
                                              hide-details="true"></v-text-field>
                                <v-divider></v-divider>
                              </template>


                              <template v-for="(group, index1) in field.groups" :key="index1">
                                <template v-if="group.type === 'select-button' && isSameWineType(group)">

                                  <template v-for="(val, index2) in group.groupValues" :key="val.id">
                                    <v-checkbox :value="val" class="mx-3"
                                                :color="(val.color) ? val.color : (val.negatif ? 'red' : '')"
                                                :true-icon="val.icon ? val.icon : 'mdi-checkbox-marked'"
                                                v-model="selectedTasting.vin[step.name][field.name][group.id]"
                                                :multiple="group.multi" :label="val.value" density="compact"
                                                hide-details
                                                @click="test(val, group, $event)"></v-checkbox>

                                    <!-- Autocomplete for Notes -->

                                    <!-- <v-autocomplete
                                      label="Sélectionnez des notes pour" density="compact" chips
                                      :items="['test1', 'test2']"
                                      item-title="libelle" item-value="libelle" hide-details variant="outlined"
                                      multiple></v-autocomplete> -->

                                    <v-autocomplete v-if="val.notes"
                                                    label="Sélectionnez des notes pour" density="compact" chips
                                                    :items="val.notes"
                                                    @update:model-value="getSelectedNotes(group, val, $event)"
                                                    item-title="libelle" item-value="libelle" hide-details
                                                    variant="outlined"
                                                    multiple></v-autocomplete>


                                  </template>

                                </template>

                                <v-divider thickness="3"></v-divider>
                              </template>

                              <template v-if="field.type === 'slider'">
                                <label>{{ field.label }}</label>
                                <div>
                                  <v-slider v-model="selectedTasting.vin[step.name][field.name]" thumb-label="always">
                                    <template v-slot:thumb-label="{ modelValue }">
                                      {{ options[Math.min(Math.floor(modelValue / 7), 9)] }}
                                    </template>
                                  </v-slider>
                                </div>
                                <v-divider></v-divider>
                              </template>

                            </v-row>
                          </template>

                        </template>

                      </v-col>
                    </v-row>
                  </v-container>
                </template>


              </v-stepper>
              <v-btn @click="submitForm">Submit</v-btn>
            </v-sheet>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
.v-container {
  height: 100%;
  width: 100%;
  max-width: 100%;
}

.btn-toggle {
  width: fit-content;
}
</style>