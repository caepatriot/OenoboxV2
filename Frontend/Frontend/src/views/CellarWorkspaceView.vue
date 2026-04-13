<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCaveStore } from '@/stores/cave.js'

const router = useRouter()
const route = useRoute()
const caveStore = useCaveStore()

const templateOptions = [
  { key: 'single-rack', label: 'Single rack', type: 'rack', rowCount: 2, totalSpaces: 20, defaultSpaceCapacity: 1 },
  { key: 'double-rack', label: 'Double rack', type: 'rack', rowCount: 4, totalSpaces: 48, defaultSpaceCapacity: 1 },
  { key: 'wine-fridge', label: 'Wine fridge', type: 'fridge', rowCount: 5, totalSpaces: 30, defaultSpaceCapacity: 1 },
  { key: 'single-shelf', label: 'Single shelf', type: 'shelf', rowCount: 1, totalSpaces: 12, defaultSpaceCapacity: 1 },
  { key: 'custom', label: 'Custom', type: 'rack', rowCount: 2, totalSpaces: 12, defaultSpaceCapacity: 1 },
]

const currentCaveId = ref(null)
const selectedUnitId = ref(null)
const selectedSlotIds = ref([])
const slotSearch = ref('')
const slotState = ref('all')
const slotGroupFilter = ref('')
const selectedWineId = ref(null)
const assignQuantity = ref(1)
const autoPlaceQuantity = ref(6)
const autoPlaceGroup = ref('')
const isImporting = ref(false)
const showWineDialog = ref(false)
const showDeleteCellarDialog = ref(false)
const alertMessage = ref('')
const alertType = ref('success')
const fileInput = ref(null)
const undoStack = ref([])
const redoStack = ref([])

const caveForm = ref(defaultCaveForm())
const unitForm = ref(defaultUnitForm())
const bulkSlotForm = ref({
  preferredWineGroup: '',
  sectionName: '',
  slotLabelPrefix: '',
  capacity: null,
  disabled: null,
  notes: '',
})
const wineForm = ref(defaultWineForm())

const currentCave = computed(() => {
  if (!caveStore.caves.length || currentCaveId.value == null) return null
  return caveStore.caves.find((cave) => String(cave.id) === String(currentCaveId.value)) || null
})

const currentUnits = computed(() => {
  return [...(currentCave.value?.units || [])].sort((a, b) => Number(a.displayOrder || 0) - Number(b.displayOrder || 0))
})

const selectedUnit = computed(() => {
  return currentUnits.value.find((unit) => String(unit.id) === String(selectedUnitId.value)) || null
})

const allSlots = computed(() => {
  if (!currentCave.value) return []
  return currentUnits.value.flatMap((unit) => {
    return (unit.spaces || []).map((space) => ({
      ...space,
      unitId: unit.id,
      unitName: unit.name,
      unitType: unit.type,
      unitPreferredWineGroup: unit.preferredWineGroup,
      displayOrder: unit.displayOrder,
      occupiedQuantity: Number(space.occupiedQuantity || 0),
      freeCapacity: Number(space.freeCapacity ?? space.capacity ?? 0),
      isOccupied: Number(space.occupiedQuantity || 0) > 0,
      isSelectable: !space.disabled && Number(space.freeCapacity ?? space.capacity ?? 0) > 0,
      placements: space.currentBottles || [],
    }))
  })
})

