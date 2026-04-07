<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWineCatalogStore } from '@/modules/winecatalog/store/winecatalog.store.js'

const route = useRoute()
const router = useRouter()
const store = useWineCatalogStore()

const wineId = computed(() => Number(route.params.wineId ?? route.params.wineVintageId))
const wine = computed(() => store.selectedWine)

const deleteWine = async () => {
  if (!wine.value?.id) return
  await store.removeWine(wine.value.id)
  await router.push({ name: 'wines' })
}

onMounted(async () => {
  if (!Number.isFinite(wineId.value)) {
    await router.replace({ name: 'wines' })
    return
  }

  try {
    await store.fetchWineById(wineId.value)
  } catch {
    await router.replace({ name: 'wines' })
  }
})
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Wine detail</p>
        <h1 class="oeno-title">{{ wine?.name || 'Wine' }}</h1>
        <p class="oeno-subtitle">Review all shared catalog details used for cellar placements and tasting notes.</p>
      </div>
      <div class="d-flex ga-2 flex-wrap">
        <v-btn :to="{ name: 'wine-edit', params: { wineId } }" class="oeno-btn oeno-btn--secondary">
          Edit
        </v-btn>
        <v-btn :loading="store.isSaving" class="oeno-btn" color="error" variant="tonal" @click="deleteWine">
          Delete
        </v-btn>
      </div>
    </div>

    <v-alert v-if="store.error" type="error" variant="tonal">
      {{ store.error }}
    </v-alert>

    <v-progress-linear
      v-if="store.isLoading"
      color="#7a2838"
      indeterminate
      rounded
      class="mb-2"
    />

    <v-card v-if="wine" class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <div class="oeno-two-col">
          <div class="oeno-stack">
            <div>
              <p class="oeno-card-label mb-1">Producer</p>
              <div class="oeno-strong">{{ wine.producer || 'Unknown' }}</div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Type</p>
              <div class="oeno-strong">{{ wine.wineTypeName || 'Unknown' }}</div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Region</p>
              <div class="oeno-strong">{{ wine.region || 'Unknown' }}</div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Country</p>
              <div class="oeno-strong">{{ wine.country || 'Unknown' }}</div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Year</p>
              <div class="oeno-strong">{{ wine.year || 'N/A' }}</div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Appellation</p>
              <div class="oeno-strong">{{ wine.appellation || 'N/A' }}</div>
            </div>
          </div>

          <div class="oeno-stack">
            <div>
              <p class="oeno-card-label mb-2">Grape varieties</p>
              <div class="d-flex flex-wrap ga-2">
                <v-chip v-for="cepage in wine.cepages || []" :key="cepage" class="oeno-chip" variant="flat" size="small">
                  {{ cepage }}
                </v-chip>
                <span v-if="!(wine.cepages || []).length" class="oeno-muted">No grape varieties saved.</span>
              </div>
            </div>
            <div>
              <p class="oeno-card-label mb-2">Aromas</p>
              <div class="d-flex flex-wrap ga-2">
                <v-chip v-for="aroma in wine.aromaNotes || []" :key="aroma" variant="outlined" size="small">
                  {{ aroma }}
                </v-chip>
                <span v-if="!(wine.aromaNotes || []).length" class="oeno-muted">No aromas saved.</span>
              </div>
            </div>
            <div>
              <p class="oeno-card-label mb-2">Food pairings</p>
              <div class="d-flex flex-wrap ga-2">
                <v-chip v-for="pairing in wine.foodPairings || []" :key="pairing" variant="outlined" size="small">
                  {{ pairing }}
                </v-chip>
                <span v-if="!(wine.foodPairings || []).length" class="oeno-muted">No pairings saved.</span>
              </div>
            </div>
            <div>
              <p class="oeno-card-label mb-1">Source</p>
              <div class="oeno-strong">{{ wine.dataSource || 'community' }}</div>
            </div>
          </div>
        </div>

        <div class="mt-6">
          <p class="oeno-card-label mb-2">Description</p>
          <p class="oeno-muted">{{ wine.description || 'No description saved for this wine yet.' }}</p>
        </div>
      </v-card-text>
    </v-card>
  </section>
</template>
