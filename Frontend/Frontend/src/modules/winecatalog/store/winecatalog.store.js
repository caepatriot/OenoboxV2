import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { createWine, deleteWine, getWineById, listWines, suggestWines, updateWine } from '@/modules/winecatalog/api/wine.api.js'

export const useWineCatalogStore = defineStore('winecatalog', () => {
  const wines = ref([])
  const suggestions = ref([])
  const selectedWine = ref(null)
  const isLoading = ref(false)
  const isSaving = ref(false)
  const error = ref(null)
  const lastQuery = ref('')
  const lastFilters = ref({
    query: '',
    region: '',
    country: '',
    producer: '',
    wineType: '',
  })

  const sortedWines = computed(() =>
    [...wines.value].sort((a, b) => (a?.name || '').localeCompare(b?.name || '', undefined, { sensitivity: 'base' }))
  )

  const normalizeFilters = (filtersOrQuery = '') => {
    if (typeof filtersOrQuery === 'string') {
      return {
        query: filtersOrQuery.trim(),
        region: '',
        country: '',
        producer: '',
        wineType: '',
      }
    }

    const source = filtersOrQuery && typeof filtersOrQuery === 'object' ? filtersOrQuery : {}

    return {
      query: String(source.query || '').trim(),
      region: String(source.region || '').trim(),
      country: String(source.country || '').trim(),
      producer: String(source.producer || '').trim(),
      wineType: String(source.wineType || '').trim(),
    }
  }

  const fetchWines = async (filtersOrQuery = '') => {
    try {
      isLoading.value = true
      error.value = null
      const normalizedFilters = normalizeFilters(filtersOrQuery)
      lastQuery.value = normalizedFilters.query
      lastFilters.value = normalizedFilters
      const response = await listWines(normalizedFilters)
      wines.value = Array.isArray(response?.data) ? response.data : []
      return wines.value
    } catch (err) {
      error.value = err?.response?.data || err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchWineById = async (id) => {
    try {
      isLoading.value = true
      error.value = null
      const response = await getWineById(id)
      selectedWine.value = response?.data || null
      return selectedWine.value
    } catch (err) {
      error.value = err?.response?.data || err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const saveWine = async (payload, id = null) => {
    try {
      isSaving.value = true
      error.value = null
      const response = id ? await updateWine(id, payload) : await createWine(payload)
      const savedWine = response?.data
      if (!savedWine) return null

      const index = wines.value.findIndex((wine) => wine.id === savedWine.id)
      if (index === -1) {
        wines.value.push(savedWine)
      } else {
        wines.value[index] = savedWine
      }

      if (selectedWine.value?.id === savedWine.id) {
        selectedWine.value = savedWine
      }

      return savedWine
    } catch (err) {
      error.value = err?.response?.data || err.message
      throw err
    } finally {
      isSaving.value = false
    }
  }

  const removeWine = async (id) => {
    try {
      isSaving.value = true
      error.value = null
      await deleteWine(id)
      wines.value = wines.value.filter((wine) => wine.id !== id)
      if (selectedWine.value?.id === id) {
        selectedWine.value = null
      }
    } catch (err) {
      error.value = err?.response?.data || err.message
      throw err
    } finally {
      isSaving.value = false
    }
  }

  const fetchSuggestions = async (query) => {
    if (!query || query.trim().length < 2) {
      suggestions.value = []
      return suggestions.value
    }

    try {
      const response = await suggestWines(query.trim())
      suggestions.value = Array.isArray(response?.data) ? response.data : []
      return suggestions.value
    } catch {
      suggestions.value = []
      return suggestions.value
    }
  }

  const clearSuggestions = () => {
    suggestions.value = []
  }

  return {
    wines,
    suggestions,
    selectedWine,
    isLoading,
    isSaving,
    error,
    lastQuery,
    lastFilters,
    sortedWines,
    fetchWines,
    fetchWineById,
    saveWine,
    removeWine,
    fetchSuggestions,
    clearSuggestions,
  }
})
