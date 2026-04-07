<template>
  <v-card class="virtual-cellar-workspace" rounded="xl" elevation="2">
    <v-toolbar color="transparent" density="comfortable">
      <v-toolbar-title class="text-h6 font-weight-bold">
        Virtual Cellar Workspace
      </v-toolbar-title>

      <v-spacer />

      <v-btn-toggle
        v-model="mode"
        mandatory
        divided
        rounded="pill"
        density="comfortable"
      >
        <v-btn value="stock">Stock</v-btn>
        <v-btn value="layout">Layout</v-btn>
      </v-btn-toggle>
    </v-toolbar>

    <v-divider />

    <div class="workspace-grid">
      <aside class="sidebar-left pa-4">
        <v-text-field
          v-model="search"
          label="Search a wine"
          variant="outlined"
          density="comfortable"
          prepend-inner-icon="mdi-magnify"
          hide-details
          clearable
          class="mb-4"
        />

        <div class="d-flex flex-wrap ga-2 mb-4">
          <v-chip
            v-for="filter in filters"
            :key="filter"
            :color="selectedFilter === filter ? 'primary' : undefined"
            :variant="selectedFilter === filter ? 'flat' : 'outlined'"
            size="small"
            @click="selectedFilter = filter"
          >
            {{ filter }}
          </v-chip>
        </div>

        <v-card variant="tonal" rounded="xl" class="mb-4">
          <v-card-text class="d-flex flex-wrap ga-3">
            <div>
              <div class="text-caption text-medium-emphasis">Wines</div>
              <div class="text-h6 font-weight-bold">{{ filteredWines.length }}</div>
            </div>
            <div>
              <div class="text-caption text-medium-emphasis">Bottles</div>
              <div class="text-h6 font-weight-bold">{{ totalStockBottles }}</div>
            </div>
            <div>
              <div class="text-caption text-medium-emphasis">Unassigned</div>
              <div class="text-h6 font-weight-bold">{{ totalUnassignedBottles }}</div>
            </div>
            <div>
              <div class="text-caption text-medium-emphasis">Free slots</div>
              <div class="text-h6 font-weight-bold">{{ totalFreeSlots }}</div>
            </div>
          </v-card-text>
        </v-card>

        <v-card rounded="xl" variant="outlined" class="mb-4">
          <v-card-title class="text-subtitle-1">Unassigned Bottles</v-card-title>
          <v-card-text>
            <template v-if="unassignedWines.length">
              <div
                v-for="wine in unassignedWines"
                :key="`unassigned-${wine.id}`"
                class="d-flex align-center justify-space-between mb-2"
              >
                <div>
                  <div class="font-weight-medium">{{ wine.name }}</div>
                  <div class="text-caption text-medium-emphasis">{{ wine.region }} · {{ wine.vintage }}</div>
                </div>
                <v-chip size="small" color="primary" variant="tonal">
                  {{ wine.unassigned }} left
                </v-chip>
              </div>
            </template>
            <template v-else>
              <div class="text-body-2 text-medium-emphasis">All bottles are currently assigned to racks.</div>
            </template>
          </v-card-text>
        </v-card>

        <div class="wine-list">
          <v-card
            v-for="wine in filteredWines"
            :key="wine.id"
            rounded="xl"
            class="mb-3 wine-card"
            :variant="selectedWine?.id === wine.id ? 'flat' : 'outlined'"
            :color="selectedWine?.id === wine.id ? 'primary' : undefined"
            @click="selectWine(wine)"
          >
            <v-card-text>
              <div class="d-flex align-start justify-space-between ga-3">
                <div>
                  <div class="font-weight-bold">{{ wine.name }}</div>
                  <div class="text-caption opacity-80">{{ wine.region }} · {{ wine.vintage }}</div>
                </div>
                <v-chip size="small" :variant="selectedWine?.id === wine.id ? 'flat' : 'tonal'">
                  {{ wine.color }}
                </v-chip>
              </div>

              <div class="d-flex align-center justify-space-between mt-3 text-body-2">
                <span>{{ countWineBottles(wine.id) }} bottle<span v-if="countWineBottles(wine.id) > 1">s</span> placed</span>
                <span>{{ countUnassignedBottles(wine.id) }} unassigned</span>
              </div>
              <div class="d-flex align-center justify-space-between mt-1 text-body-2">
                <span>{{ countWineUnits(wine.id) }} rack<span v-if="countWineUnits(wine.id) > 1">s</span></span>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </aside>

      <main class="workspace-center pa-4">
        <div class="d-flex align-center justify-space-between mb-4 flex-wrap ga-3">
          <div>
            <div class="text-caption text-medium-emphasis">Current cave</div>
            <div class="text-h6 font-weight-bold">{{ currentCave.name }}</div>
          </div>

          <div class="d-flex ga-2 flex-wrap">
            <v-btn variant="tonal" prepend-icon="mdi-plus-box" @click="addRackRectangle">
              Add rack rectangle
            </v-btn>
            <v-btn variant="tonal" prepend-icon="mdi-fit-to-screen" @click="fitView">
              Clear selection
            </v-btn>
          </div>
        </div>

        <v-row dense>
          <v-col cols="12" md="8">
            <v-card rounded="xl" class="map-card" variant="outlined">
              <v-card-title class="d-flex align-center justify-space-between flex-wrap ga-2">
                <span>Cellar map (top view)</span>
                <div class="d-flex ga-2 flex-wrap">
                  <v-chip size="small" variant="tonal">Occupied {{ totalPlacedBottles }}</v-chip>
                  <v-chip size="small" variant="tonal">Free {{ totalFreeSlots }}</v-chip>
                </div>
              </v-card-title>

              <v-card-text>
                <div ref="mapRef" class="cellar-map" @pointerdown="onMapPointerDown">
                  <div
                    v-for="unit in currentCave.units"
                    :key="unit.id"
                    class="storage-unit"
                    :class="[
                      `unit-type--${unit.unitType}`,
                      {
                        'is-selected': selectedUnit?.id === unit.id,
                        'is-layout': mode === 'layout',
                      }
                    ]"
                    :style="unitStyle(unit)"
                    @click.stop="selectUnit(unit)"
                  >
                    <div
                      class="storage-unit__body"
                      @pointerdown.stop="startMove($event, unit)"
                    >
                      <div class="storage-unit__header">
                        <span class="font-weight-bold">{{ unit.name }}</span>
                        <v-chip size="x-small" variant="flat">{{ occupancyPercent(unit) }}%</v-chip>
                      </div>

                      <div class="storage-unit__meta">
                        {{ unit.frontRows }} rows · {{ rackTotalCapacity(unit) }} slots · {{ unit.rotation }}°
                      </div>

                      <div class="storage-unit__preview">
                        <span>{{ unit.frontRows }} × {{ unit.maxRowCapacity }}</span>
                      </div>
                    </div>

                    <button
                      v-if="mode === 'layout' && selectedUnit?.id === unit.id"
                      type="button"
                      class="unit-control unit-control--rotate"
                      @click.stop="rotateUnit(unit)"
                    >
                      ↻
                    </button>

                    <button
                      v-if="mode === 'layout' && selectedUnit?.id === unit.id"
                      type="button"
                      class="unit-control unit-control--resize"
                      @pointerdown.stop="startResize($event, unit)"
                    />
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </v-col>

          <v-col cols="12" md="4">
            <v-card rounded="xl" variant="outlined" class="h-100">
              <v-card-title>Rack locations for selected wine</v-card-title>
              <v-card-text>
                <template v-if="selectedWine && selectedWineLocations.length">
                  <v-timeline density="compact" align="start" side="end" truncate-line="both">
                    <v-timeline-item
                      v-for="location in selectedWineLocations"
                      :key="location.unitId"
                      dot-color="primary"
                      size="small"
                    >
                      <div class="font-weight-bold">{{ location.unitName }}</div>
                      <div class="text-body-2">{{ location.count }} bottles</div>
                    </v-timeline-item>
                  </v-timeline>
                </template>

                <template v-else>
                  <div class="text-body-2 text-medium-emphasis">
                    Select a wine and click an empty front-view slot to place a bottle.
                  </div>
                </template>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </main>

      <aside class="sidebar-right pa-4">
        <v-card rounded="xl" variant="outlined" class="mb-4">
          <v-card-title class="text-subtitle-1">Inspector</v-card-title>
          <v-card-text>
            <template v-if="selectedWine">
              <div class="text-overline">Selected wine</div>
              <div class="text-h6 font-weight-bold mb-1">{{ selectedWine.name }}</div>
              <div class="text-body-2 text-medium-emphasis mb-4">
                {{ selectedWine.region }} · {{ selectedWine.vintage }}
              </div>

              <div class="d-flex flex-wrap ga-2 mb-4">
                <v-chip size="small" variant="tonal">{{ selectedWine.color }}</v-chip>
                <v-chip size="small" variant="tonal">{{ countWineBottles(selectedWine.id) }} placed</v-chip>
                <v-chip size="small" variant="tonal">{{ countUnassignedBottles(selectedWine.id) }} unassigned</v-chip>
              </div>
            </template>

            <template v-if="selectedUnit">
              <div class="text-overline">Selected rack</div>
              <div class="text-h6 font-weight-bold mb-1">{{ selectedUnit.name }}</div>
              <div class="text-body-2 text-medium-emphasis mb-4">
                Top view: {{ selectedUnit.w.toFixed(1) }}% × {{ selectedUnit.h.toFixed(1) }}% · Rot {{ selectedUnit.rotation }}°
              </div>

              <v-row dense class="mb-2">
                <v-col cols="6">
                  <v-card rounded="lg" variant="tonal">
                    <v-card-text>
                      <div class="text-caption text-medium-emphasis">Capacity</div>
                      <div class="text-h6 font-weight-bold">{{ rackTotalCapacity(selectedUnit) }}</div>
                    </v-card-text>
                  </v-card>
                </v-col>
                <v-col cols="6">
                  <v-card rounded="lg" variant="tonal">
                    <v-card-text>
                      <div class="text-caption text-medium-emphasis">Occupied</div>
                      <div class="text-h6 font-weight-bold">{{ rackOccupied(selectedUnit) }}</div>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>

              <div v-if="mode === 'layout'" class="mt-3">
                <v-btn block variant="outlined" class="mb-2" @click="rotateUnit(selectedUnit)">Rotate 90°</v-btn>
                <v-btn
                  block
                  color="error"
                  variant="tonal"
                  :disabled="rackOccupied(selectedUnit) > 0"
                  @click="deleteSelectedUnit"
                >
                  Delete rack
                </v-btn>
              </div>
              <div v-if="mode === 'layout' && rackOccupied(selectedUnit) > 0" class="text-caption text-medium-emphasis mt-2">
                Rack can only be deleted when empty.
              </div>
            </template>

            <template v-if="actionMessage">
              <v-alert class="mt-4" density="comfortable" variant="tonal">{{ actionMessage }}</v-alert>
            </template>
          </v-card-text>
        </v-card>

        <v-card rounded="xl" variant="outlined">
          <v-card-title class="text-subtitle-1">Rack Front View</v-card-title>
          <v-card-text>
            <template v-if="selectedUnit">
              <v-row dense>
                <v-col cols="6">
                  <v-text-field
                    v-model.number="frontEditor.rows"
                    type="number"
                    min="1"
                    max="12"
                    density="comfortable"
                    label="Rows"
                    variant="outlined"
                  />
                </v-col>
                <v-col cols="6" class="d-flex align-center">
                  <v-btn block variant="outlined" @click="applyFrontRows">Apply rows</v-btn>
                </v-col>
              </v-row>

              <div v-for="(cap, index) in frontEditor.rowCapacities" :key="`row-${index}`" class="mb-2 d-flex ga-2 align-center">
                <div class="text-caption row-label">Row {{ index + 1 }}</div>
                <v-text-field
                  v-model.number="frontEditor.rowCapacities[index]"
                  type="number"
                  min="1"
                  max="24"
                  density="compact"
                  variant="outlined"
                  hide-details
                />
              </div>

              <v-btn block variant="outlined" class="mb-4" @click="applyRowCapacities">
                Apply row capacities
              </v-btn>

              <div class="front-rack-grid">
                <div v-for="(row, rowIndex) in selectedUnit.frontSlots" :key="`front-row-${rowIndex}`" class="front-rack-row">
                  <div class="front-rack-row__label">R{{ rowIndex + 1 }}</div>
                  <button
                    v-for="(slot, colIndex) in row"
                    :key="`slot-${rowIndex}-${colIndex}`"
                    type="button"
                    class="front-slot"
                    :class="frontSlotClass(slot)"
                    @click="onFrontSlotClick(rowIndex, colIndex)"
                  >
                    {{ colIndex + 1 }}
                  </button>
                </div>
              </div>

              <v-alert
                v-if="selectedFrontSlot"
                class="mt-4"
                variant="tonal"
                density="comfortable"
              >
                <div class="font-weight-bold">Row {{ selectedFrontSlot.row + 1 }} · Slot {{ selectedFrontSlot.col + 1 }}</div>
                <div class="text-body-2">{{ selectedFrontSlot.slot?.wineName || 'Empty slot' }}</div>
                <v-btn
                  v-if="selectedFrontSlot.slot"
                  size="small"
                  class="mt-2"
                  variant="outlined"
                  @click="removeBottleFromFrontSlot"
                >
                  Remove bottle
                </v-btn>
              </v-alert>
            </template>

            <template v-else>
              <div class="text-body-2 text-medium-emphasis">
                Click a rack in top view to edit front rows and place bottles.
              </div>
            </template>
          </v-card-text>
        </v-card>
      </aside>
    </div>
  </v-card>