const visibleSlots = computed(() => {
  const query = slotSearch.value.trim().toLowerCase()
  const groupQuery = slotGroupFilter.value.trim().toLowerCase()

  return allSlots.value.filter((slot) => {
    const matchesUnit = !selectedUnitId.value || String(slot.unitId) === String(selectedUnitId.value)
    const matchesState =
      slotState.value === 'all'
        ? true
        : slotState.value === 'free'
          ? !slot.isOccupied
          : slot.isOccupied
    const matchesSearch =
      !query ||
      [
        slot.slotLabel,
        slot.sectionName,
        slot.unitName,
        slot.preferredWineGroup,
        ...(slot.placements || []).flatMap((placement) => [placement.wine?.name, placement.wine?.region, placement.wine?.wineTypeName]),
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(query)
    const matchesGroup =
      !groupQuery ||
      [slot.preferredWineGroup, slot.unitPreferredWineGroup, slot.sectionName]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(groupQuery)

    return matchesUnit && matchesState && matchesSearch && matchesGroup
  })
})

const selectedSlots = computed(() => {
  const ids = new Set(selectedSlotIds.value.map(String))
  return allSlots.value.filter((slot) => ids.has(String(slot.id)))
})

const freeSlots = computed(() => allSlots.value.filter((slot) => slot.isSelectable))

const selectedWine = computed(() => {
  return caveStore.wines.find((wine) => String(wine.id) === String(selectedWineId.value)) || null
})

const groupedWineCounts = computed(() => {
  const counts = new Map()
  allSlots.value.forEach((slot) => {
    slot.placements.forEach((placement) => {
      if (!placement.wine?.id) return
      const key = placement.wine.id
      counts.set(key, (counts.get(key) || 0) + Number(placement.quantity || 0))
    })
  })
  return counts
})

const lowStockItems = computed(() => {
  return caveStore.wines
    .map((wine) => ({
      ...wine,
      quantity: groupedWineCounts.value.get(wine.id) || 0,
    }))
    .filter((wine) => wine.quantity > 0 && wine.quantity <= 2)
    .sort((a, b) => a.quantity - b.quantity)
    .slice(0, 6)
})

const conflictItems = computed(() => {
  return allSlots.value.filter((slot) => slot.occupiedQuantity > Number(slot.capacity || 0) || (slot.disabled && slot.occupiedQuantity > 0))
})

const canUndo = computed(() => undoStack.value.length > 0 && !!currentCave.value)
const canRedo = computed(() => redoStack.value.length > 0 && !!currentCave.value)

watch(currentCave, (cave) => {
  if (!cave) return
  caveStore.selectCave(cave)
  caveForm.value = {
    id: cave.id,
    name: cave.name || '',
    description: cave.description || '',
    width: cave.dimensions?.width || null,
    height: cave.dimensions?.height || null,
    depth: cave.dimensions?.depth || null,
    temperature: cave.temperature ?? 12,
    humidity: cave.humidity ?? 70,
  }

  if (!selectedUnitId.value && cave.units?.length) {
    selectedUnitId.value = cave.units[0].id
  }

  const selected = cave.units?.find((unit) => String(unit.id) === String(selectedUnitId.value)) || null
  if (selected) {
    syncUnitForm(selected)
  } else {
    unitForm.value = defaultUnitForm()
  }
}, { immediate: true })

watch(selectedUnit, (unit) => {
  if (unit) {
    caveStore.selectUnit(unit)
    syncUnitForm(unit)
  }
})

onMounted(async () => {
  await Promise.allSettled([caveStore.loadCaves(), caveStore.getWines()])

  const routeCaveId = route.query.caveId
  if (routeCaveId && caveStore.caves.some((cave) => String(cave.id) === String(routeCaveId))) {
    currentCaveId.value = routeCaveId
  } else if (caveStore.caves.length) {
    currentCaveId.value = caveStore.caves[0].id
  }

  if (currentCaveId.value) {
    await caveStore.refreshCave(currentCaveId.value).catch(() => {})
    router.replace({ query: { caveId: currentCaveId.value } })
  }
})

function defaultCaveForm() {
  return {
    id: null,
    name: '',
    description: '',
    width: null,
    height: null,
    depth: null,
    temperature: 12,
    humidity: 70,
  }
}

function defaultUnitForm() {
  return {
    id: null,
    name: '',
    type: 'rack',
    templateKey: 'single-rack',
    rowCount: 2,
    columnCount: null,
    totalSpaces: 20,
    rowCapacitiesText: '10,10',
    defaultSpaceCapacity: 1,
    preferredWineGroup: '',
    notes: '',
    displayOrder: 0,
  }
}

function defaultWineForm() {
  return {
    name: '',
    wineTypeName: '',
    region: '',
    year: null,
    producer: '',
    appellation: '',
    country: '',
  }
}

function setAlert(message, type = 'success') {
  alertMessage.value = message
  alertType.value = type
}

function clearAlert() {
  alertMessage.value = ''
}

function parseRowCapacities(text) {
  return String(text || '')
    .split(',')
    .map((value) => Number(value.trim()))
    .filter((value) => Number.isFinite(value) && value > 0)
}

function createCavePayload() {
  return {
    name: caveForm.value.name,
    description: caveForm.value.description,
    temperature: caveForm.value.temperature,
    humidity: caveForm.value.humidity,
    dimensions: {
      width: caveForm.value.width,
      height: caveForm.value.height,
      depth: caveForm.value.depth,
    },
  }
}

function createUnitPayload() {
  const rowCapacities = parseRowCapacities(unitForm.value.rowCapacitiesText)
  const payload = {
    name: unitForm.value.name,
    type: unitForm.value.type,
    templateKey: unitForm.value.templateKey,
    layoutMode: 'formula',
    rowCount: unitForm.value.rowCount,
    columnCount: unitForm.value.columnCount,
    totalSpaces: unitForm.value.totalSpaces,
    defaultSpaceCapacity: unitForm.value.defaultSpaceCapacity,
    preferredWineGroup: unitForm.value.preferredWineGroup,
    notes: unitForm.value.notes,
    displayOrder: unitForm.value.displayOrder,
  }

  if (rowCapacities.length) {
    payload.rowCapacities = rowCapacities
    payload.rowCount = rowCapacities.length
    payload.totalSpaces = rowCapacities.reduce((sum, value) => sum + value, 0)
  }

  return payload
}

function syncUnitForm(unit) {
  unitForm.value = {
    id: unit.id,
    name: unit.name || '',
    type: unit.type || 'rack',
    templateKey: unit.templateKey || 'custom',
    rowCount: unit.rowCount || unit.rowCapacities?.length || 1,
    columnCount: unit.columnCount || null,
    totalSpaces: unit.totalSpaces || unit.spaces?.length || 0,
    rowCapacitiesText: (unit.rowCapacities || []).join(', '),
    defaultSpaceCapacity: unit.defaultSpaceCapacity || 1,
    preferredWineGroup: unit.preferredWineGroup || '',
    notes: unit.notes || '',
    displayOrder: unit.displayOrder || 0,
  }
}

function chooseTemplate(key) {
  const template = templateOptions.find((item) => item.key === key)
  if (!template) return
  unitForm.value.templateKey = template.key
  unitForm.value.type = template.type
  unitForm.value.rowCount = template.rowCount
  unitForm.value.totalSpaces = template.totalSpaces
  unitForm.value.defaultSpaceCapacity = template.defaultSpaceCapacity

  if (template.rowCount && template.totalSpaces) {
    const each = Math.floor(template.totalSpaces / template.rowCount)
    const remainder = template.totalSpaces % template.rowCount
    unitForm.value.rowCapacitiesText = Array.from({ length: template.rowCount }, (_, index) => each + (index < remainder ? 1 : 0)).join(', ')
  }
}

function selectCave(caveId) {
  currentCaveId.value = caveId
  selectedUnitId.value = null
  selectedSlotIds.value = []
  undoStack.value = []
  redoStack.value = []
  router.replace({ query: { caveId } })
  caveStore.refreshCave(caveId).catch(() => {})
}

function recordSnapshot() {
  if (!currentCave.value) return
  undoStack.value.push(JSON.parse(JSON.stringify(currentCave.value)))
  if (undoStack.value.length > 20) {
    undoStack.value.shift()
  }
  redoStack.value = []
}

async function undoLastAction() {
  if (!canUndo.value || !currentCave.value) return
  redoStack.value.push(JSON.parse(JSON.stringify(currentCave.value)))
  const snapshot = undoStack.value.pop()
  await caveStore.restoreCave(currentCave.value.id, snapshot)
  setAlert('Last action undone.')
}

async function redoLastAction() {
  if (!canRedo.value || !currentCave.value) return
  undoStack.value.push(JSON.parse(JSON.stringify(currentCave.value)))
  const snapshot = redoStack.value.pop()
  await caveStore.restoreCave(currentCave.value.id, snapshot)
  setAlert('Action restored.')
}

async function saveCave() {
  clearAlert()
  try {
    if (currentCave.value?.id) {
      recordSnapshot()
      await caveStore.updateCave(currentCave.value.id, createCavePayload())
      setAlert('Cellar details saved.')
      return
    }

    const created = await caveStore.createCave(createCavePayload())
    currentCaveId.value = created.id
    router.replace({ query: { caveId: created.id } })
    setAlert('Cellar created. You can now add storage units.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to save cellar.', 'error')
  }
}

async function saveUnit() {
  if (!currentCave.value) {
    setAlert('Create a cellar first.', 'error')
    return
  }
  clearAlert()
  try {
    if (unitForm.value.id) {
      recordSnapshot()
      const updated = await caveStore.updateStorageUnit(currentCave.value.id, unitForm.value.id, createUnitPayload())
      selectedUnitId.value = updated.id
      setAlert('Storage unit updated.')
    } else {
      recordSnapshot()
      const created = await caveStore.addStorageUnit(currentCave.value.id, createUnitPayload())
      selectedUnitId.value = created.id
      setAlert('Storage unit added.')
    }
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to save storage unit.', 'error')
  }
}

function editUnit(unit) {
  selectedUnitId.value = unit.id
  syncUnitForm(unit)
}

function createNewUnit() {
  unitForm.value = defaultUnitForm()
}

async function deleteUnit(unit) {
  if (!currentCave.value) return
  clearAlert()
  try {
    recordSnapshot()
    await caveStore.deleteStorageUnit(currentCave.value.id, unit.id)
    selectedUnitId.value = null
    unitForm.value = defaultUnitForm()
    setAlert('Storage unit deleted.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to delete storage unit.', 'error')
  }
}

function toggleSlotSelection(slotId) {
  const key = String(slotId)
  if (selectedSlotIds.value.some((id) => String(id) === key)) {
    selectedSlotIds.value = selectedSlotIds.value.filter((id) => String(id) !== key)
    return
  }
  selectedSlotIds.value = [...selectedSlotIds.value, slotId]
}

function toggleAllVisibleFreeSlots() {
  const freeVisibleIds = visibleSlots.value.filter((slot) => slot.isSelectable).map((slot) => slot.id)
  const allSelected = freeVisibleIds.length && freeVisibleIds.every((id) => selectedSlotIds.value.some((selectedId) => String(selectedId) === String(id)))
  selectedSlotIds.value = allSelected ? [] : freeVisibleIds
}

async function applyBulkSlotMetadata() {
  if (!currentCave.value || !selectedSlots.value.length) {
    setAlert('Select at least one slot.', 'error')
    return
  }
  clearAlert()
  try {
    recordSnapshot()
    await caveStore.bulkUpdateSpaces(currentCave.value.id, {
      spaceIds: selectedSlots.value.map((slot) => slot.id),
      preferredWineGroup: bulkSlotForm.value.preferredWineGroup || null,
      sectionName: bulkSlotForm.value.sectionName || null,
      slotLabelPrefix: bulkSlotForm.value.slotLabelPrefix || null,
      capacity: bulkSlotForm.value.capacity || null,
      disabled: bulkSlotForm.value.disabled,
      notes: bulkSlotForm.value.notes || null,
    })
    setAlert('Selected slots updated.')
    selectedSlotIds.value = []
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to update selected slots.', 'error')
  }
}

async function assignSelectedSlots() {
  if (!currentCave.value) {
    setAlert('Create or choose a cellar first.', 'error')
    return
  }
  if (!selectedWine.value) {
    setAlert('Choose a wine first.', 'error')
    return
  }
  const assignableSlots = selectedSlots.value.filter((slot) => slot.isSelectable)
  if (!assignableSlots.length) {
    setAlert('Select at least one free slot.', 'error')
    return
  }
  clearAlert()
  try {
    recordSnapshot()
    await caveStore.bulkAddPlacements(currentCave.value.id, {
      wineId: selectedWine.value.id,
      spaceIds: assignableSlots.map((slot) => slot.id),
      quantityPerSpace: assignQuantity.value,
    })
    selectedSlotIds.value = []
    setAlert('Bottle assignment saved.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to assign bottles.', 'error')
  }
}

async function autoPlaceWine() {
  if (!currentCave.value) {
    setAlert('Choose a cellar first.', 'error')
    return
  }
  if (!selectedWine.value) {
    setAlert('Choose a wine first.', 'error')
    return
  }
  clearAlert()
  try {
    recordSnapshot()
    await caveStore.autoPlace(currentCave.value.id, {
      wineId: selectedWine.value.id,
      quantity: autoPlaceQuantity.value,
      preferredGroup: autoPlaceGroup.value || null,
    })
    setAlert('Wine auto-placed into the best available slots.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to auto-place bottles.', 'error')
  }
}

async function removePlacement(placementId) {
  if (!currentCave.value) return
  clearAlert()
  try {
    recordSnapshot()
    await caveStore.removeBottlePlacement(currentCave.value.id, placementId)
    setAlert('Placement removed.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to remove placement.', 'error')
  }
}

async function exportCurrentCave() {
  if (!currentCave.value) return
  const data = await caveStore.exportCave(currentCave.value.id)
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = `${(currentCave.value.name || 'cellar').replace(/\s+/g, '-').toLowerCase()}.json`
  anchor.click()
  URL.revokeObjectURL(url)
}

function triggerImport() {
  fileInput.value?.click()
}

async function handleImport(event) {
  const [file] = event.target.files || []
  if (!file) return
  clearAlert()
  isImporting.value = true
  try {
    const text = await file.text()
    const payload = JSON.parse(text)
    const imported = await caveStore.importCave(payload)
    currentCaveId.value = imported.id
    selectedUnitId.value = imported.units?.[0]?.id || null
    router.replace({ query: { caveId: imported.id } })
    setAlert('Cellar imported successfully.')
  } catch (error) {
    setAlert(error.message || 'Unable to import file.', 'error')
  } finally {
    isImporting.value = false
    event.target.value = ''
  }
}

async function createWine() {
  clearAlert()
  try {
    const created = await caveStore.createWine({ ...wineForm.value })
    selectedWineId.value = created.id
    showWineDialog.value = false
    wineForm.value = defaultWineForm()
    setAlert('Wine created.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to create wine.', 'error')
  }
}

function startNewCellar() {
  currentCaveId.value = null
  selectedUnitId.value = null
  selectedSlotIds.value = []
  caveForm.value = defaultCaveForm()
  unitForm.value = defaultUnitForm()
  undoStack.value = []
  redoStack.value = []
  router.replace({ query: {} })
}

async function deleteCurrentCellar() {
  if (!currentCave.value) return
  clearAlert()
  try {
    await caveStore.deleteCave(currentCave.value.id)
    showDeleteCellarDialog.value = false
    const nextCave = caveStore.caves[0] || null
    currentCaveId.value = nextCave?.id || null
    router.replace({ query: nextCave ? { caveId: nextCave.id } : {} })
    setAlert('Cellar deleted.')
  } catch (error) {
    setAlert(error.response?.data || error.message || 'Unable to delete cellar.', 'error')
  }
}

function formatWineLabel(wine) {
  return [wine.name, wine.year, wine.region].filter(Boolean).join(' · ')
}

function isSlotSelected(slotId) {
  return selectedSlotIds.value.some((id) => String(id) === String(slotId))
}

function unitStats(unit) {
  const spaces = unit.spaces || []
  const occupied = spaces.filter((slot) => Number(slot.occupiedQuantity || 0) > 0).length
  return {
    total: spaces.length,
    occupied,
    free: Math.max(spaces.length - occupied, 0),
  }
}
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Cellar workspace</p>
        <h1 class="oeno-title">Simple physical storage management</h1>
        <p class="oeno-subtitle">
          Build your cellar with formulas instead of a heavy map: racks, fridges, shelves and rows of slots that stay practical to edit and fast to use.
        </p>
      </div>

      <div class="d-flex ga-2 flex-wrap align-center">
        <v-btn variant="tonal" prepend-icon="mdi-plus" @click="startNewCellar">New cellar</v-btn>
        <v-btn variant="tonal" prepend-icon="mdi-undo" :disabled="!canUndo" @click="undoLastAction">Undo</v-btn>
        <v-btn variant="tonal" prepend-icon="mdi-redo" :disabled="!canRedo" @click="redoLastAction">Redo</v-btn>
        <v-btn variant="tonal" prepend-icon="mdi-file-export-outline" :disabled="!currentCave" @click="exportCurrentCave">Export</v-btn>
        <v-btn variant="tonal" prepend-icon="mdi-file-import-outline" :loading="isImporting" @click="triggerImport">Import</v-btn>
        <input ref="fileInput" type="file" accept="application/json" class="d-none" @change="handleImport" />
      </div>
    </div>

    <v-alert v-if="alertMessage" :type="alertType" variant="tonal" rounded="xl">
      {{ alertMessage }}
    </v-alert>

    <div class="cellar-workspace-grid">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
            <div>
              <p class="oeno-card-label mb-1">Step 1</p>
              <h2 class="oeno-card-title">Cellar identity</h2>
            </div>
            <v-select
              v-if="caveStore.caves.length"
              :model-value="currentCaveId"
              :items="caveStore.caves"
              item-title="name"
              item-value="id"
              label="Current cellar"
              density="comfortable"
              hide-details
              variant="outlined"
              class="cellar-cave-select"
              @update:model-value="selectCave"
            />
          </div>

          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field v-model="caveForm.name" label="Cellar name" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field v-model="caveForm.description" label="Description" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="caveForm.temperature" type="number" label="Temperature °C" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="caveForm.humidity" type="number" label="Humidity %" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="4" md="2">
              <v-text-field v-model.number="caveForm.width" type="number" label="Width" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="4" md="2">
              <v-text-field v-model.number="caveForm.height" type="number" label="Height" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="4" md="2">
              <v-text-field v-model.number="caveForm.depth" type="number" label="Depth" variant="outlined" density="comfortable" />
            </v-col>
          </v-row>

          <div class="d-flex ga-3 flex-wrap mt-2">
            <v-btn class="oeno-btn oeno-btn--primary" @click="saveCave">{{ currentCave ? 'Save cellar' : 'Create cellar' }}</v-btn>
            <v-btn v-if="currentCave" variant="text" color="error" @click="showDeleteCellarDialog = true">Delete cellar</v-btn>
          </div>
        </v-card-text>
      </v-card>

      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
            <div>
              <p class="oeno-card-label mb-1">Step 2</p>
              <h2 class="oeno-card-title">Storage formula</h2>
            </div>
            <v-btn variant="tonal" prepend-icon="mdi-plus-box-multiple-outline" @click="createNewUnit">New unit</v-btn>
          </div>

          <div class="d-flex flex-wrap ga-2 mb-4">
            <v-chip
              v-for="template in templateOptions"
              :key="template.key"
              :variant="unitForm.templateKey === template.key ? 'flat' : 'tonal'"
              class="oeno-chip"
              @click="chooseTemplate(template.key)"
            >
              {{ template.label }}
            </v-chip>
          </div>

          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field v-model="unitForm.name" label="Storage unit name" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="unitForm.type"
                :items="['rack', 'fridge', 'shelf', 'box', 'cabinet']"
                label="Type"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="unitForm.rowCount" type="number" label="Rows" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="unitForm.totalSpaces" type="number" label="Total spaces" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="unitForm.defaultSpaceCapacity" type="number" label="Capacity / slot" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="6" md="3">
              <v-text-field v-model.number="unitForm.displayOrder" type="number" label="Display order" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="unitForm.rowCapacitiesText"
                label="Row capacities"
                hint="Example: 10,10 or 6,6,8"
                persistent-hint
                variant="outlined"
                density="comfortable"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field v-model="unitForm.preferredWineGroup" label="Preferred group" hint="red, white, Champagne, Burgundy..." persistent-hint variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field v-model="unitForm.notes" label="Notes" variant="outlined" density="comfortable" />
            </v-col>
          </v-row>

          <div class="d-flex ga-3 flex-wrap mt-2">
            <v-btn class="oeno-btn oeno-btn--primary" :disabled="!currentCave" @click="saveUnit">{{ unitForm.id ? 'Save unit' : 'Add unit' }}</v-btn>
            <div class="oeno-muted align-self-center">{{ unitForm.rowCapacitiesText || 'Use rows + total spaces for quick generation.' }}</div>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <v-row dense>
      <v-col cols="12" lg="4">
        <v-card class="oeno-card h-100" variant="flat">
          <v-card-text class="pa-6">
            <div class="d-flex align-center justify-space-between mb-4">
              <div>
                <p class="oeno-card-label mb-1">Overview</p>
                <h2 class="oeno-card-title">Storage units</h2>
              </div>
              <v-chip class="oeno-chip" variant="flat">{{ currentUnits.length }} units</v-chip>
            </div>

            <div v-if="currentUnits.length" class="cellar-unit-list">
              <div
                v-for="unit in currentUnits"
                :key="unit.id"
                class="cellar-unit-item"
                :class="{ 'is-active': String(selectedUnitId) === String(unit.id) }"
                @click="editUnit(unit)"
              >
                <div>
                  <div class="oeno-strong">{{ unit.name }}</div>
                  <div class="oeno-muted mt-1">{{ unit.type }} · {{ unitStats(unit).occupied }} occupied · {{ unitStats(unit).free }} free</div>
                </div>
                <div class="d-flex ga-2 align-center">
                  <v-chip size="small" variant="tonal">{{ unit.totalSpaces || unit.spaces?.length || 0 }} slots</v-chip>
                  <v-btn icon="mdi-delete-outline" size="x-small" variant="text" color="error" @click.stop="deleteUnit(unit)" />
                </div>
              </div>
            </div>

            <div v-else class="oeno-muted">No storage unit yet. Start with a rack, shelf or wine fridge.</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" lg="8">
        <v-card class="oeno-card h-100" variant="flat">
          <v-card-text class="pa-6">
            <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
              <div>
                <p class="oeno-card-label mb-1">Step 3</p>
                <h2 class="oeno-card-title">Slot explorer</h2>
              </div>
              <div class="d-flex ga-2 flex-wrap">
                <v-btn variant="tonal" prepend-icon="mdi-checkbox-multiple-marked-outline" @click="toggleAllVisibleFreeSlots">Toggle visible free slots</v-btn>
                <v-chip class="oeno-chip" variant="flat">{{ selectedSlots.length }} selected</v-chip>
              </div>
            </div>

            <v-row dense class="mb-3">
              <v-col cols="12" md="4">
                <v-text-field v-model="slotSearch" hide-details label="Search slots or wines" variant="outlined" density="comfortable" prepend-inner-icon="mdi-magnify" />
              </v-col>
              <v-col cols="6" md="3">
                <v-select v-model="slotState" hide-details :items="[{ title: 'All', value: 'all' }, { title: 'Free', value: 'free' }, { title: 'Occupied', value: 'occupied' }]" label="State" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="6" md="5">
                <v-text-field v-model="slotGroupFilter" hide-details label="Group or section filter" variant="outlined" density="comfortable" />
              </v-col>
            </v-row>

            <div v-if="visibleSlots.length" class="cellar-slot-list">
              <div v-for="slot in visibleSlots" :key="slot.id" class="cellar-slot-item" :class="{ 'is-selected': isSlotSelected(slot.id) }">
                <div class="d-flex align-start ga-3 flex-grow-1">
                  <v-checkbox-btn :model-value="isSlotSelected(slot.id)" :disabled="!slot.isSelectable" @click.stop="toggleSlotSelection(slot.id)" />
                  <div class="flex-grow-1">
                    <div class="d-flex align-center justify-space-between flex-wrap ga-2">
                      <div>
                        <div class="oeno-strong">{{ slot.slotLabel || `Slot ${slot.slotIndex}` }}</div>
                        <div class="oeno-muted mt-1">{{ slot.unitName }} · {{ slot.sectionName || 'No section' }}</div>
                      </div>
                      <div class="d-flex ga-2 flex-wrap align-center">
                        <v-chip size="small" :variant="slot.isOccupied ? 'flat' : 'tonal'">{{ slot.isOccupied ? 'Occupied' : 'Free' }}</v-chip>
                        <v-chip v-if="slot.preferredWineGroup" size="small" variant="tonal">{{ slot.preferredWineGroup }}</v-chip>
                        <v-chip v-if="slot.disabled" size="small" color="error" variant="tonal">Disabled</v-chip>
                      </div>
                    </div>

                    <div v-if="slot.placements.length" class="mt-3 cellar-placement-list">
                      <div v-for="placement in slot.placements" :key="placement.id" class="cellar-placement-chip">
                        <div>
                          <div class="oeno-strong">{{ placement.wine?.name || 'Unknown wine' }}</div>
                          <div class="oeno-muted mt-1">{{ placement.quantity }} bottle<span v-if="placement.quantity > 1">s</span> · {{ placement.wine?.region || placement.wine?.wineTypeName || 'Unspecified' }}</div>
                        </div>
                        <v-btn icon="mdi-delete-outline" size="x-small" variant="text" color="error" @click="removePlacement(placement.id)" />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="oeno-muted">No slots match these filters.</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row dense>
      <v-col cols="12" lg="6">
        <v-card class="oeno-card h-100" variant="flat">
          <v-card-text class="pa-6">
            <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
              <div>
                <p class="oeno-card-label mb-1">Step 4</p>
                <h2 class="oeno-card-title">Assign bottles</h2>
              </div>
              <v-btn variant="text" prepend-icon="mdi-plus" @click="showWineDialog = true">Quick wine</v-btn>
            </div>

            <v-select
              v-model="selectedWineId"
              :items="caveStore.wines"
              item-title="name"
              item-value="id"
              label="Wine"
              variant="outlined"
              density="comfortable"
            >
              <template #selection="{ item }">{{ formatWineLabel(item.raw) }}</template>
              <template #item="{ props, item }">
                <v-list-item v-bind="props" :title="item.raw.name" :subtitle="formatWineLabel(item.raw)" />
              </template>
            </v-select>

            <v-row dense class="mt-1">
              <v-col cols="12" md="4">
                <v-text-field v-model.number="assignQuantity" type="number" label="Qty / selected slot" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="8" class="d-flex align-center">
                <v-btn class="oeno-btn oeno-btn--primary w-100" :disabled="!selectedSlots.length" @click="assignSelectedSlots">
                  Assign to {{ selectedSlots.length }} selected slot<span v-if="selectedSlots.length > 1">s</span>
                </v-btn>
              </v-col>
            </v-row>

            <div class="cellar-divider my-5"></div>

            <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-3">
              <div>
                <div class="oeno-strong">Auto-place</div>
                <div class="oeno-muted mt-1">Let Oenobox pick the next free slots, prioritizing matching groups.</div>
              </div>
            </div>

            <v-row dense>
              <v-col cols="12" md="4">
                <v-text-field v-model.number="autoPlaceQuantity" type="number" label="Bottle count" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="8">
                <v-text-field v-model="autoPlaceGroup" label="Preferred group override" hint="Leave empty to use wine type or region automatically" persistent-hint variant="outlined" density="comfortable" />
              </v-col>
            </v-row>

            <v-btn class="oeno-btn oeno-btn--secondary mt-2" variant="flat" :disabled="!selectedWine" @click="autoPlaceWine">Auto-place selected wine</v-btn>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" lg="6">
        <v-card class="oeno-card h-100" variant="flat">
          <v-card-text class="pa-6">
            <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
              <div>
                <p class="oeno-card-label mb-1">Step 5</p>
                <h2 class="oeno-card-title">Section & group rules</h2>
              </div>
              <v-chip class="oeno-chip" variant="flat">{{ selectedSlots.length }} selected</v-chip>
            </div>

            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field v-model="bulkSlotForm.preferredWineGroup" label="Preferred group" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field v-model="bulkSlotForm.sectionName" label="Section name" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field v-model="bulkSlotForm.slotLabelPrefix" label="Slot label prefix" hint="Example: RED-A or FRIDGE-1" persistent-hint variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="3">
                <v-text-field v-model.number="bulkSlotForm.capacity" type="number" label="Capacity" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12" md="3">
                <v-select v-model="bulkSlotForm.disabled" :items="[{ title: 'No change', value: null }, { title: 'Enabled', value: false }, { title: 'Disabled', value: true }]" label="Disabled" variant="outlined" density="comfortable" />
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="bulkSlotForm.notes" label="Notes" variant="outlined" density="comfortable" />
              </v-col>
            </v-row>

            <v-btn class="oeno-btn oeno-btn--primary mt-2" :disabled="!selectedSlots.length" @click="applyBulkSlotMetadata">Apply to selected slots</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <div class="cellar-workspace-grid">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
            <div>
              <p class="oeno-card-label mb-1">Inventory insight</p>
              <h2 class="oeno-card-title">Low stock warnings</h2>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ lowStockItems.length }} wine<span v-if="lowStockItems.length > 1">s</span></v-chip>
          </div>

          <div v-if="lowStockItems.length" class="cellar-simple-list">
            <div v-for="wine in lowStockItems" :key="wine.id" class="cellar-simple-item">
              <div>
                <div class="oeno-strong">{{ wine.name }}</div>
                <div class="oeno-muted mt-1">{{ wine.region || wine.wineTypeName || 'Unspecified' }}</div>
              </div>
              <v-chip size="small" variant="tonal">{{ wine.quantity }} left</v-chip>
            </div>
          </div>
          <div v-else class="oeno-muted">No low stock warning right now.</div>
        </v-card-text>
      </v-card>

      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
            <div>
              <p class="oeno-card-label mb-1">Consistency</p>
              <h2 class="oeno-card-title">Conflicts</h2>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ conflictItems.length }} issue<span v-if="conflictItems.length > 1">s</span></v-chip>
          </div>

          <div v-if="conflictItems.length" class="cellar-simple-list">
            <div v-for="slot in conflictItems" :key="slot.id" class="cellar-simple-item">
              <div>
                <div class="oeno-strong">{{ slot.slotLabel }}</div>
                <div class="oeno-muted mt-1">{{ slot.unitName }} · {{ slot.occupiedQuantity }}/{{ slot.capacity }}</div>
              </div>
              <v-chip size="small" color="error" variant="tonal">Conflict</v-chip>
            </div>
          </div>
          <div v-else class="oeno-muted">No capacity or disabled-slot conflicts detected.</div>
        </v-card-text>
      </v-card>
    </div>

    <v-dialog v-model="showWineDialog" max-width="640">
      <v-card rounded="xl">
        <v-card-title class="px-6 pt-6">Quick wine creation</v-card-title>
        <v-card-text class="px-6 pb-2">
          <v-row dense>
            <v-col cols="12" md="6"><v-text-field v-model="wineForm.name" label="Name" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12" md="6"><v-text-field v-model="wineForm.wineTypeName" label="Type" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12" md="6"><v-text-field v-model="wineForm.region" label="Region" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12" md="6"><v-text-field v-model.number="wineForm.year" type="number" label="Vintage" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12" md="6"><v-text-field v-model="wineForm.producer" label="Producer" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12" md="6"><v-text-field v-model="wineForm.appellation" label="Appellation" variant="outlined" density="comfortable" /></v-col>
            <v-col cols="12"><v-text-field v-model="wineForm.country" label="Country" variant="outlined" density="comfortable" /></v-col>
          </v-row>
        </v-card-text>
        <v-card-actions class="px-6 pb-6">
          <v-spacer />
          <v-btn variant="text" @click="showWineDialog = false">Cancel</v-btn>
          <v-btn class="oeno-btn oeno-btn--primary" @click="createWine">Create wine</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showDeleteCellarDialog" max-width="520">
      <v-card rounded="xl">
        <v-card-title class="px-6 pt-6">Delete this cellar?</v-card-title>
        <v-card-text class="px-6 pb-2">This removes the cellar, its storage units and all bottle placements.</v-card-text>
        <v-card-actions class="px-6 pb-6">
          <v-spacer />
          <v-btn variant="text" @click="showDeleteCellarDialog = false">Cancel</v-btn>
          <v-btn color="error" variant="flat" @click="deleteCurrentCellar">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </section>
</template>

<style scoped>
.cellar-workspace-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.cellar-cave-select {
  min-width: 260px;
}

.cellar-unit-list,
.cellar-slot-list,
.cellar-simple-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cellar-unit-item,
.cellar-slot-item,
.cellar-simple-item,
.cellar-placement-chip {
  width: 100%;
  border: 1px solid rgba(171, 118, 96, 0.16);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.54);
  padding: 16px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.cellar-unit-item {
  cursor: pointer;
  text-align: left;
}

.cellar-unit-item.is-active,
.cellar-slot-item.is-selected {
  border-color: rgba(111, 37, 51, 0.28);
  box-shadow: 0 16px 36px rgba(65, 21, 32, 0.08);
}

.cellar-slot-item {
  align-items: stretch;
}

.cellar-placement-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cellar-placement-chip {
  padding: 10px 12px;
  border-radius: 16px;
}

.cellar-divider {
  height: 1px;
  background: rgba(111, 37, 51, 0.12);
}

@media (max-width: 1100px) {
  .cellar-workspace-grid {
    grid-template-columns: 1fr;
  }
}
</style>
