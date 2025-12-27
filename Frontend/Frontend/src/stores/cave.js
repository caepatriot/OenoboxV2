import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

// Mock data for caves, storage units, and bottle placements
const mockCaves = [
  {
    id: 1,
    name: 'Cave Principale',
    description: 'Cave principale du domaine avec température contrôlée',
    dimensions: { width: 400, height: 300, depth: 200 }, // cm
    temperature: 12,
    humidity: 70,
    units: [
      {
        id: 1,
        name: 'Rack Central',
        type: 'rack',
        position: { x: 50, y: 50 },
        rotation: 0,
        dimensions: { width: 120, height: 180, depth: 30 },
        orientation: 'vertical',
        capacity: 24,
        spaces: Array.from({ length: 24 }, (_, i) => ({
          id: i + 1,
          unitId: 1,
          position: { row: Math.floor(i / 6), column: i % 6 },
          coordinates: { x: (i % 6) * 20, y: Math.floor(i / 6) * 30 },
          capacity: 1,
          currentBottles: []
        }))
      },
      {
        id: 2,
        name: 'Étagère Murale',
        type: 'shelf',
        position: { x: 200, y: 30 },
        rotation: 0,
        dimensions: { width: 150, height: 40, depth: 25 },
        orientation: 'horizontal',
        capacity: 12,
        spaces: Array.from({ length: 12 }, (_, i) => ({
          id: i + 25,
          unitId: 2,
          position: { row: Math.floor(i / 6), column: i % 6 },
          coordinates: { x: (i % 6) * 25, y: Math.floor(i / 6) * 20 },
          capacity: 1,
          currentBottles: []
        }))
      }
    ]
  },
  {
    id: 2,
    name: 'Cave Secondaire',
    description: 'Petite cave de stockage supplémentaire',
    dimensions: { width: 200, height: 150, depth: 100 },
    temperature: 14,
    humidity: 65,
    units: [
      {
        id: 3,
        name: 'Armoire Vin',
        type: 'cabinet',
        position: { x: 20, y: 20 },
        rotation: 0,
        dimensions: { width: 80, height: 120, depth: 40 },
        orientation: 'vertical',
        capacity: 16,
        spaces: Array.from({ length: 16 }, (_, i) => ({
          id: i + 37,
          unitId: 3,
          position: { row: Math.floor(i / 4), column: i % 4 },
          coordinates: { x: (i % 4) * 20, y: Math.floor(i / 4) * 30 },
          capacity: 1,
          currentBottles: []
        }))
      }
    ]
  }
]

const mockBottlePlacements = [
  {
    id: 1,
    spaceId: 1,
    wine: {
      id: 1,
      name: 'Château Margaux 2015',
      type: 'red',
      cepage: ['Cabernet Sauvignon', 'Merlot'],
      region: 'Bordeaux',
      year: 2015
    },
    quantity: 1,
    dateAdded: '2024-01-15',
    preferredStorageDuration: 10,
    notes: 'Excellent millésime'
  },
  {
    id: 2,
    spaceId: 5,
    wine: {
      id: 2,
      name: 'Domaine de la Romanée-Conti 2018',
      type: 'red',
      cepage: ['Pinot Noir'],
      region: 'Bourgogne',
      year: 2018
    },
    quantity: 1,
    dateAdded: '2024-02-20',
    preferredStorageDuration: 15,
    notes: 'Acquisition spéciale'
  }
]

// Mock wines database (simplified version of existing wines)
const mockWines = [
  {
    id: 1,
    name: 'Château Margaux 2015',
    type: 'red',
    cepage: ['Cabernet Sauvignon', 'Merlot'],
    region: 'Bordeaux',
    year: 2015,
    aopIgpVdf: 'AOP',
    elevage: '18 mois en barriques',
    prixLancement: 450,
    prixActuel: 520
  },
  {
    id: 2,
    name: 'Domaine de la Romanée-Conti 2018',
    type: 'red',
    cepage: ['Pinot Noir'],
    region: 'Bourgogne',
    year: 2018,
    aopIgpVdf: 'AOP',
    elevage: '16 mois en fûts',
    prixLancement: 1200,
    prixActuel: 1500
  },
  {
    id: 3,
    name: 'Chablis Premier Cru 2020',
    type: 'white',
    cepage: ['Chardonnay'],
    region: 'Bourgogne',
    year: 2020,
    aopIgpVdf: 'AOP',
    elevage: '8 mois sur lies',
    prixLancement: 35,
    prixActuel: 42
  }
]

