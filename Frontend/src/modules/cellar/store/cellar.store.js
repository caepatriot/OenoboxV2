import { defineStore } from 'pinia'
import { cellarApi } from '@/core/api/http.js'

export const useCellarStore = defineStore('cellar', () => {
    // Reactive state
    const caves = ref([])
    const currentCave = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Actions
    const fetchCaves = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.getAll()
            caves.value = response.data
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const fetchCave = async (id) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.getById(id)
            currentCave.value = response.data
            return response.data
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const createCave = async (caveData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.create(caveData)
            const newCave = response.data
            caves.value.push(newCave)
            return newCave
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const updateCave = async (id, caveData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.update(id, caveData)
            const updated = response.data
            const index = caves.value.findIndex(c => c.id === id)
            if (index !== -1) {
                caves.value[index] = updated
            }
            if (currentCave.value?.id === id) {
                currentCave.value = updated
            }
            return updated
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const deleteCave = async (id) => {
        loading.value = true
        error.value = null
        try {
            await cellarApi.delete(id)
            caves.value = caves.value.filter(c => c.id !== id)
            if (currentCave.value?.id === id) {
                currentCave.value = null
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const createUnit = async (caveId, unitData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.createUnit(caveId, unitData)
            const newUnit = response.data
            const cave = caves.value.find(c => c.id === caveId)
            if (cave) {
                cave.units.push(newUnit)
            }
            if (currentCave.value?.id === caveId) {
                currentCave.value.units.push(newUnit)
            }
            return newUnit
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const updateUnit = async (unitId, unitData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.updateUnit(unitId, unitData)
            const updatedUnit = response.data
            // Update in caves
            for (const cave of caves.value) {
                const unitIndex = cave.units.findIndex(u => u.id === unitId)
                if (unitIndex !== -1) {
                    cave.units[unitIndex] = updatedUnit
                    break
                }
            }
            if (currentCave.value) {
                const unitIndex = currentCave.value.units.findIndex(u => u.id === unitId)
                if (unitIndex !== -1) {
                    currentCave.value.units[unitIndex] = updatedUnit
                }
            }
            return updatedUnit
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const deleteUnit = async (unitId) => {
        loading.value = true
        error.value = null
        try {
            await cellarApi.deleteUnit(unitId)
            const cave = caves.value.find(c => c.units.some(u => u.id === unitId))
            if (cave) {
                cave.units = cave.units.filter(u => u.id !== unitId)
            }
            if (currentCave.value) {
                currentCave.value.units = currentCave.value.units.filter(u => u.id !== unitId)
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const addPlacement = async (placementData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.addPlacement(placementData)
            const newPlacement = response.data
            // Update spaces
            syncBottlePlacements()
            return newPlacement
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const updatePlacement = async (id, placementData) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.updatePlacement(id, placementData)
            const updated = response.data
            syncBottlePlacements()
            return updated
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const deletePlacement = async (id) => {
        loading.value = true
        error.value = null
        try {
            await cellarApi.deletePlacement(id)
            syncBottlePlacements()
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const assignGroupToSpace = async (spaceId, groupName) => {
        loading.value = true
        error.value = null
        try {
            await cellarApi.assignGroup(spaceId, groupName)
            // Update local state
            for (const cave of caves.value) {
                for (const unit of cave.units) {
                    const space = unit.spaces.find(s => s.id === spaceId)
                    if (space) {
                        space.groupName = groupName
                        break
                    }
                }
            }
            if (currentCave.value) {
                for (const unit of currentCave.value.units) {
                    const space = unit.spaces.find(s => s.id === spaceId)
                    if (space) {
                        space.groupName = groupName
                        break
                    }
                }
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const suggestSpacesForWine = async (caveId, wineId) => {
        loading.value = true
        error.value = null
        try {
            const response = await cellarApi.suggestSpaces(caveId, wineId)
            return response.data
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const syncBottlePlacements = () => {
        // Refresh current cave if needed
        if (currentCave.value) {
            fetchCave(currentCave.value.id)
        }
    }

    return {
        // State
        caves: readonly(caves),
        currentCave: readonly(currentCave),
        loading: readonly(loading),
        error: readonly(error),

        // Actions
        fetchCaves,
        fetchCave,
        createCave,
        updateCave,
        deleteCave,
        createUnit,
        updateUnit,
        deleteUnit,
        addPlacement,
        updatePlacement,
        deletePlacement,
        assignGroupToSpace,
        suggestSpacesForWine
    }
})