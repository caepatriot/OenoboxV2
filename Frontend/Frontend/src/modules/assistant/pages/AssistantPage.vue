<script setup>
import { computed, onMounted } from 'vue'
import { useCaveStore } from '@/modules/cellar/store/cellar.store.js'
import { buildOliSuggestions, extractPlacements, safeArray } from '@/shared/helpers/cellarMetrics.js'

const caveStore = useCaveStore()

onMounted(async () => {
  await Promise.allSettled([caveStore.loadCaves(), caveStore.getWines()])
})

const placements = computed(() => extractPlacements(caveStore.caves))
const suggestions = computed(() => buildOliSuggestions({ caves: caveStore.caves, placements: placements.value }))
const cellarNames = computed(() => safeArray(caveStore.caves).map((cave) => cave.name).filter(Boolean))
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Assistant</p>
        <h1 class="oeno-title">Oli</h1>
        <p class="oeno-subtitle">A design pass that puts assistant-style recommendations into the same premium visual language as the concept app.</p>
      </div>
      <RouterLink to="/app/inventory" class="oeno-link-chip">
        <v-icon size="18">mdi-arrow-right</v-icon>
        Open inventory
      </RouterLink>
    </div>

    <div class="oeno-hero mb-6">
      <div class="oeno-hero__content">
        <p class="oeno-eyebrow text-amber-100">Smart cellar companion</p>
        <h2 class="oeno-hero__title">Your collection is becoming readable at a glance.</h2>
        <p class="oeno-hero__text">Oli can already highlight the most stocked bottles, spot conditions worth watching, and connect inventory actions back to the cellar workspace.</p>
      </div>
      <div class="oeno-hero__aside">
        <div class="oeno-hero-badge">{{ cellarNames.join(' Â· ') || 'No cellar loaded yet' }}</div>
      </div>
    </div>

    <div class="oeno-stack">
      <v-card v-for="(suggestion, index) in suggestions" :key="`${suggestion.title}-${index}`" class="oeno-card oeno-card--dark" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-start justify-space-between ga-4">
            <div>
              <p class="oeno-card-label oeno-card-label--light mb-2">Oli suggestion</p>
              <h2 class="oeno-card-title oeno-card-title--light mb-2">{{ suggestion.title }}</h2>
              <p class="oeno-muted-light">{{ suggestion.description }}</p>
            </div>
            <div class="oeno-pill-dark">{{ suggestion.tone }}</div>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </section>
</template>