</template>

<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'

const mode = ref('stock')
const search = ref('')
const selectedFilter = ref('All')
const selectedWine = ref(null)
const selectedUnit = ref(null)
const selectedFrontSlot = ref(null)
const actionMessage = ref('')
const mapRef = ref(null)

const frontEditor = ref({
  rows: 1,
  rowCapacities: [1],
})

const filters = ['All', 'Red', 'White', 'Sparkling']

const UNIT_TYPES = {
  RACK: 'RACK',
}

const currentCave = ref({
  id: 1,
  name: 'Main Cellar',
  units: [
    makeUnit({ id: 1, name: 'Rack A1', x: 6, y: 8, w: 22, h: 14, rotation: 0, rowCapacities: [6, 6, 6] }),
    makeUnit({ id: 2, name: 'Rack A2', x: 32, y: 8, w: 18, h: 14, rotation: 0, rowCapacities: [5, 5, 5] }),
    makeUnit({ id: 3, name: 'Rack B1', x: 58, y: 10, w: 16, h: 18, rotation: 0, rowCapacities: [4, 4, 4, 4] }),
  ],
})

const wines = ref([
  { id: 101, name: 'Chablis Premier Cru', region: 'Bourgogne', vintage: 2020, color: 'White', stock: 12 },
  { id: 102, name: 'Margaux Chateau Example', region: 'Bordeaux', vintage: 2015, color: 'Red', stock: 10 },
  { id: 103, name: 'Cremant Brut Nature', region: 'Luxembourg', vintage: 2022, color: 'Sparkling', stock: 10 },
  { id: 104, name: 'Rioja Reserva', region: 'Spain', vintage: 2018, color: 'Red', stock: 12 },
])

