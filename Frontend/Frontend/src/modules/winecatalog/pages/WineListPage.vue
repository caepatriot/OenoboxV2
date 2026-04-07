<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useWineCatalogStore } from '@/modules/winecatalog/store/winecatalog.store.js'

const store = useWineCatalogStore()
const search = ref('')
const region = ref('')
const country = ref('')
const producer = ref('')
const wineType = ref('')
let searchTimer = null

const wines = computed(() => store.sortedWines)
const wineCount = computed(() => wines.value.length)

const uniqueTextValues = (items, field) => {
  const values = new Set()
  for (const item of Array.isArray(items) ? items : []) {
    const value = String(item?.[field] || '').trim()
    if (value) values.add(value)
  }

  return [...values].sort((a, b) => a.localeCompare(b, undefined, { sensitivity: 'base' }))
}

const regionOptions = computed(() => uniqueTextValues(store.wines, 'region'))
const countryOptions = computed(() => uniqueTextValues(store.wines, 'country'))
const producerOptions = computed(() => uniqueTextValues(store.wines, 'producer'))
const wineTypeOptions = computed(() => uniqueTextValues(store.wines, 'wineTypeName'))

const activeFilters = computed(() => {
  const chips = []
  if (region.value) chips.push({ key: 'region', label: `Region: ${region.value}` })
  if (country.value) chips.push({ key: 'country', label: `Country: ${country.value}` })
  if (producer.value) chips.push({ key: 'producer', label: `Producer: ${producer.value}` })
  if (wineType.value) chips.push({ key: 'wineType', label: `Type: ${wineType.value}` })
  return chips
})

const hasActiveFilters = computed(() => Boolean(search.value.trim() || region.value || country.value || producer.value || wineType.value))

const buildFilters = () => ({
  query: search.value.trim(),
  region: region.value,
  country: country.value,
  producer: producer.value,
  wineType: wineType.value,
})

const loadWines = async () => {
  await store.fetchWines(buildFilters())
}

onMounted(async () => {
  await loadWines()
})

watch([search, region, country, producer, wineType], () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    await loadWines()
  }, 350)
})

const clearAllFilters = () => {
  search.value = ''
  region.value = ''
  country.value = ''
  producer.value = ''
  wineType.value = ''
}

const clearFilter = (key) => {
  if (key === 'region') region.value = ''
  if (key === 'country') country.value = ''
  if (key === 'producer') producer.value = ''
  if (key === 'wineType') wineType.value = ''
}

