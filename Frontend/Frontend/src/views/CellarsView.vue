<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCaveStore } from '@/stores/cave.js'
import { cellarCapacity, cellarOccupied, occupancyRate, safeArray } from '@/utils/cellarMetrics.js'

const router = useRouter()
const caveStore = useCaveStore()

onMounted(async () => {
  await caveStore.loadCaves().catch(() => {})
})

const caves = computed(() => caveStore.caves)

const caveCards = computed(() => {
  return safeArray(caves.value).map((cave) => {
    const capacity = cellarCapacity(cave)
    const occupied = cellarOccupied(cave)
    return {
      ...cave,
      capacity,
      occupied,
      occupancy: occupancyRate(occupied, capacity),
      unitsCount: safeArray(cave.units).length,
    }
  })
})

function selectCaveAndGo(cave, path) {
  caveStore.selectCave(cave)
  const firstUnit = safeArray(cave.units)[0]
  if (firstUnit) {
    caveStore.selectUnit(firstUnit)
  }
  router.push(path)
}
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Physical cellar</p>
        <h1 class="oeno-title">My cellars</h1>
        <p class="oeno-subtitle">Manage each storage space with the warmer, more premium visual direction from the cellar concept.</p>
      </div>
      <RouterLink to="/cave-admin" class="oeno-link-chip">
        <v-icon size="18">mdi-pencil-ruler</v-icon>
        Create or edit layout
      </RouterLink>
    </div>

    <div class="oeno-card-grid oeno-card-grid--wide">
      <v-card
        v-for="cave in caveCards"
        :key="cave.id"
        class="oeno-card oeno-cellar-card"
        variant="flat"
      >
        <v-card-text class="pa-6">
          <div class="d-flex align-start justify-space-between ga-4 mb-5 flex-wrap">
            <div>
              <p class="oeno-card-label mb-2">Cellar</p>
              <h2 class="oeno-card-title">{{ cave.name || 'Unnamed cellar' }}</h2>
              <p class="oeno-muted mt-2">
                {{ cave.description || 'Ready to manage racks, slots, bottles and cellar conditions.' }}
              </p>
            </div>

            <div class="oeno-icon-badge">
              <v-icon size="28">mdi-glass-wine</v-icon>
            </div>
          </div>

          <div class="oeno-mini-stats mb-5">
            <div class="oeno-soft-tile">
              <div class="oeno-card-label">Temperature</div>
              <div class="oeno-soft-value">{{ Number(cave.temperature ?? 12).toFixed(1) }}°C</div>
            </div>
            <div class="oeno-soft-tile">
              <div class="oeno-card-label">Humidity</div>
              <div class="oeno-soft-value">{{ Number(cave.humidity ?? 70).toFixed(0) }}%</div>
            </div>
            <div class="oeno-soft-tile">
              <div class="oeno-card-label">Racks & units</div>
              <div class="oeno-soft-value">{{ cave.unitsCount }}</div>
            </div>
          </div>

          <div class="mb-5">
            <div class="d-flex align-center justify-space-between mb-2">
              <span class="oeno-card-label">Occupancy</span>
              <span class="oeno-strong">{{ cave.occupied }} / {{ cave.capacity }} bottles</span>
            </div>
            <v-progress-linear
              :model-value="cave.occupancy"
              height="12"
              rounded
              bg-color="rgba(120, 49, 53, 0.10)"
              color="#8d2f3c"
            />
            <p class="oeno-muted mt-2">{{ cave.occupancy }}% of mapped capacity in use.</p>
          </div>

          <div class="d-flex ga-3 flex-wrap">
            <v-btn class="oeno-btn oeno-btn--primary" @click="selectCaveAndGo(cave, '/cave-test')">
              Explore workspace
            </v-btn>
            <v-btn class="oeno-btn oeno-btn--secondary" variant="flat" @click="selectCaveAndGo(cave, '/cave-admin')">
              Edit layout
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <v-card v-if="!caveCards.length" class="oeno-card" variant="flat">
      <v-card-text class="pa-8 text-center">
        <div class="oeno-empty-icon mx-auto mb-4">
          <v-icon size="30">mdi-bottle-wine-outline</v-icon>
        </div>
        <h2 class="oeno-card-title mb-2">No cellar yet</h2>
        <p class="oeno-muted mb-4">Start by creating a cellar and drawing the physical layout of your real storage space.</p>
        <RouterLink to="/cave-admin" class="oeno-link-chip">
          <v-icon size="18">mdi-plus</v-icon>
          Create first cellar
        </RouterLink>
      </v-card-text>
    </v-card>
  </section>
</template>