seedOccupancy()

const filteredWines = computed(() => {
  const q = search.value.trim().toLowerCase()
  return wines.value.filter((wine) => {
    const matchesFilter = selectedFilter.value === 'All' || wine.color === selectedFilter.value
    const matchesSearch = !q || [wine.name, wine.region, String(wine.vintage)].join(' ').toLowerCase().includes(q)
    return matchesFilter && matchesSearch
  })
})

const totalFreeSlots = computed(() => currentCave.value.units.reduce((sum, unit) => sum + (rackTotalCapacity(unit) - rackOccupied(unit)), 0))
const totalPlacedBottles = computed(() => currentCave.value.units.reduce((sum, unit) => sum + rackOccupied(unit), 0))
const totalStockBottles = computed(() => wines.value.reduce((sum, wine) => sum + wine.stock, 0))
const totalUnassignedBottles = computed(() => wines.value.reduce((sum, wine) => sum + countUnassignedBottles(wine.id), 0))
const unassignedWines = computed(() => {
  return wines.value
    .map((wine) => ({
      ...wine,
      unassigned: countUnassignedBottles(wine.id),
    }))
    .filter((wine) => wine.unassigned > 0)
    .sort((a, b) => b.unassigned - a.unassigned)
})

const selectedWineLocations = computed(() => {
  if (!selectedWine.value) return []
  return currentCave.value.units
    .map((unit) => ({
      unitId: unit.id,
      unitName: unit.name,
      count: countWineInUnit(selectedWine.value.id, unit),
    }))
    .filter((location) => location.count > 0)
})

