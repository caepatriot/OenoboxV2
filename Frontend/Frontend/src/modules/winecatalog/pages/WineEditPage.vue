<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWineSearch } from '@/modules/winecatalog/composables/useWineSearch.js'
import { useWineCatalogStore } from '@/modules/winecatalog/store/winecatalog.store.js'

const route = useRoute()
const router = useRouter()
const store = useWineCatalogStore()

const emptyForm = () => ({
  name: '',
  wineTypeName: '',
  cepages: [],
  region: '',
  year: null,
  producer: '',
  country: '',
  appellation: '',
  description: '',
  aromaNotes: [],
  foodPairings: [],
  imageUrl: '',
  dataSource: 'community',
  externalId: null,
})

const form = reactive(emptyForm())
const formError = ref(null)

const wineId = computed(() => Number(route.params.wineId))
const isEdit = computed(() => Number.isFinite(wineId.value))

const {
  query: lookupQuery,
  results: lookupResults,
  isLoading: lookupLoading,
} = useWineSearch((query) => store.fetchSuggestions(query), { debounceMs: 300, minChars: 2 })

const assignFromWine = (wine) => {
  form.name = wine?.name || ''
  form.wineTypeName = wine?.wineTypeName || ''
  form.cepages = Array.isArray(wine?.cepages) ? [...wine.cepages] : []
  form.region = wine?.region || ''
  form.year = wine?.year ?? null
  form.producer = wine?.producer || ''
  form.country = wine?.country || ''
  form.appellation = wine?.appellation || ''
  form.description = wine?.description || ''
  form.aromaNotes = Array.isArray(wine?.aromaNotes) ? [...wine.aromaNotes] : []
  form.foodPairings = Array.isArray(wine?.foodPairings) ? [...wine.foodPairings] : []
  form.imageUrl = wine?.imageUrl || ''
  form.dataSource = wine?.dataSource || 'community'
  form.externalId = wine?.externalId || null
}

const applySuggestion = (suggestion) => {
  assignFromWine({
    ...suggestion,
    cepages: suggestion?.cepages || [],
    aromaNotes: suggestion?.aromaNotes || [],
    foodPairings: suggestion?.foodPairings || [],
  })
}

const save = async () => {
  formError.value = null

  if (!form.name?.trim()) {
    formError.value = 'Wine name is required.'
    return
  }

  const payload = {
    ...form,
    name: form.name.trim(),
    wineTypeName: form.wineTypeName?.trim() || null,
    cepages: Array.from(new Set((form.cepages || []).map((item) => String(item || '').trim()).filter(Boolean))),
    region: form.region?.trim() || null,
    year: Number.isFinite(Number(form.year)) ? Number(form.year) : null,
    producer: form.producer?.trim() || null,
    country: form.country?.trim() || null,
    appellation: form.appellation?.trim() || null,
    description: form.description?.trim() || null,
    aromaNotes: Array.from(new Set((form.aromaNotes || []).map((item) => String(item || '').trim()).filter(Boolean))),
    foodPairings: Array.from(new Set((form.foodPairings || []).map((item) => String(item || '').trim()).filter(Boolean))),
    imageUrl: form.imageUrl?.trim() || null,
    dataSource: form.dataSource?.trim() || 'community',
    externalId: form.externalId ? String(form.externalId).trim() : null,
  }

  try {
    const savedWine = await store.saveWine(payload, isEdit.value ? wineId.value : null)
    if (!savedWine?.id) return
    await router.push({ name: 'wine-detail', params: { wineId: savedWine.id } })
  } catch (err) {
    formError.value = err?.response?.data || err.message
  }
}

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const wine = await store.fetchWineById(wineId.value)
    assignFromWine(wine || {})
  } catch {
    await router.replace({ name: 'wines' })
  }
})
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">{{ isEdit ? 'Edit wine' : 'Add wine' }}</p>
        <h1 class="oeno-title">{{ isEdit ? 'Update wine catalog entry' : 'Create wine catalog entry' }}</h1>
        <p class="oeno-subtitle">
          Start with a quick lookup to pre-fill data, then adjust details before saving to the shared catalog.
        </p>
      </div>
      <RouterLink to="/app/wines" class="oeno-link-chip">
        <v-icon size="18">mdi-arrow-left</v-icon>
        Back to wines
      </RouterLink>
    </div>

    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <h2 class="oeno-card-title mb-4">Smart lookup</h2>
        <v-text-field
          v-model="lookupQuery"
          label="Search a wine label"
          placeholder="Type a label to fetch local/public suggestions"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          hide-details
        />

        <v-progress-linear v-if="lookupLoading" indeterminate rounded color="#7a2838" class="mt-3" />

        <v-list
          v-if="lookupResults.length"
          class="mt-3"
          rounded
          bg-color="transparent"
        >
          <v-list-item
            v-for="candidate in lookupResults"
            :key="`${candidate.externalId || candidate.id || candidate.name}-${candidate.dataSource || 'source'}`"
            :title="candidate.name"
            :subtitle="[candidate.producer, candidate.region, candidate.wineTypeName].filter(Boolean).join(' · ')"
          >
            <template #append>
              <v-btn size="small" variant="text" @click="applySuggestion(candidate)">Use</v-btn>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>

    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <h2 class="oeno-card-title mb-4">Wine details</h2>
        <v-alert v-if="formError || store.error" type="error" variant="tonal" class="mb-4">
          {{ formError || store.error }}
        </v-alert>

        <div class="oeno-two-col">
          <v-text-field v-model="form.name" label="Wine name *" variant="outlined" />
          <v-text-field v-model="form.wineTypeName" label="Wine type" variant="outlined" />
          <v-text-field v-model="form.producer" label="Producer" variant="outlined" />
          <v-text-field v-model="form.year" label="Vintage year" type="number" variant="outlined" />
          <v-text-field v-model="form.region" label="Region" variant="outlined" />
          <v-text-field v-model="form.country" label="Country" variant="outlined" />
          <v-text-field v-model="form.appellation" label="Appellation" variant="outlined" />
          <v-text-field v-model="form.imageUrl" label="Image URL" variant="outlined" />
        </div>

        <v-combobox
          v-model="form.cepages"
          label="Grape varieties"
          multiple
          chips
          closable-chips
          variant="outlined"
          class="mt-2"
        />

        <v-combobox
          v-model="form.aromaNotes"
          label="Aroma notes"
          multiple
          chips
          closable-chips
          variant="outlined"
          class="mt-2"
        />

        <v-combobox
          v-model="form.foodPairings"
          label="Food pairings"
          multiple
          chips
          closable-chips
          variant="outlined"
          class="mt-2"
        />

        <v-textarea
          v-model="form.description"
          label="Description"
          variant="outlined"
          rows="4"
          auto-grow
          class="mt-2"
        />

        <div class="d-flex ga-2 mt-4">
          <v-btn
            :loading="store.isSaving"
            class="oeno-btn oeno-btn--primary"
            @click="save"
          >
            {{ isEdit ? 'Save changes' : 'Create wine' }}
          </v-btn>
          <v-btn
            variant="text"
            class="oeno-btn"
            :to="isEdit ? { name: 'wine-detail', params: { wineId } } : { name: 'wines' }"
          >
            Cancel
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </section>
</template>
