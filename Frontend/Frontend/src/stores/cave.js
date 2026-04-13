import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { caveApi, wineApi } from '@/services/api.js'

export const useCaveStore = defineStore('cave', () => {
  const caves = ref([])
  const bottlePlacements = ref([])
  const wines = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  const selectedCave = ref(null)
  const selectedUnit = ref(null)
  const selectedSpace = ref(null)

  const totalBottles = computed(() => {
    return bottlePlacements.value.reduce((total, placement) => total + Number(placement.quantity || 0), 0)
  })

  const availableSpaces = computed(() => {
    if (!selectedUnit.value) return []
    return (selectedUnit.value.spaces || []).filter((space) => Number(space.freeCapacity ?? space.capacity ?? 0) > 0 && !space.disabled)
  })

  const occupiedSpaces = computed(() => {
    if (!selectedUnit.value) return []
    return (selectedUnit.value.spaces || []).filter((space) => Number(space.occupiedQuantity || 0) > 0)
  })

  function syncBottlePlacements() {
    bottlePlacements.value = caves.value.flatMap((cave) =>
      (cave.units || []).flatMap((unit) =>
        (unit.spaces || []).flatMap((space) => space.currentBottles || []),
      ),
    )
  }

  function upsertCave(cave) {
    const index = caves.value.findIndex((item) => item.id === cave.id)
    if (index === -1) {
      caves.value.push(cave)
    } else {
      caves.value[index] = cave
    }
    if (selectedCave.value?.id === cave.id) {
      selectedCave.value = cave
      if (selectedUnit.value?.id) {
        selectedUnit.value = (cave.units || []).find((unit) => unit.id === selectedUnit.value.id) || null
      }
      if (selectedSpace.value?.id && selectedUnit.value) {
        selectedSpace.value = (selectedUnit.value.spaces || []).find((space) => space.id === selectedSpace.value.id) || null
      }
    }
    syncBottlePlacements()
  }

  async function withLoading(task) {
    try {
      isLoading.value = true
      error.value = null
      return await task()
    } catch (err) {
      error.value = err.response?.data || err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const loadCaves = async () => withLoading(async () => {
    const response = await caveApi.getAll()
    caves.value = response.data
    if (selectedCave.value?.id) {
      selectedCave.value = caves.value.find((cave) => cave.id === selectedCave.value.id) || null
    }
    syncBottlePlacements()
    return caves.value
  })

  const getCaveById = async (id) => withLoading(async () => {
    const response = await caveApi.getById(id)
    upsertCave(response.data)
    return response.data
  })

  const refreshCave = async (id) => withLoading(async () => {
    const response = await caveApi.getWorkspace(id)
    upsertCave(response.data)
    return response.data
  })

  const createCave = async (caveData) => withLoading(async () => {
    const response = await caveApi.create(caveData)
    upsertCave(response.data)
    return response.data
  })

  const importCave = async (caveData) => withLoading(async () => {
    const response = await caveApi.importLayout(caveData)
    upsertCave(response.data)
    return response.data
  })

  const restoreCave = async (id, caveData) => withLoading(async () => {
    const response = await caveApi.restore(id, caveData)
    upsertCave(response.data)
    return response.data
  })

  const updateCave = async (id, caveData) => withLoading(async () => {
    const response = await caveApi.update(id, caveData)
    upsertCave(response.data)
    return response.data
  })

  const deleteCave = async (id) => withLoading(async () => {
    await caveApi.delete(id)
    caves.value = caves.value.filter((cave) => cave.id !== id)
    if (selectedCave.value?.id === id) {
      selectedCave.value = null
      selectedUnit.value = null
      selectedSpace.value = null
    }
    syncBottlePlacements()
  })

  const addStorageUnit = async (caveId, unitData) => withLoading(async () => {
    const response = await caveApi.createUnit(caveId, unitData)
    await refreshCave(caveId)
    return response.data
  })

  const updateStorageUnit = async (caveId, unitId, unitData) => withLoading(async () => {
    const response = await caveApi.updateUnit(unitId, unitData)
    await refreshCave(caveId)
    return response.data
  })

  const deleteStorageUnit = async (caveId, unitId) => withLoading(async () => {
    await caveApi.deleteUnit(unitId)
    await refreshCave(caveId)
    if (selectedUnit.value?.id === unitId) {
      selectedUnit.value = null
      selectedSpace.value = null
    }
  })

  const updateSpace = async (caveId, spaceId, spaceData) => withLoading(async () => {
    const response = await caveApi.updateSpace(spaceId, spaceData)
    await refreshCave(caveId)
    return response.data
  })

  const bulkUpdateSpaces = async (caveId, payload) => withLoading(async () => {
    const response = await caveApi.bulkUpdateSpaces(payload)
    await refreshCave(caveId)
    return response.data
  })

  const addBottlePlacement = async (caveId, placementData) => withLoading(async () => {
    const response = await caveApi.addPlacement(placementData)
    await refreshCave(caveId)
    return response.data
  })

  const bulkAddPlacements = async (caveId, payload) => withLoading(async () => {
    const response = await caveApi.bulkAddPlacements(payload)
    await refreshCave(caveId)
    return response.data
  })

  const autoPlace = async (caveId, payload) => withLoading(async () => {
    const response = await caveApi.autoPlace(caveId, payload)
    await refreshCave(caveId)
    return response.data
  })

  const updateBottlePlacement = async (caveId, id, placementData) => withLoading(async () => {
    const response = await caveApi.updatePlacement(id, placementData)
    await refreshCave(caveId)
    return response.data
  })

  const removeBottlePlacement = async (caveId, id) => withLoading(async () => {
    await caveApi.deletePlacement(id)
    await refreshCave(caveId)
  })

  const exportCave = async (id) => withLoading(async () => {
    const response = await caveApi.exportLayout(id)
    return response.data
  })

  const getWines = async (params) => withLoading(async () => {
    const response = await wineApi.getAll(params)
    wines.value = response.data
    return wines.value
  })

  const createWine = async (wineData) => withLoading(async () => {
    const response = await wineApi.create(wineData)
    wines.value = [...wines.value, response.data].sort((a, b) => (a.name || '').localeCompare(b.name || '', undefined, { sensitivity: 'base' }))
    return response.data
  })

  const updateWine = async (id, wineData) => withLoading(async () => {
    const response = await wineApi.update(id, wineData)
    const index = wines.value.findIndex((wine) => wine.id === id)
    if (index !== -1) wines.value[index] = response.data
    return response.data
  })

  const deleteWine = async (id) => withLoading(async () => {
    await wineApi.delete(id)
    wines.value = wines.value.filter((wine) => wine.id !== id)
  })

  const searchWines = async (query) => {
    const needle = query?.trim()?.toLowerCase() || ''
    if (!needle) return wines.value
    return wines.value.filter((wine) => {
      return [wine.name, wine.region, wine.wineTypeName, wine.producer, wine.appellation]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(needle)
    })
  }

  const selectCave = (cave) => {
    selectedCave.value = cave
    selectedUnit.value = null
    selectedSpace.value = null
  }

  const selectUnit = (unit) => {
    selectedUnit.value = unit
    selectedSpace.value = null
  }

  const selectSpace = (space) => {
    selectedSpace.value = space
  }

  return {
    caves,
    bottlePlacements,
    wines,
    isLoading,
    error,
    selectedCave,
    selectedUnit,
    selectedSpace,
    totalBottles,
    availableSpaces,
    occupiedSpaces,
    loadCaves,
    getCaveById,
    refreshCave,
    createCave,
    importCave,
    restoreCave,
    updateCave,
    deleteCave,
    addStorageUnit,
    updateStorageUnit,
    deleteStorageUnit,
    updateSpace,
    bulkUpdateSpaces,
    addBottlePlacement,
    bulkAddPlacements,
    autoPlace,
    updateBottlePlacement,
    removeBottlePlacement,
    exportCave,
    getWines,
    createWine,
    updateWine,
    deleteWine,
    searchWines,
    selectCave,
    selectUnit,
    selectSpace,
  }
})