const interaction = ref({
  type: null,
  unitId: null,
  startClientX: 0,
  startClientY: 0,
  startX: 0,
  startY: 0,
  startW: 0,
  startH: 0,
  startRotation: 0,
  moved: false,
})

watch(selectedUnit, (unit) => {
  if (!unit) return
  frontEditor.value = {
    rows: unit.frontRows,
    rowCapacities: [...unit.rowCapacities],
  }
  selectedFrontSlot.value = null
})

watch(mode, () => {
  actionMessage.value = ''
})

function makeUnit({ id, name, x, y, w, h, rotation, rowCapacities }) {
  const normalized = rowCapacities.map((value) => clamp(Math.round(value), 1, 24))
  return {
    id,
    name,
    x,
    y,
    w,
    h,
    rotation: normalizeRotation(rotation),
    unitType: UNIT_TYPES.RACK,
    frontRows: normalized.length,
    rowCapacities: normalized,
    frontSlots: createFrontSlots(normalized),
    maxRowCapacity: Math.max(...normalized),
  }
}

function createFrontSlots(rowCapacities, previous = []) {
  return rowCapacities.map((capacity, rowIndex) => {
    const oldRow = previous[rowIndex] || []
    const nextRow = []
    for (let col = 0; col < capacity; col += 1) {
      nextRow.push(oldRow[col] || null)
    }
    return nextRow
  })
}