onBeforeUnmount(() => {
  if (searchTimer) clearTimeout(searchTimer)
})
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Wine catalog</p>
        <h1 class="oeno-title">Shared wine references</h1>
        <p class="oeno-subtitle">
          Search wines already known by the community and add missing labels so everyone can reuse them in cellar tracking and tastings.
        </p>
      </div>
      <RouterLink to="/app/wines/new" class="oeno-link-chip">
        <v-icon size="18">mdi-plus</v-icon>
        Add wine
      </RouterLink>
    </div>

    <div class="oeno-hero oeno-hero--compact mb-2">
      <div class="oeno-hero__content">
        <div class="oeno-mini-stats">
          <div class="oeno-soft-tile">
            <div class="oeno-card-label">Catalog entries</div>
            <div class="oeno-soft-value">{{ wineCount }}</div>
          </div>
        </div>
      </div>

      <div class="oeno-search-box">
        <v-text-field
          v-model="search"
          hide-details
          variant="solo-filled"
          density="comfortable"
          placeholder="Search by name, region, producer, grape..."
          prepend-inner-icon="mdi-magnify"
          class="oeno-search-field"
        />
      </div>
    </div>

    <v-card class="oeno-card mb-2" variant="flat">
      <v-card-text class="pa-4">
        <div class="d-flex flex-wrap ga-3 align-center">
          <v-select
            v-model="region"
            :items="regionOptions"
            label="Region"
            placeholder="All regions"
            variant="outlined"
            density="comfortable"
            hide-details
            clearable
            class="flex-grow-1"
          />
          <v-select
            v-model="country"
            :items="countryOptions"
            label="Country"
            placeholder="All countries"
            variant="outlined"
            density="comfortable"
            hide-details
            clearable
            class="flex-grow-1"
          />
          <v-select
            v-model="producer"
            :items="producerOptions"
            label="Producer"
            placeholder="All producers"
            variant="outlined"
            density="comfortable"
            hide-details
            clearable
            class="flex-grow-1"
          />
          <v-select
            v-model="wineType"
            :items="wineTypeOptions"
            label="Type / Color"
            placeholder="All types"
            variant="outlined"
            density="comfortable"
            hide-details
            clearable
            class="flex-grow-1"
          />
          <v-btn variant="text" :disabled="!hasActiveFilters" @click="clearAllFilters">
            Clear filters
          </v-btn>
        </div>

        <div v-if="activeFilters.length" class="d-flex flex-wrap ga-2 mt-3">
          <v-chip
            v-for="filter in activeFilters"
            :key="filter.key"
            size="small"
            variant="outlined"
            closable
            @click:close="clearFilter(filter.key)"
          >
            {{ filter.label }}
          </v-chip>
        </div>
      </v-card-text>
    </v-card>

    <v-alert v-if="store.error" type="error" variant="tonal" class="mb-2">
      {{ store.error }}
    </v-alert>

    <v-progress-linear
      v-if="store.isLoading"
      color="#7a2838"
      indeterminate
      rounded
      class="mb-2"
    />

    <div class="oeno-stack">
      <v-card
        v-for="wine in wines"
        :key="wine.id || `${wine.name}-${wine.year}`"
        class="oeno-card"
        variant="flat"
      >
        <v-card-text class="pa-6">
          <div class="d-flex align-start justify-space-between flex-wrap ga-3">
            <div>
              <h2 class="oeno-card-title mb-1">{{ wine.name }}</h2>
              <p class="oeno-muted mb-2">
                {{ wine.region || 'Unknown region' }}
                <span v-if="wine.year"> | {{ wine.year }}</span>
                <span v-if="wine.country"> | {{ wine.country }}</span>
              </p>
              <div class="d-flex flex-wrap ga-2">
                <v-chip v-if="wine.wineTypeName" size="small" class="oeno-chip" variant="flat">
                  {{ wine.wineTypeName }}
                </v-chip>
                <v-chip
                  v-for="cepage in wine.cepages || []"
                  :key="`${wine.id}-cepage-${cepage}`"
                  size="small"
                  variant="outlined"
                >
                  {{ cepage }}
                </v-chip>
                <v-chip v-if="wine.dataSource" size="small" variant="outlined">
                  {{ wine.dataSource }}
                </v-chip>
              </div>
            </div>

            <div class="d-flex ga-2 flex-wrap">
              <v-btn
                :to="{ name: 'wine-detail', params: { wineId: wine.id } }"
                variant="text"
                size="small"
              >
                View
              </v-btn>
              <v-btn
                :to="{ name: 'wine-edit', params: { wineId: wine.id } }"
                variant="text"
                size="small"
              >
                Edit
              </v-btn>
            </div>
          </div>
        </v-card-text>
      </v-card>

      <v-card v-if="!store.isLoading && !wines.length" class="oeno-card" variant="flat">
        <v-card-text class="pa-8 text-center">
          <div class="oeno-empty-icon mx-auto mb-4">
            <v-icon size="30">mdi-bottle-wine-outline</v-icon>
          </div>
          <h2 class="oeno-card-title mb-2">No wines found</h2>
          <p class="oeno-muted mb-5">No catalog entries match your search yet.</p>
          <v-btn to="/app/wines/new" class="oeno-btn oeno-btn--primary">
            Add the first matching wine
          </v-btn>
        </v-card-text>
      </v-card>
    </div>
  </section>
</template>
