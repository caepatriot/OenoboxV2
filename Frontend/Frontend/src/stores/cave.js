import {ref, computed} from 'vue'
import {defineStore} from 'pinia'
import {caveApi, wineApi} from '@/services/api.js'

export const useCaveStore = defineStore('cave', () => {
    // Reactive state
    const caves = ref([])
    const bottlePlacements = ref([])
    const wines = ref([])
    const isLoading = ref(false)
    const error = ref(null)

    // Selected items for UI state
    const selectedCave = ref(null)
    const selectedUnit = ref(null)
    const selectedSpace = ref(null)

    // Computed properties
    const totalBottles = computed(() => {
        return bottlePlacements.value.reduce((total, placement) => total + placement.quantity, 0)
    })

    const availableSpaces = computed(() => {
        if (!selectedUnit.value) return []

        const unitSpaces = selectedUnit.value.spaces || []
        return unitSpaces.filter(space => {
            const placement = bottlePlacements.value.find(p => p.spaceId === space.id)
            return !placement || placement.quantity < (space.capacity || 1)
        })
    })

    const occupiedSpaces = computed(() => {
        if (!selectedUnit.value) return []

        const unitSpaces = selectedUnit.value.spaces || []
        return unitSpaces.filter(space => {
            const placement = bottlePlacements.value.find(p => p.spaceId === space.id)
            return placement && placement.quantity > 0
        })
    })

    // Actions
    const loadCaves = async () => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.getAll()
            caves.value = response.data
            syncBottlePlacements()
            return caves.value
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const syncBottlePlacements = () => {
        bottlePlacements.value = caves.value.flatMap(c => 
            (c.units || []).flatMap(u => 
                (u.spaces || []).flatMap(s => s.currentBottles || [])
            )
        )
    }

    const getCaveById = async (id) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.getById(id)
            const cave = response.data
            const index = caves.value.findIndex(c => c.id === id)
            if (index !== -1) {
                caves.value[index] = cave
            } else {
                caves.value.push(cave)
            }
            syncBottlePlacements()
            return cave
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const createCave = async (caveData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.create(caveData)
            const newCave = response.data
            caves.value.push(newCave)
            return newCave
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const updateCave = async (id, caveData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.update(id, caveData)
            const updated = response.data
            const index = caves.value.findIndex(c => c.id === id)
            if (index !== -1) caves.value[index] = updated
            if (selectedCave.value?.id === id) selectedCave.value = updated
            syncBottlePlacements()
            return updated
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const deleteCave = async (id) => {
        try {
            isLoading.value = true
            error.value = null
            await caveApi.delete(id)
            caves.value = caves.value.filter(c => c.id !== id)
            if (selectedCave.value?.id === id) {
                selectedCave.value = null
                selectedUnit.value = null
                selectedSpace.value = null
            }
            syncBottlePlacements()
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const addStorageUnit = async (caveId, unitData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.createUnit(caveId, unitData)
            const newUnit = response.data
            const cave = caves.value.find(c => c.id === caveId)
            if (cave) {
                if (!cave.units) cave.units = []
                cave.units.push(newUnit)
                if (selectedCave.value?.id === caveId) selectedCave.value = {...cave}
            }
            return newUnit
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const updateStorageUnit = async (caveId, unitId, unitData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.updateUnit(unitId, unitData)
            const updatedUnit = response.data
            const cave = caves.value.find(c => c.id === caveId)
            if (cave && cave.units) {
                const index = cave.units.findIndex(u => u.id === unitId)
                if (index !== -1) cave.units[index] = updatedUnit
                if (selectedCave.value?.id === caveId) selectedCave.value = {...cave}
                if (selectedUnit.value?.id === unitId) selectedUnit.value = updatedUnit
            }
            syncBottlePlacements()
            return updatedUnit
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const deleteStorageUnit = async (caveId, unitId) => {
        try {
            isLoading.value = true
            error.value = null
            await caveApi.deleteUnit(unitId)
            const cave = caves.value.find(c => c.id === caveId)
            if (cave && cave.units) {
                cave.units = cave.units.filter(u => u.id !== unitId)
                if (selectedCave.value?.id === caveId) selectedCave.value = {...cave}
            }
            if (selectedUnit.value?.id === unitId) {
                selectedUnit.value = null
                selectedSpace.value = null
            }
            syncBottlePlacements()
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const addBottlePlacement = async (placementData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.addPlacement(placementData)
            const newPlacement = response.data
            syncBottlePlacements()
            return newPlacement
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const updateBottlePlacement = async (id, placementData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await caveApi.updatePlacement(id, placementData)
            const updated = response.data
            syncBottlePlacements()
            return updated
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const removeBottlePlacement = async (id) => {
        try {
            isLoading.value = true
            error.value = null
            await caveApi.deletePlacement(id)
            syncBottlePlacements()
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const getWines = async () => {
        try {
            isLoading.value = true
            error.value = null
            const response = await wineApi.getAll()
            wines.value = response.data
            return wines.value
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const searchWines = async (query) => {
        try {
            isLoading.value = true
            error.value = null
            return wines.value.filter(wine =>
                wine.name.toLowerCase().includes(query.toLowerCase()) ||
                (wine.region && wine.region.toLowerCase().includes(query.toLowerCase()))
            )
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }


    // UI state management
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
        // State
        caves,
        bottlePlacements,
        wines,
        isLoading,
        error,
        selectedCave,
        selectedUnit,
        selectedSpace,

        // Computed
        totalBottles,
        availableSpaces,
        occupiedSpaces,

        // Actions
        loadCaves,
        getCaveById,
        createCave,
        updateCave,
        deleteCave,
        addStorageUnit,
        updateStorageUnit,
        deleteStorageUnit,
        addBottlePlacement,
        updateBottlePlacement,
        removeBottlePlacement,
        getWines,
        searchWines,
        selectCave,
        selectUnit,
        selectSpace
    }
})