function seedOccupancy() {
  const unitA = currentCave.value.units[0]
  const unitB = currentCave.value.units[1]
  const unitC = currentCave.value.units[2]
  placeWineInNextSlots(101, unitA, 4)
  placeWineInNextSlots(102, unitA, 3)
  placeWineInNextSlots(103, unitB, 5)
  placeWineInNextSlots(104, unitC, 6)
}

function placeWineInNextSlots(wineId, unit, count) {
  const wine = wines.value.find((entry) => entry.id === wineId)
  if (!wine || !unit) return
  let left = count

  for (let row = 0; row < unit.frontSlots.length; row += 1) {
    for (let col = 0; col < unit.frontSlots[row].length; col += 1) {
      if (left === 0) return
      if (!unit.frontSlots[row][col]) {
        unit.frontSlots[row][col] = {
          wineId: wine.id,
          wineName: wine.name,
          color: wine.color,
        }
        left -= 1
      }
    }
  }
}

function rackTotalCapacity(unit) {
  return unit.rowCapacities.reduce((sum, size) => sum + size, 0)
}

function rackOccupied(unit) {
  return unit.frontSlots.reduce((sum, row) => sum + row.filter(Boolean).length, 0)
}

function occupancyPercent(unit) {
  const total = rackTotalCapacity(unit)
  if (total === 0) return 0
  return Math.round((rackOccupied(unit) / total) * 100)
}

function selectWine(wine) {
  selectedWine.value = wine
  actionMessage.value = `Selected wine: ${wine.name}`
}

function selectUnit(unit) {
  if (interaction.value.moved) return
  selectedUnit.value = unit
}

function fitView() {
  selectedUnit.value = null
  selectedFrontSlot.value = null
  actionMessage.value = ''
}

function addRackRectangle() {
  const nextId = currentCave.value.units.reduce((max, unit) => Math.max(max, unit.id), 0) + 1
  const newUnit = makeUnit({
    id: nextId,
    name: `Rack ${nextId}`,
    x: 4,
    y: 4,
    w: 14,
    h: 10,
    rotation: 0,
    rowCapacities: [5, 5, 5],
  })

  currentCave.value.units.push(newUnit)
  resolveCollisionAndBounds(newUnit)
  selectedUnit.value = newUnit
  actionMessage.value = 'Rack created. Drag it, resize it, and rotate it in Layout mode.'
}

function countWineInUnit(wineId, unit) {
  return unit.frontSlots.reduce((sum, row) => {
    return sum + row.filter((slot) => slot?.wineId === wineId).length
  }, 0)
}

function countWineBottles(wineId) {
  return currentCave.value.units.reduce((sum, unit) => sum + countWineInUnit(wineId, unit), 0)
}

function countWineUnits(wineId) {
  return currentCave.value.units.filter((unit) => countWineInUnit(wineId, unit) > 0).length
}

function countUnassignedBottles(wineId) {
  const wine = wines.value.find((entry) => entry.id === wineId)
  if (!wine) return 0
  return Math.max(0, wine.stock - countWineBottles(wineId))
}

function unitStyle(unit) {
  return {
    left: `${unit.x}%`,
    top: `${unit.y}%`,
    width: `${unit.w}%`,
    height: `${unit.h}%`,
    transform: `rotate(${unit.rotation}deg)`,
  }
}

function normalizeRotation(value) {
  const normalized = ((Math.round(value / 90) * 90) % 360 + 360) % 360
  return normalized
}

