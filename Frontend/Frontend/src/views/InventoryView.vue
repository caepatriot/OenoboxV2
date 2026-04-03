<script setup>
import { computed, onMounted, ref } from 'vue'
import { useCaveStore } from '@/stores/cave.js'
import { extractPlacements, formatBottleTitle, formatCurrency, safeArray } from '@/utils/cellarMetrics.js'

const caveStore = useCaveStore()
const search = ref('')

onMounted(async () => {
  await Promise.allSettled([caveStore.loadCaves(), caveStore.getWines()])
})

const placements = computed(() => extractPlacements(caveStore.caves))

const filteredPlacements = computed(() => {
  const query = search.value.trim().toLowerCase()
  if (!query) return placements.value

  return placements.value.filter((placement) => {
    const haystack = [
      placement?.wine?.name,
      placement?.wine?.region,
      placement?.wine?.type,
      placement?.caveName,
      placement?.unitName,
      placement?.spaceLabel,
      placement?.wine?.year,
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase()

    return haystack.includes(query)
  })
})

const groupedPlacements = computed(() => {
  return filteredPlacements.value.reduce((groups, placement) => {
    const key = `${placement.caveName}__${placement.unitName}`
    if (!groups[key]) {
      groups[key] = {
        key,
        caveName: placement.caveName,
        unitName: placement.unitName,
        items: [],
      }
    }
    groups[key].items.push(placement)
    return groups
  }, {})
})

const groupsList = computed(() => Object.values(groupedPlacements.value))
const knownWines = computed(() => safeArray(caveStore.wines).length)
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Inventory</p>
        <h1 class="oeno-title">Bottle inventory</h1>
        <p class="oeno-subtitle">Search stock by bottle, rack, or cellar section and jump back into the legacy placement workflow when needed.</p>
      </div>
      <RouterLink to="/cave-placement" class="oeno-link-chip">
        <v-icon size="18">mdi-swap-horizontal-bold</v-icon>
        Manage placements
      </RouterLink>
    </div>

    <div class="oeno-hero oeno-hero--compact mb-6">
      <div class="oeno-hero__content">
        <div class="oeno-mini-stats">
          <div class="oeno-soft-tile">
            <div class="oeno-card-label">Placed references</div>
            <div class="oeno-soft-value">{{ filteredPlacements.length }}</div>
          </div>
          <div class="oeno-soft-tile">
            <div class="oeno-card-label">Known wines</div>
            <div class="oeno-soft-value">{{ knownWines }}</div>
          </div>
        </div>
      </div>

      <div class="oeno-search-box">
        <v-text-field
          v-model="search"
          hide-details
          variant="solo-filled"
          density="comfortable"
          placeholder="Search a bottle, region, rack or cellar"
          prepend-inner-icon="mdi-magnify"
          class="oeno-search-field"
        />
      </div>
    </div>

    <div class="oeno-stack">
      <v-card v-for="group in groupsList" :key="group.key" class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <div>
              <p class="oeno-card-label mb-2">{{ group.caveName }}</p>
              <h2 class="oeno-card-title">{{ group.unitName }}</h2>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ group.items.length }} tracked slots</v-chip>
          </div>

          <div class="oeno-list">
            <div v-for="item in group.items" :key="item.id || `${item.spaceId}-${item.wine?.id || item.spaceLabel}`" class="oeno-list-item">
              <div>
                <div class="oeno-strong">{{ formatBottleTitle(item.wine) }}</div>
                <div class="oeno-muted mt-1">{{ item.spaceLabel }} · {{ item.quantity }} bottle<span v-if="item.quantity > 1">s</span></div>
              </div>
              <div class="text-right">
                <div class="oeno-strong">{{ formatCurrency((item.wine?.prixActuel ?? item.wine?.prixLancement ?? 0) * item.quantity) }}</div>
                <div class="oeno-muted mt-1">{{ item.wine?.type || 'Wine' }}</div>
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>

      <v-card v-if="!groupsList.length" class="oeno-card" variant="flat">
        <v-card-text class="pa-8 text-center">
          <div class="oeno-empty-icon mx-auto mb-4">
            <v-icon size="30">mdi-magnify-close</v-icon>
          </div>
          <h2 class="oeno-card-title mb-2">No matching bottles</h2>
          <p class="oeno-muted">Try another search or use the placement screen to assign new stock to your cellar map.</p>
        </v-card-text>
      </v-card>
    </div>
  </section>
</template>
