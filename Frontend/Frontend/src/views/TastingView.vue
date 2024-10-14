<script setup>
import {ref, reactive, onMounted} from 'vue';
import {useTastingStore} from "@/stores/tasting.js";

const store = useTastingStore();

const currentStep = ref(1);
let steps = ref([]);

onMounted(() => {
  steps.value = store.tasting_steps;
});
</script>

<template>
  <v-card class="" v-if="steps && steps.length">
    <v-card-text>

      <v-stepper
          v-model="currentStep"
          :items="steps"
          show-actions
      >
        <template v-slot:[`item.${step.step}`]
                  v-for="step in steps" :key="step.step">

          <h3 class="text-h6">{{ step.title }}</h3>

          <v-container>
            <v-row class="d-flex justify-center">
              <v-col
                  class=""
                  cols="12"
                  md="12"
              >

                <template v-for="field in step.fields" :key="field.id">
                  <v-text-field
                      v-if="field.type === 'text'"
                      hide-details="auto"
                      :label="field.label"
                  ></v-text-field>

                  <v-select
                      v-if="field.type === 'select'"
                      :label="field.label"
                      :items="field.options"
                  ></v-select>

                  <v-text-field
                      v-if="field.type === 'number'"
                      :label="field.label"
                      model-value="10.00"
                      prefix="€"
                  ></v-text-field>

                  <v-btn-toggle
                      value=""
                      v-if="field.type === 'select-button'"
                  >

                    <v-btn :color="option.iconColor"
                        v-for="option in field.options" :key="option.id">
                      <span class="mr-2">{{ option.type }}</span>
                      <v-icon start :color="option.iconColor" size="x-large">
                        mdi-glass-wine
                      </v-icon>
                    </v-btn>
                  </v-btn-toggle>

                </template>
              </v-col>
            </v-row>
          </v-container>

        </template>

      </v-stepper>

    </v-card-text>
  </v-card>
</template>

<style scoped>


</style>