function rotateUnit(unit) {
  if (mode.value !== 'layout') return
  const previous = snapshotUnit(unit)
  unit.rotation = normalizeRotation(unit.rotation + 90)
  const resolved = resolveCollisionAndBounds(unit)
  if (!resolved) restoreUnit(unit, previous)
}

function applyFrontRows() {
  if (!selectedUnit.value) return
  const targetRows = clamp(Math.round(frontEditor.value.rows || 1), 1, 12)
  const currentCaps = [...frontEditor.value.rowCapacities]

  while (currentCaps.length < targetRows) currentCaps.push(6)
  if (currentCaps.length > targetRows) currentCaps.splice(targetRows)

  frontEditor.value.rows = targetRows
  frontEditor.value.rowCapacities = currentCaps.map((value) => clamp(Math.round(value || 1), 1, 24))
  applyRowCapacities()
}

function applyRowCapacities() {
  if (!selectedUnit.value) return
  const unit = selectedUnit.value
  const nextCapacities = frontEditor.value.rowCapacities.map((value) => clamp(Math.round(value || 1), 1, 24))

  unit.frontRows = nextCapacities.length
  unit.rowCapacities = nextCapacities
  unit.frontSlots = createFrontSlots(nextCapacities, unit.frontSlots)
  unit.maxRowCapacity = Math.max(...nextCapacities)
  selectedFrontSlot.value = null
  actionMessage.value = 'Front view updated.'
}

function onFrontSlotClick(rowIndex, colIndex) {
  if (!selectedUnit.value) return
  const slot = selectedUnit.value.frontSlots[rowIndex][colIndex]
  selectedFrontSlot.value = { row: rowIndex, col: colIndex, slot }

  if (!slot) {
    if (!selectedWine.value) {
      actionMessage.value = 'Select a wine first, then click an empty slot.'
      return
    }
    if (countUnassignedBottles(selectedWine.value.id) === 0) {
      actionMessage.value = `No unassigned bottles left for ${selectedWine.value.name}.`
      return
    }

    selectedUnit.value.frontSlots[rowIndex][colIndex] = {
      wineId: selectedWine.value.id,
      wineName: selectedWine.value.name,
      color: selectedWine.value.color,
    }
    selectedFrontSlot.value = {
      row: rowIndex,
      col: colIndex,
      slot: selectedUnit.value.frontSlots[rowIndex][colIndex],
    }
    actionMessage.value = `Placed ${selectedWine.value.name} in ${selectedUnit.value.name}.`
  }
}

function removeBottleFromFrontSlot() {
  if (!selectedUnit.value || !selectedFrontSlot.value) return
  const { row, col } = selectedFrontSlot.value
  selectedUnit.value.frontSlots[row][col] = null
  selectedFrontSlot.value = { row, col, slot: null }
  actionMessage.value = 'Bottle removed from slot.'
}

function deleteSelectedUnit() {
  if (!selectedUnit.value || mode.value !== 'layout') return
  if (rackOccupied(selectedUnit.value) > 0) {
    actionMessage.value = 'Rack cannot be deleted while it still has bottles.'
    return
  }

  currentCave.value.units = currentCave.value.units.filter((unit) => unit.id !== selectedUnit.value.id)
  selectedUnit.value = null
  selectedFrontSlot.value = null
  actionMessage.value = 'Rack deleted.'
}

function frontSlotClass(slot) {
  return {
    'front-slot--occupied': Boolean(slot),
    'front-slot--white': slot?.color === 'White',
    'front-slot--red': slot?.color === 'Red',
    'front-slot--sparkling': slot?.color === 'Sparkling',
  }
}

function onMapPointerDown() {
  selectedFrontSlot.value = null
}

function startMove(event, unit) {
  if (mode.value !== 'layout') return

  const pointerId = event.pointerId
  event.currentTarget.setPointerCapture(pointerId)

  interaction.value = {
    type: 'move',
    unitId: unit.id,
    startClientX: event.clientX,
    startClientY: event.clientY,
    startX: unit.x,
    startY: unit.y,
    startW: unit.w,
    startH: unit.h,
    startRotation: unit.rotation,
    moved: false,
  }

  addPointerListeners()
}

function startResize(event, unit) {
  if (mode.value !== 'layout') return

  const pointerId = event.pointerId
  event.currentTarget.setPointerCapture(pointerId)

  interaction.value = {
    type: 'resize',
    unitId: unit.id,
    startClientX: event.clientX,
    startClientY: event.clientY,
    startX: unit.x,
    startY: unit.y,
    startW: unit.w,
    startH: unit.h,
    startRotation: unit.rotation,
    moved: false,
  }

  addPointerListeners()
}

