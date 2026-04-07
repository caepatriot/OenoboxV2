<script setup>
import { computed, onMounted } from 'vue'
import { useCaveStore } from '@/modules/cellar/store/cellar.store.js'
import { buildOliSuggestions, cellarCapacity, extractPlacements, extractUnits, formatCurrency, inventoryValue, occupancyRate, safeArray } from '@/shared/helpers/cellarMetrics.js'

const caveStore = useCaveStore()

onMounted(async () => {
  await Promise.allSettled([caveStore.loadCaves(), caveStore.getWines()])
})

const placements = computed(() => extractPlacements(caveStore.caves))
const totalBottles = computed(() => placements.value.reduce((sum, placement) => sum + Number(placement.quantity || 1), 0))
const totalCapacity = computed(() => safeArray(caveStore.caves).reduce((sum, cave) => sum + cellarCapacity(cave), 0))
const totalUnits = computed(() => extractUnits(caveStore.caves).length)
const totalValue = computed(() => inventoryValue(placements.value))
const suggestions = computed(() => buildOliSuggestions({ caves: caveStore.caves, placements: placements.value }).slice(0, 2))
const occupancy = computed(() => occupancyRate(totalBottles.value, totalCapacity.value))
const cellarPreview = computed(() => safeArray(caveStore.caves).slice(0, 2))
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-hero mb-6">
      <div class="oeno-hero__content">
        <p class="oeno-eyebrow text-amber-100">Oenobox</p>
        <h1 class="oeno-hero__title">A warmer cellar dashboard for your real storage world.</h1>
        <p class="oeno-hero__text">
          This redesign brings OenoboxV2 closer to the Wine Cellar App concept: richer cards, a cleaner mobile-style shell, inventory-first screens and a more premium cellar feeling.
        </p>
        <div class="d-flex ga-3 flex-wrap mt-4">
          <RouterLink to="/app/cellars" class="oeno-link-chip oeno-link-chip--light">
            <v-icon size="18">mdi-view-grid-outline</v-icon>
            Open cellars
          </RouterLink>
          <RouterLink to="/app/tastings" class="oeno-link-chip oeno-link-chip--light">
            <v-icon size="18">mdi-glass-wine</v-icon>
            Tasting module
          </RouterLink>
        </div>
      </div>

      <div class="oeno-hero__aside">
        <div class="oeno-glass-panel">
          <div class="oeno-card-label oeno-card-label--light">Current occupancy</div>
          <div class="oeno-hero-metric">{{ occupancy }}%</div>
          <div class="oeno-muted-light">{{ totalBottles }} of {{ totalCapacity || 0 }} mapped bottles / slots</div>
        </div>
      </div>
    </div>

    <div class="oeno-card-grid mb-6">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Total bottles</p>
          <div class="oeno-metric">{{ totalBottles }}</div>
          <p class="oeno-muted mt-2">Tracked through cellar placements.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Collection value</p>
          <div class="oeno-metric">{{ formatCurrency(totalValue) }}</div>
          <p class="oeno-muted mt-2">Estimated from bottle price fields already present in your data.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Mapped units</p>
          <div class="oeno-metric">{{ totalUnits }}</div>
          <p class="oeno-muted mt-2">Racks, cabinets and mapped storage structures.</p>
        </v-card-text>
      </v-card>
    </div>

    <div class="oeno-two-col">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
            <div>
              <p class="oeno-card-label mb-1">Quick access</p>
              <h2 class="oeno-card-title">Main flows</h2>
            </div>
          </div>
          <div class="oeno-action-grid">
            <RouterLink to="/app/cellars" class="oeno-action-card">
              <v-icon size="26">mdi-home-floor-1</v-icon>
              <span>My cellars</span>
            </RouterLink>
            <RouterLink to="/app/inventory" class="oeno-action-card">
              <v-icon size="26">mdi-bottle-wine-outline</v-icon>
              <span>Inventory</span>
            </RouterLink>
            <RouterLink to="/cave-test" class="oeno-action-card">
              <v-icon size="26">mdi-vector-square</v-icon>
              <span>Workspace</span>
            </RouterLink>
            <RouterLink to="/app/tastings" class="oeno-action-card">
              <v-icon size="26">mdi-glass-wine</v-icon>
              <span>Tasting</span>
            </RouterLink>
          </div>
        </v-card-text>
      </v-card>

      <v-card class="oeno-card oeno-card--dark" variant="flat">
        <v-card-text class="pa-6">
          <p class="oeno-card-label oeno-card-label--light mb-2">Oli suggestions</p>
          <h2 class="oeno-card-title oeno-card-title--light mb-4">What stands out right now</h2>
          <div class="oeno-stack">
            <div v-for="(suggestion, index) in suggestions" :key="`${suggestion.title}-${index}`" class="oeno-list-item oeno-list-item--dark">
              <div>
                <div class="oeno-strong oeno-strong--light">{{ suggestion.title }}</div>
                <div class="oeno-muted-light mt-1">{{ suggestion.description }}</div>
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <v-card class="oeno-card mt-6" variant="flat">
      <v-card-text class="pa-6">
        <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
          <div>
            <p class="oeno-card-label mb-1">Cellar preview</p>
            <h2 class="oeno-card-title">Your mapped spaces</h2>
          </div>
          <RouterLink to="/app/cellars" class="oeno-link-chip">
            <v-icon size="18">mdi-arrow-right</v-icon>
            View all
          </RouterLink>
        </div>

        <div class="oeno-card-grid" v-if="cellarPreview.length">
          <div v-for="cave in cellarPreview" :key="cave.id" class="oeno-list-item oeno-list-item--soft">
            <div>
              <div class="oeno-strong">{{ cave.name || 'Unnamed cellar' }}</div>
              <div class="oeno-muted mt-1">{{ cave.description || 'Ready for layout editing and bottle placement.' }}</div>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ Number(cave.temperature ?? 12).toFixed(1) }}Â°C</v-chip>
          </div>
        </div>
        <p v-else class="oeno-muted">No cellar loaded yet. Use the cellar administration screen to create one.</p>
      </v-card-text>
    </v-card>
  </section>
</template>


