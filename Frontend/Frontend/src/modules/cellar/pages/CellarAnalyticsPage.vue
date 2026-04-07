<script setup>
import { computed, onMounted } from 'vue'
import { useCaveStore } from '@/modules/cellar/store/cellar.store.js'
import { buildTypeDistribution, cellarCapacity, cellarOccupied, extractPlacements, extractUnits, formatCurrency, inventoryValue, occupancyRate, safeArray } from '@/shared/helpers/cellarMetrics.js'

const caveStore = useCaveStore()

onMounted(async () => {
  await Promise.allSettled([caveStore.loadCaves(), caveStore.getWines()])
})

const placements = computed(() => extractPlacements(caveStore.caves))
const totalValue = computed(() => inventoryValue(placements.value))
const totalCapacity = computed(() => safeArray(caveStore.caves).reduce((sum, cave) => sum + cellarCapacity(cave), 0))
const totalOccupied = computed(() => safeArray(caveStore.caves).reduce((sum, cave) => sum + cellarOccupied(cave), 0))
const unitsCount = computed(() => extractUnits(caveStore.caves).length)
const typeDistribution = computed(() => buildTypeDistribution(placements.value))

const cellarBreakdown = computed(() => {
  return safeArray(caveStore.caves).map((cave) => {
    const occupied = cellarOccupied(cave)
    const capacity = cellarCapacity(cave)
    return {
      id: cave.id,
      name: cave.name || 'Unnamed cellar',
      occupied,
      capacity,
      occupancy: occupancyRate(occupied, capacity),
    }
  })
})
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Analytics</p>
        <h1 class="oeno-title">Collection insights</h1>
        <p class="oeno-subtitle">A lighter analytics layer inspired by the concept design, but driven by your current mapped cellar data.</p>
      </div>
    </div>

    <div class="oeno-card-grid mb-6">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Estimated value</p>
          <div class="oeno-metric">{{ formatCurrency(totalValue) }}</div>
          <p class="oeno-muted mt-2">Based on tracked purchase or current prices in bottle records.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Occupancy</p>
          <div class="oeno-metric">{{ totalOccupied }} / {{ totalCapacity }}</div>
          <p class="oeno-muted mt-2">{{ occupancyRate(totalOccupied, totalCapacity) }}% of current mapped capacity.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Storage units</p>
          <div class="oeno-metric">{{ unitsCount }}</div>
          <p class="oeno-muted mt-2">Racks, cabinets and other mapped structures.</p>
        </v-card-text>
      </v-card>
    </div>

    <div class="oeno-two-col">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <h2 class="oeno-card-title mb-4">By cellar</h2>
          <div class="oeno-stack">
            <div v-for="cellar in cellarBreakdown" :key="cellar.id" class="oeno-list-item oeno-list-item--soft">
              <div>
                <div class="oeno-strong">{{ cellar.name }}</div>
                <div class="oeno-muted mt-1">{{ cellar.occupied }} / {{ cellar.capacity }} bottles</div>
              </div>
              <div class="oeno-bar-wrap">
                <v-progress-linear :model-value="cellar.occupancy" height="10" rounded color="#8d2f3c" bg-color="rgba(120, 49, 53, 0.1)" />
                <span class="oeno-muted">{{ cellar.occupancy }}%</span>
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>

      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <h2 class="oeno-card-title mb-4">Bottle styles</h2>
          <div class="oeno-stack">
            <div v-for="type in typeDistribution" :key="type.label" class="oeno-list-item oeno-list-item--soft">
              <div class="oeno-strong">{{ type.label }}</div>
              <v-chip class="oeno-chip" variant="flat">{{ type.value }} bottle<span v-if="type.value > 1">s</span></v-chip>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </section>
</template>