function addPointerListeners() {
  window.addEventListener('pointermove', onPointerMove)
  window.addEventListener('pointerup', onPointerUp)
}

function removePointerListeners() {
  window.removeEventListener('pointermove', onPointerMove)
  window.removeEventListener('pointerup', onPointerUp)
}

function onPointerMove(event) {
  if (!interaction.value.type || !mapRef.value) return

  const unit = currentCave.value.units.find((entry) => entry.id === interaction.value.unitId)
  if (!unit) return

  const mapRect = mapRef.value.getBoundingClientRect()
  const deltaXPercent = ((event.clientX - interaction.value.startClientX) / mapRect.width) * 100
  const deltaYPercent = ((event.clientY - interaction.value.startClientY) / mapRect.height) * 100

  interaction.value.moved = true

  if (interaction.value.type === 'move') {
    unit.x = interaction.value.startX + deltaXPercent
    unit.y = interaction.value.startY + deltaYPercent
  }

  if (interaction.value.type === 'resize') {
    unit.w = clamp(interaction.value.startW + deltaXPercent, 6, 45)
    unit.h = clamp(interaction.value.startH + deltaYPercent, 6, 45)
  }

  keepInsideMap(unit)
}

function onPointerUp() {
  if (!interaction.value.type) return

  const unit = currentCave.value.units.find((entry) => entry.id === interaction.value.unitId)
  if (unit) {
    const previous = {
      x: interaction.value.startX,
      y: interaction.value.startY,
      w: interaction.value.startW,
      h: interaction.value.startH,
      rotation: interaction.value.startRotation,
    }

    const resolved = resolveCollisionAndBounds(unit)
    if (!resolved) restoreUnit(unit, previous)
  }

  setTimeout(() => {
    interaction.value.moved = false
  }, 0)

  interaction.value.type = null
  removePointerListeners()
}

function keepInsideMap(unit) {
  const bounds = getRenderedBounds(unit)
  if (bounds.x < 0) unit.x += Math.abs(bounds.x)
  if (bounds.y < 0) unit.y += Math.abs(bounds.y)
  if (bounds.x + bounds.w > 100) unit.x -= bounds.x + bounds.w - 100
  if (bounds.y + bounds.h > 100) unit.y -= bounds.y + bounds.h - 100
}

function getRenderedBounds(unit) {
  if (unit.rotation % 180 === 0) {
    return { x: unit.x, y: unit.y, w: unit.w, h: unit.h }
  }

  const cx = unit.x + unit.w / 2
  const cy = unit.y + unit.h / 2
  return {
    x: cx - unit.h / 2,
    y: cy - unit.w / 2,
    w: unit.h,
    h: unit.w,
  }
}

function overlaps(unitA, unitB) {
  const a = getRenderedBounds(unitA)
  const b = getRenderedBounds(unitB)

  return !(
    a.x + a.w <= b.x ||
    b.x + b.w <= a.x ||
    a.y + a.h <= b.y ||
    b.y + b.h <= a.y
  )
}

function resolveCollisionAndBounds(unit) {
  keepInsideMap(unit)
  if (!hasCollision(unit)) return true

  const original = snapshotUnit(unit)
  const found = findNearestFreeSpot(unit)
  if (!found) {
    restoreUnit(unit, original)
    return false
  }

  unit.x = found.x
  unit.y = found.y
  keepInsideMap(unit)
  return true
}

function hasCollision(unit) {
  return currentCave.value.units.some((other) => other.id !== unit.id && overlaps(unit, other))
}

function findNearestFreeSpot(unit) {
  const origin = { x: unit.x, y: unit.y }
  const step = 1
  const maxRadius = 45

  for (let radius = 0; radius <= maxRadius; radius += step) {
    const candidates = []

    for (let x = origin.x - radius; x <= origin.x + radius; x += step) {
      candidates.push({ x, y: origin.y - radius })
      candidates.push({ x, y: origin.y + radius })
    }

    for (let y = origin.y - radius + step; y <= origin.y + radius - step; y += step) {
      candidates.push({ x: origin.x - radius, y })
      candidates.push({ x: origin.x + radius, y })
    }

    for (const candidate of candidates) {
      const probe = { ...unit, x: candidate.x, y: candidate.y }
      keepInsideMap(probe)
      if (!hasCollision(probe)) {
        return { x: probe.x, y: probe.y }
      }
    }
  }

  return null
}

