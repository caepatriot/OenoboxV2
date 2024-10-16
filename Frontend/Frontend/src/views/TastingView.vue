<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useTastingStore } from "@/stores/tasting.js";

const store = useTastingStore();

let selectedTasting = ref(null);

const currentStep = ref(1);
let steps = ref([]);

const tabImg = ref("one");

const drawer = ref(true);

// const items = ref([
//   { title: 'Foo', value: 'foo' },
//   { title: 'Bar', value: 'bar' },
//   { title: 'Fizz', value: 'fizz' },
//   { title: 'Buzz', value: 'buzz' },
// ]);


onMounted(() => {
  document.body.addEventListener('mousemove', handleMouseMove)
  steps.value = store.tasting_steps;
});





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

</script>

<template>

  <v-navigation-drawer v-model="drawer" temporary @mouseleave="handleMouseLeave">

    <div class="d-flex flex-column justify-center">

      <v-btn v-if="!showForm" class="ma-2" @click="toggleForm" color="primary">Nouvelle dégustation</v-btn>


      <v-card v-if="showForm" class="ma-2">
        <v-card-title>Nouvelle fiche</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field variant="outlined" label="Title" v-model="newTitle" :rules="titleRules" required />
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
                  Bouteille</v-tab>
                <v-tab value="two">
                  <v-icon class="mr-2" icon="mdi-glass-wine"></v-icon>
                  Verre
                </v-tab>
              </v-tabs>

              <v-card-text>
                <v-tabs-window v-model="tabImg">

                  <v-tabs-window-item class="relative" value="one">
                    <v-img width="100%" max-height="100%" aspect-ratio="1/1" cover
                      src="https://cuisinedecheffe.com/87427-large_default/vin-rouge-bordeaux-le-bedat-aoc-hve-bouteille-750ml.jpg">
                    </v-img>
                    <v-btn class="position-absolute bottom-0 right-0 ma-2" icon="mdi-camera-outline"
                      size="large"></v-btn>
                  </v-tabs-window-item>

                  <v-tabs-window-item class="relative" value="two">
                    <v-img width="100%" max-height="100%" aspect-ratio="1/1" cover
                      src="https://lesraisinsdelajoie.fr/214-large_default/4-verres-a-bordeaux.jpg"></v-img>
                    <v-btn class="position-absolute bottom-0 right-0 ma-2" icon="mdi-camera-outline"
                      size="large"></v-btn>
                  </v-tabs-window-item>

                </v-tabs-window>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col>
            <v-sheet class="pa-4">

              <v-stepper v-model="currentStep" :items="steps" show-actions>
                <template v-slot:[`item.${step.step}`] v-for="step in steps" :key="step.step">

                  <h3 class="text-h6">{{ step.title }}</h3>

                  <v-container>
                    <v-row>
                      <v-col class="d-flex flex-column ga-3">

                        <template class="d-flex flex-wrap ga-3" v-for="field in step.fields" :key="field.id">

                          <v-text-field variant="outlined" v-model="selectedTasting" v-if="field.type === 'text'" hide-details="auto"
                            :label="field.label"></v-text-field>

                          <v-select variant="outlined" v-if="field.type === 'select'" :label="field.label"
                            :items="field.values" hide-details="true"></v-select>

                          <v-text-field variant="outlined" v-if="field.type === 'number'" :label="field.label"
                            prefix="€" hide-details="true"></v-text-field>

                          <v-btn-toggle value="" v-if="field.type === 'select-button'">

                            <v-btn :color="option.iconColor" v-for="option in field.options" :key="option.id">
                              <span>{{ option.type }}</span>
                              <v-icon v-if="option.icon" start :color="option.iconColor" :icon="option.icon"
                                size="x-large">
                              </v-icon>
                            </v-btn>

                          </v-btn-toggle>
                        </template>
                      </v-col>
                    </v-row>
                  </v-container>
                </template>
              </v-stepper>
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
</style>