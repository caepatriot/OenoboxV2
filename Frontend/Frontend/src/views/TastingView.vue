<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useTastingStore } from "@/stores/tasting.js";

const store = useTastingStore();

const currentStep = ref(1);
let steps = ref([]);

const tabImg = ref("one");

onMounted(() => {
  steps.value = store.tasting_steps;
});
</script>

<template>

  <v-container>
    <v-row no-gutters>
      <v-col>
        <v-card class="" v-if="steps && steps.length">
          <v-row no-gutters>
            <v-col class="v-col-5">
              <v-sheet class="pa-2 ma-2">

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
                      <v-tabs-window-item value="one">
                        <v-img :width="auto" max-height="100%" aspect-ratio="1/1" cover
                          src="https://cuisinedecheffe.com/87427-large_default/vin-rouge-bordeaux-le-bedat-aoc-hve-bouteille-750ml.jpg">
                        </v-img>

                      </v-tabs-window-item>

                      <v-tabs-window-item value="two">
                        <v-img :width="auto" max-height="100%" aspect-ratio="1/1" cover
                          src="https://lesraisinsdelajoie.fr/214-large_default/4-verres-a-bordeaux.jpg"></v-img>
                      </v-tabs-window-item>
                    </v-tabs-window>
                  </v-card-text>
                </v-card>

              </v-sheet>
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
                            <v-text-field v-if="field.type === 'text'" hide-details="auto"
                              :label="field.label"></v-text-field>

                            <v-select v-if="field.type === 'select'" :label="field.label" :items="field.values"
                              hide-details="true"></v-select>

                            <v-text-field v-if="field.type === 'number'" :label="field.label" model-value="10.00"
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
        </v-card>
      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
.v-container {
  max-width: 100%;
}
</style>