function snapshotUnit(unit) {
  return {
    x: unit.x,
    y: unit.y,
    w: unit.w,
    h: unit.h,
    rotation: unit.rotation,
  }
}

function restoreUnit(unit, state) {
  unit.x = state.x
  unit.y = state.y
  unit.w = state.w
  unit.h = state.h
  unit.rotation = state.rotation
}

function clamp(value, min, max) {
  return Math.min(Math.max(value, min), max)
}

onBeforeUnmount(() => {
  removePointerListeners()
})
</script>

<style scoped>
.virtual-cellar-workspace {
  overflow: hidden;
}

.workspace-grid {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr) 360px;
  min-height: 780px;
}

.sidebar-left,
.sidebar-right {
  background: rgb(var(--v-theme-surface));
}

.sidebar-left {
  border-right: 1px solid rgba(0, 0, 0, 0.08);
}

.sidebar-right {
  border-left: 1px solid rgba(0, 0, 0, 0.08);
}

.workspace-center {
  background:
    radial-gradient(circle at top left, rgba(var(--v-theme-primary), 0.07), transparent 28%),
    linear-gradient(180deg, rgba(0, 0, 0, 0.015), transparent 22%);
}

.wine-list {
  max-height: 620px;
  overflow: auto;
  padding-right: 4px;
}

.wine-card {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.wine-card:hover {
  transform: translateY(-2px);
}

.map-card {
  min-height: 560px;
}

.cellar-map {
  position: relative;
  width: 100%;
  min-height: 460px;
  border-radius: 24px;
  background:
    linear-gradient(rgba(0, 0, 0, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.04) 1px, transparent 1px),
    linear-gradient(180deg, rgba(var(--v-theme-surface-variant), 0.28), rgba(var(--v-theme-surface), 0.95));
  background-size: 32px 32px, 32px 32px, auto;
  overflow: hidden;
}

.storage-unit {
  position: absolute;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  background: rgba(var(--v-theme-surface), 0.92);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
  transform-origin: center;
  transition: box-shadow 0.15s ease, border-color 0.15s ease;
}

.storage-unit__body {
  width: 100%;
  height: 100%;
  padding: 10px;
  cursor: move;
}

.storage-unit.is-selected {
  border-color: rgb(var(--v-theme-primary));
  box-shadow: 0 0 0 2px rgba(var(--v-theme-primary), 0.24);
}

.storage-unit.is-layout {
  border-style: dashed;
}

.unit-type--RACK {
  border-radius: 12px;
}

.storage-unit__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
}

.storage-unit__meta {
  font-size: 12px;
  opacity: 0.72;
}

.storage-unit__preview {
  margin-top: 8px;
  height: calc(100% - 44px);
  border: 1px dashed rgba(0, 0, 0, 0.18);
  border-radius: 8px;
  display: grid;
  place-items: center;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.6);
}

.unit-control {
  position: absolute;
  border: none;
  border-radius: 999px;
  width: 22px;
  height: 22px;
  background: rgba(var(--v-theme-primary), 0.95);
  color: white;
  cursor: pointer;
}

.unit-control--rotate {
  top: -8px;
  right: -8px;
}

.unit-control--resize {
  right: -8px;
  bottom: -8px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.7);
  cursor: nwse-resize;
}

.front-rack-grid {
  display: grid;
  gap: 8px;
}

.front-rack-row {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
}

.front-rack-row__label {
  width: 28px;
  font-size: 11px;
  opacity: 0.7;
}

.row-label {
  width: 54px;
}

.front-slot {
  min-width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  background: rgba(0, 0, 0, 0.04);
  cursor: pointer;
  font-size: 11px;
}

.front-slot--occupied {
  font-weight: 700;
}

.front-slot--white {
  background: #d9c38a;
  color: #503c10;
}

.front-slot--red {
  background: #7b2637;
  color: white;
}

.front-slot--sparkling {
  background: #6e7f59;
  color: white;
}

@media (max-width: 1400px) {
  .workspace-grid {
    grid-template-columns: 280px minmax(0, 1fr) 320px;
  }
}

@media (max-width: 1100px) {
  .workspace-grid {
    grid-template-columns: 1fr;
  }

  .sidebar-left,
  .sidebar-right {
    border: 0;
  }
}
</style>