export const useCaveStore = defineStore('cave', () => {
  // Reactive state
  const caves = ref([...mockCaves])
  const bottlePlacements = ref([...mockBottlePlacements])
  const wines = ref([...mockWines])
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
      return !placement || placement.quantity < space.capacity
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
      // In real implementation, this would call the API
      // For now, just return mock data
      return caves.value
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const getCaveById = async (id) => {
    try {
      isLoading.value = true
      error.value = null
      const cave = caves.value.find(c => c.id === id)
      if (!cave) throw new Error('Cave not found')
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
      const newCave = {
        id: Math.max(...caves.value.map(c => c.id)) + 1,
        ...caveData,
        units: []
      }
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
      const index = caves.value.findIndex(c => c.id === id)
      if (index === -1) throw new Error('Cave not found')

      caves.value[index] = { ...caves.value[index], ...caveData }
      return caves.value[index]
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
      const index = caves.value.findIndex(c => c.id === id)
      if (index === -1) throw new Error('Cave not found')

      caves.value.splice(index, 1)
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
      const cave = caves.value.find(c => c.id === caveId)
      if (!cave) throw new Error('Cave not found')

      const newUnit = {
        id: Math.max(...caves.value.flatMap(c => c.units.map(u => u.id))) + 1,
        ...unitData,
        rotation: unitData.rotation || 0,
        spaces: [] // Will be generated based on dimensions
      }

      // Generate storage spaces based on unit type and dimensions
      newUnit.spaces = generateStorageSpaces(newUnit)

      cave.units.push(newUnit)
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
      const cave = caves.value.find(c => c.id === caveId)
      if (!cave) throw new Error('Cave not found')

      const unitIndex = cave.units.findIndex(u => u.id === unitId)
      if (unitIndex === -1) throw new Error('Storage unit not found')

      cave.units[unitIndex] = { ...cave.units[unitIndex], ...unitData }
      return cave.units[unitIndex]
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
      const cave = caves.value.find(c => c.id === caveId)
      if (!cave) throw new Error('Cave not found')

      const unitIndex = cave.units.findIndex(u => u.id === unitId)
      if (unitIndex === -1) throw new Error('Storage unit not found')

      cave.units.splice(unitIndex, 1)
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

      // Check if wine exists, if not add it
      let wine = wines.value.find(w => w.id === placementData.wine.id)
      if (!wine) {
        wine = {
          id: Math.max(...wines.value.map(w => w.id)) + 1,
          ...placementData.wine
        }
        wines.value.push(wine)
      }

      const newPlacement = {
        id: Math.max(...bottlePlacements.value.map(p => p.id)) + 1,
        ...placementData,
        wine: wine,
        dateAdded: new Date().toISOString().split('T')[0]
      }

      bottlePlacements.value.push(newPlacement)
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
      const index = bottlePlacements.value.findIndex(p => p.id === id)
      if (index === -1) throw new Error('Bottle placement not found')

      bottlePlacements.value[index] = { ...bottlePlacements.value[index], ...placementData }
      return bottlePlacements.value[index]
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
      const index = bottlePlacements.value.findIndex(p => p.id === id)
      if (index === -1) throw new Error('Bottle placement not found')

      bottlePlacements.value.splice(index, 1)
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
      // In real implementation, this would call the wine API
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
      const results = wines.value.filter(wine =>
        wine.name.toLowerCase().includes(query.toLowerCase()) ||
        wine.region.toLowerCase().includes(query.toLowerCase()) ||
        wine.cepage.some(c => c.toLowerCase().includes(query.toLowerCase()))
      )
      return results
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Helper function to generate storage spaces based on unit dimensions
  const generateStorageSpaces = (unit) => {
    const spaces = []
    let spaceId = Math.max(...caves.value.flatMap(c => c.units.flatMap(u => u.spaces?.map(s => s.id) || []))) + 1

    // Simple grid generation based on unit type
    const rows = unit.type === 'shelf' ? 2 : 6
    const cols = unit.type === 'shelf' ? 6 : 4

    for (let row = 0; row < rows; row++) {
      for (let col = 0; col < cols; col++) {
        spaces.push({
          id: spaceId++,
          unitId: unit.id,
          position: { row, column: col },
          coordinates: {
            x: col * (unit.dimensions.width / cols),
            y: row * (unit.dimensions.height / rows)
          },
          capacity: 1,
          currentBottles: []
        })
      }
    }

    return spaces
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