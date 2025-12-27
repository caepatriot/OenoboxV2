<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { useCaveStore } from '@/stores/cave.js'

const caveStore = useCaveStore()

// ===== Constants / refs
const SCALE = 0.5
const canvasRef = ref(null)

// UI state
const showCreateCaveDialog = ref(false)
const showCreateUnitDialog = ref(false)
const showEditUnitDialog = ref(false)
const selectedView = ref('overview') // 'overview' or 'detail'
const editingUnit = ref(null)
const editMode = ref(false)
const draggingUnit = ref(null)
const dragOffset = ref({ x: 0, y: 0 })

// Form data
const newCaveData = reactive({
  name: '',
  description: '',
  dimensions: { width: 400, height: 300, depth: 200 },
  temperature: 12,
  humidity: 70
})

const newUnitData = reactive({
  name: '',
  type: 'rack',
  position: { x: 50, y: 50 },
  dimensions: { width: 120, height: 180, depth: 30 },
  orientation: 'vertical'
})

// Unit types (Vuetify 3 expects { title, value } by default)
const unitTypes = [
  { value: 'rack', title: 'Rack à vin' },
  { value: 'shelf', title: 'Étagère' },
  { value: 'cabinet', title: 'Armoire' },
  { value: 'wall-mounted', title: 'Fixation murale' },
  { value: 'floor-standing', title: 'Support au sol' }
]

// Load data on mount
onMounted(async () => {
  try {
    await caveStore.loadCaves()
  } catch (error) {
    console.error('Failed to load caves:', error)
  }
})

// Computed properties
const selectedCave = computed(() => caveStore.selectedCave)
const selectedUnit = computed(() => caveStore.selectedUnit)

// ===== CRUD
const createCave = async () => {
  try {
    await caveStore.createCave({ ...newCaveData })
    showCreateCaveDialog.value = false
    resetNewCaveData()
  } catch (error) {
    console.error('Failed to create cave:', error)
    alert('Erreur lors de la création de la cave')
  }
}

const selectCave = (cave) => {
  caveStore.selectCave(cave)
  selectedView.value = 'overview'
}

const selectUnit = (unit) => {
  caveStore.selectUnit(unit)
  selectedView.value = 'detail'
}

const createStorageUnit = async () => {
  if (!selectedCave.value) return

  try {
    await caveStore.addStorageUnit(selectedCave.value.id, { ...newUnitData })
    showCreateUnitDialog.value = false
    resetNewUnitData()
  } catch (error) {
    console.error('Failed to create storage unit:', error)
    alert("Erreur lors de la création de l'unité de stockage")
  }
}

const editStorageUnit = (unit) => {
  editingUnit.value = unit
  Object.assign(newUnitData, {
    name: unit.name,
    type: unit.type,
    position: { ...unit.position },
    dimensions: { ...unit.dimensions },
    orientation: unit.orientation
  })
  showEditUnitDialog.value = true
}

const updateStorageUnit = async () => {
  if (!selectedCave.value || !editingUnit.value) return

  try {
    await caveStore.updateStorageUnit(selectedCave.value.id, editingUnit.value.id, { ...newUnitData })
    showEditUnitDialog.value = false
    editingUnit.value = null
    resetNewUnitData()
  } catch (error) {
    console.error('Failed to update storage unit:', error)
    alert("Erreur lors de la mise à jour de l'unité de stockage")
  }
}

const deleteStorageUnit = async (unit) => {
  if (!selectedCave.value) return

  if (confirm(`Êtes-vous sûr de vouloir supprimer l'unité "${unit.name}" ?`)) {
    try {
      await caveStore.deleteStorageUnit(selectedCave.value.id, unit.id)
    } catch (error) {
      console.error('Failed to delete storage unit:', error)
      alert("Erreur lors de la suppression de l'unité de stockage")
    }
  }
}

// ===== Reset helpers
const resetNewCaveData = () => {
  Object.assign(newCaveData, {
    name: '',
    description: '',
    dimensions: { width: 400, height: 300, depth: 200 },
    temperature: 12,
    humidity: 70
  })
}

const resetNewUnitData = () => {
  Object.assign(newUnitData, {
    name: '',
    type: 'rack',
    position: { x: 50, y: 50 },
    dimensions: { width: 120, height: 180, depth: 30 },
    orientation: 'vertical'
  })
}

// ===== UI helpers
const getUnitIcon = (type) => {
  const icons = {
    rack: 'mdi-package-variant-closed',
    shelf: 'mdi-shelf',
    cabinet: 'mdi-archive',
    'wall-mounted': 'mdi-wall',
    'floor-standing': 'mdi-floor-plan'
  }
  return icons[type] || 'mdi-package-variant'
}

const getUnitColor = (type) => {
  const colors = {
    rack: 'brown',
    shelf: 'orange',
    cabinet: 'blue',
    'wall-mounted': 'green',
    'floor-standing': 'purple'
  }
  return colors[type] || 'grey'
}

// ===== Edit mode / drag
const toggleEditMode = () => {
  editMode.value = !editMode.value
  if (!editMode.value) {
    draggingUnit.value = null
    cleanupDragListeners()
  }
}

const cleanupDragListeners = () => {
  document.removeEventListener('mousemove', onMouseMove)
  document.removeEventListener('mouseup', onMouseUp)
}

onBeforeUnmount(() => {
  cleanupDragListeners()
})

const startDrag = (unit, event) => {
  if (!editMode.value) return
  if (event.button !== 0) return // only left click drags

  const canvas = canvasRef.value
  if (!canvas) return

  draggingUnit.value = unit

  const canvasRect = canvas.getBoundingClientRect()
  dragOffset.value = {
    x: event.clientX - canvasRect.left - unit.position.x * SCALE,
    y: event.clientY - canvasRect.top - unit.position.y * SCALE
  }

  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
}

const onMouseMove = (event) => {
  if (!draggingUnit.value || !editMode.value) return
  if (!selectedCave.value) return

  const canvas = canvasRef.value
  if (!canvas) return

  const canvasRect = canvas.getBoundingClientRect()
  const newX = (event.clientX - canvasRect.left - dragOffset.value.x) / SCALE
  const newY = (event.clientY - canvasRect.top - dragOffset.value.y) / SCALE

  const maxX = selectedCave.value.dimensions.width - draggingUnit.value.dimensions.width
  const maxY = selectedCave.value.dimensions.height - draggingUnit.value.dimensions.height

  draggingUnit.value.position.x = Math.max(0, Math.min(maxX, newX))
  draggingUnit.value.position.y = Math.max(0, Math.min(maxY, newY))
}

const onMouseUp = async () => {
  try {
    if (draggingUnit.value && selectedCave.value) {
      await caveStore.updateStorageUnit(selectedCave.value.id, draggingUnit.value.id, {
        position: { ...draggingUnit.value.position }
      })
    }
  } catch (error) {
    console.error('Failed to update unit position:', error)
  } finally {
    draggingUnit.value = null
    cleanupDragListeners()
  }
}

const rotateUnit = async (unit) => {
  if (!editMode.value) return
  if (!selectedCave.value) return

  const current = Number(unit.rotation || 0)
  unit.rotation = (current + 90) % 360

  try {
    await caveStore.updateStorageUnit(selectedCave.value.id, unit.id, {
      rotation: unit.rotation
    })
  } catch (error) {
    console.error('Failed to update unit rotation:', error)
  }
}

const onUnitClick = (unit) => {
  if (editMode.value) return
  selectUnit(unit)
}
</script>

<template>
  <div class="cave-admin-background">
    <v-container fluid class="cave-admin-container">
      <v-row class="mb-4">
        <v-col>
          <v-card class="header-card" elevation="4">
            <v-card-title class="d-flex align-center justify-space-between">
              <div class="d-flex align-center">
                <v-icon class="mr-3" size="32" color="brown">mdi-storefront</v-icon>
                <div>
                  <h1 class="text-h4 mb-1">Administration des Caves</h1>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    Gérez la disposition et la configuration de vos espaces de stockage
                  </p>
                </div>
              </div>

              <v-btn color="brown" variant="flat" @click="showCreateCaveDialog = true" class="create-cave-btn">
                <v-icon class="mr-2">mdi-plus</v-icon>
                Nouvelle Cave
              </v-btn>
            </v-card-title>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <!-- Sidebar -->
        <v-col cols="12" md="3">
          <v-card class="cave-list-card" elevation="4">
            <v-card-title class="cave-list-title">
              <v-icon class="mr-2">mdi-format-list-bulleted</v-icon>
              Mes Caves
            </v-card-title>

            <v-card-text class="pa-0">
              <v-list class="cave-list">
                <v-list-item
                    v-for="cave in caveStore.caves"
                    :key="cave.id"
                    @click="selectCave(cave)"
                    :class="{ 'cave-list-item-active': selectedCave?.id === cave.id }"
                    class="cave-list-item"
                >
                  <template #prepend>
                    <v-icon class="mr-3" color="brown">mdi-home</v-icon>
                  </template>

                  <v-list-item-title class="cave-name">{{ cave.name }}</v-list-item-title>
                  <v-list-item-subtitle class="cave-details">
                    {{ (cave.units?.length ?? 0) }} unités • {{ cave.dimensions.width }}×{{ cave.dimensions.height }}cm
                  </v-list-item-subtitle>

                  <template #append>
                    <v-chip size="small" variant="outlined" color="brown">
                      {{ cave.units?.length ?? 0 }}
                    </v-chip>
                  </template>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Main -->
        <v-col cols="12" md="9">
          <!-- Overview -->
          <div v-if="selectedCave && selectedView === 'overview'">
            <v-card class="cave-overview-card" elevation="4">
              <v-card-title class="cave-overview-title">
                <div class="d-flex align-center justify-space-between w-100">
                  <div class="d-flex align-center">
                    <v-icon class="mr-3" color="brown">mdi-floor-plan</v-icon>
                    <div>
                      <h2 class="text-h5 mb-1">{{ selectedCave.name }}</h2>
                      <p class="text-body-2 text-medium-emphasis mb-0">{{ selectedCave.description }}</p>
                    </div>
                  </div>

                  <div class="d-flex align-center">
                    <v-btn color="primary" variant="flat" @click="showCreateUnitDialog = true">
                      <v-icon class="mr-2">mdi-plus</v-icon>
                      Ajouter Unité
                    </v-btn>

                    <v-btn
                        :color="editMode ? 'success' : 'secondary'"
                        :variant="editMode ? 'flat' : 'outlined'"
                        @click="toggleEditMode"
                        class="ml-2"
                    >
                      <v-icon class="mr-2">{{ editMode ? 'mdi-check' : 'mdi-pencil' }}</v-icon>
                      {{ editMode ? 'Terminer Édition' : 'Éditer Disposition' }}
                    </v-btn>
                  </div>
                </div>
              </v-card-title>

              <v-card-text>
                <!-- Stats -->
                <v-row class="mb-4">
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="brown" class="mb-2">mdi-package-variant-closed</v-icon>
                        <div class="text-h6">{{ selectedCave.units.length }}</div>
                        <div class="text-caption">Unités</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="blue" class="mb-2">mdi-thermometer</v-icon>
                        <div class="text-h6">{{ selectedCave.temperature }}°C</div>
                        <div class="text-caption">Température</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="green" class="mb-2">mdi-water-percent</v-icon>
                        <div class="text-h6">{{ selectedCave.humidity }}%</div>
                        <div class="text-caption">Humidité</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="orange" class="mb-2">mdi-ruler-square</v-icon>
                        <div class="text-h6">{{ selectedCave.dimensions.width }}×{{ selectedCave.dimensions.height }}</div>
                        <div class="text-caption">Dimensions (cm)</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>

                <!-- Layout -->
                <div class="cave-layout-container">
                  <h3 class="text-h6 mb-3">Vue d'ensemble de la cave</h3>

                  <div
                      ref="canvasRef"
                      class="cave-layout-canvas"
                      :style="{
                      width: selectedCave.dimensions.width * SCALE + 'px',
                      height: selectedCave.dimensions.height * SCALE + 'px'
                    }"
                  >
                    <div
                        v-for="unit in selectedCave.units"
                        :key="unit.id"
                        class="storage-unit"
                        :class="[
                        `unit-${unit.type}`,
                        { 'unit-editing': editMode, 'unit-dragging': draggingUnit === unit }
                      ]"
                        :style="{
                        left: unit.position.x * SCALE + 'px',
                        top: unit.position.y * SCALE + 'px',
                        width: unit.dimensions.width * SCALE + 'px',
                        height: unit.dimensions.height * SCALE + 'px',
                        '--rotation': `${unit.rotation || 0}deg`,
                        cursor: editMode ? 'move' : 'pointer'
                      }"
                        @mousedown="startDrag(unit, $event)"
                        @contextmenu.prevent="rotateUnit(unit)"
                        @click="onUnitClick(unit)"
                    >
                      <div class="unit-header">
                        <v-icon :icon="getUnitIcon(unit.type)" size="16" />
                        <span class="unit-name">{{ unit.name }}</span>
                      </div>

                      <div class="unit-capacity">
                        {{ unit.capacity }} emplacements
                      </div>

                      <div v-if="!editMode" class="unit-actions">
                        <v-btn icon size="small" variant="text" @click.stop="editStorageUnit(unit)">
                          <v-icon size="14">mdi-pencil</v-icon>
                        </v-btn>
                        <v-btn icon size="small" variant="text" color="error" @click.stop="deleteStorageUnit(unit)">
                          <v-icon size="14">mdi-delete</v-icon>
                        </v-btn>
                      </div>

                      <div v-else class="unit-edit-hint">
                        <v-icon size="12" color="white">mdi-rotate-right</v-icon>
                        <span class="edit-text">Clic droit pour tourner</span>
                      </div>
                    </div>
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </div>

          <!-- Detail -->
          <div v-else-if="selectedUnit && selectedView === 'detail'">
            <v-card class="unit-detail-card" elevation="4">
              <v-card-title class="unit-detail-title">
                <v-btn icon variant="text" @click="selectedView = 'overview'" class="mr-2">
                  <v-icon>mdi-arrow-left</v-icon>
                </v-btn>

                <v-icon class="mr-3" :icon="getUnitIcon(selectedUnit.type)" :color="getUnitColor(selectedUnit.type)" />

                <div>
                  <h2 class="text-h5 mb-1">{{ selectedUnit.name }}</h2>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    {{ selectedUnit.type }} • {{ selectedUnit.capacity }} emplacements
                  </p>
                </div>
              </v-card-title>

              <v-card-text>
                <v-row>
                  <v-col cols="12" md="8">
                    <div class="unit-grid-container">
                      <h4 class="text-subtitle-1 mb-3">Vue détaillée des emplacements</h4>
                      <div class="unit-grid">
                        <div
                            v-for="space in selectedUnit.spaces"
                            :key="space.id"
                            class="storage-space"
                            :class="{ 'space-occupied': caveStore.occupiedSpaces.includes(space) }"
                        >
                          <div class="space-content">
                            <v-icon v-if="caveStore.occupiedSpaces.includes(space)" color="green" size="20">
                              mdi-bottle-wine
                            </v-icon>
                            <span v-else class="space-number">
                              {{ space.position.row + 1 }}-{{ space.position.column + 1 }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </v-col>

                  <v-col cols="12" md="4">
                    <v-card variant="outlined" class="unit-properties">
                      <v-card-title class="text-subtitle-1">Propriétés</v-card-title>
                      <v-card-text>
                        <div class="property-item">
                          <strong>Type:</strong> {{ unitTypes.find(t => t.value === selectedUnit.type)?.title }}
                        </div>
                        <div class="property-item">
                          <strong>Position:</strong> {{ selectedUnit.position.x }}, {{ selectedUnit.position.y }}
                        </div>
                        <div class="property-item">
                          <strong>Dimensions:</strong>
                          {{ selectedUnit.dimensions.width }}×{{ selectedUnit.dimensions.height }}×{{ selectedUnit.dimensions.depth }} cm
                        </div>
                        <div class="property-item">
                          <strong>Orientation:</strong> {{ selectedUnit.orientation === 'vertical' ? 'Verticale' : 'Horizontale' }}
                        </div>
                        <div class="property-item">
                          <strong>Capacité:</strong> {{ selectedUnit.capacity }} bouteilles
                        </div>
                        <div class="property-item">
                          <strong>Occupés:</strong> {{ caveStore.occupiedSpaces.length }}/{{ selectedUnit.capacity }}
                        </div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </div>

          <!-- Empty -->
          <div v-else class="no-selection">
            <v-card class="no-selection-card" elevation="4">
              <v-card-text class="text-center pa-8">
                <v-icon size="64" color="grey" class="mb-4">mdi-storefront-outline</v-icon>
                <h3 class="text-h5 mb-2">Aucune cave sélectionnée</h3>
                <p class="text-body-1 text-medium-emphasis mb-4">
                  Sélectionnez une cave dans la liste ou créez-en une nouvelle pour commencer.
                </p>
                <v-btn color="brown" variant="flat" @click="showCreateCaveDialog = true" size="large">
                  <v-icon class="mr-2">mdi-plus</v-icon>
                  Créer ma première cave
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <!-- Create Cave -->
    <v-dialog v-model="showCreateCaveDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="brown">mdi-plus</v-icon>
          Créer une nouvelle cave
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newCaveData.name" label="Nom de la cave" variant="outlined" required class="mb-3" />
            <v-textarea v-model="newCaveData.description" label="Description" variant="outlined" class="mb-3" />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>

            <v-row class="mt-3">
              <v-col cols="6">
                <v-text-field v-model.number="newCaveData.temperature" label="Température (°C)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="6">
                <v-text-field v-model.number="newCaveData.humidity" label="Humidité (%)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showCreateCaveDialog = false">Annuler</v-btn>
          <v-btn color="brown" variant="flat" @click="createCave" :disabled="!newCaveData.name">Créer la cave</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Create Unit -->
    <v-dialog v-model="showCreateUnitDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-plus</v-icon>
          Ajouter une unité de stockage
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newUnitData.name" label="Nom de l'unité" variant="outlined" required class="mb-3" />

            <v-select v-model="newUnitData.type" :items="unitTypes" label="Type d'unité" variant="outlined" class="mb-3" />

            <v-select
                v-model="newUnitData.orientation"
                :items="[
                { value: 'vertical', title: 'Verticale' },
                { value: 'horizontal', title: 'Horizontale' }
              ]"
                label="Orientation"
                variant="outlined"
                class="mb-3"
            />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showCreateUnitDialog = false">Annuler</v-btn>
          <v-btn color="primary" variant="flat" @click="createStorageUnit" :disabled="!newUnitData.name">Ajouter l'unité</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Edit Unit -->
    <v-dialog v-model="showEditUnitDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-pencil</v-icon>
          Modifier l'unité de stockage
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newUnitData.name" label="Nom de l'unité" variant="outlined" required class="mb-3" />
            <v-select v-model="newUnitData.type" :items="unitTypes" label="Type d'unité" variant="outlined" class="mb-3" />

            <v-select
                v-model="newUnitData.orientation"
                :items="[
                { value: 'vertical', title: 'Verticale' },
                { value: 'horizontal', title: 'Horizontale' }
              ]"
                label="Orientation"
                variant="outlined"
                class="mb-3"
            />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showEditUnitDialog = false">Annuler</v-btn>
          <v-btn color="primary" variant="flat" @click="updateStorageUnit" :disabled="!newUnitData.name">Mettre à jour</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped>
.cave-admin-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 16px;
}

.cave-admin-container {
  max-width: 1400px;
  margin: 0 auto;
}

.header-card,
.cave-list-card,
.cave-overview-card,
.unit-detail-card,
.no-selection-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

.create-cave-btn {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  color: white;
  border-radius: 20px;
}

.cave-list-title {
  background: linear-gradient(90deg, #8b4513, #a0522d);
  color: white;
  border-radius: 10px 10px 0 0;
}

.cave-list-item {
  border-radius: 6px;
  margin: 2px 8px;
  transition: all 0.3s ease;
}

.cave-list-item:hover {
  background: rgba(139, 69, 19, 0.1);
}

.cave-list-item-active {
  background: rgba(139, 69, 19, 0.2);
  border-left: 3px solid #8b4513;
}

.cave-name {
  font-weight: 600;
  color: #8b4513;
}

.cave-details {
  font-size: 0.75rem;
}

.cave-overview-title,
.unit-detail-title {
  background: linear-gradient(90deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  color: #8b4513;
  border-radius: 10px 10px 0 0;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
}

.cave-layout-container {
  margin-top: 24px;
}

.cave-layout-canvas {
  position: relative;
  background: linear-gradient(45deg, #f9f9f9, #e9e9e9);
  border: 2px solid #ddd;
  border-radius: 8px;
  margin: 0 auto;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
}

.storage-unit {
  position: absolute;
  border: 2px solid;
  border-radius: 4px;
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4px;
  font-size: 10px;
  overflow: hidden;
  transform: rotate(var(--rotation));
  transform-origin: center;
}

.storage-unit:hover {
  z-index: 10;
  transform: rotate(var(--rotation)) scale(1.05);
}

.unit-rack {
  background: linear-gradient(45deg, #d2b48c, #cd853f);
  border-color: #8b4513;
}

.unit-shelf {
  background: linear-gradient(45deg, #f4a460, #daa520);
  border-color: #a0522d;
}

.unit-cabinet {
  background: linear-gradient(45deg, #b0c4de, #778899);
  border-color: #4682b4;
}

.unit-wall-mounted {
  background: linear-gradient(45deg, #98fb98, #32cd32);
  border-color: #228b22;
}

.unit-floor-standing {
  background: linear-gradient(45deg, #dda0dd, #ba55d3);
  border-color: #8a2be2;
}

.unit-header {
  display: flex;
  align-items: center;
  gap: 2px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.unit-name {
  font-size: 9px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.unit-capacity {
  font-size: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-align: center;
}

.unit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.storage-unit:hover .unit-actions {
  opacity: 1;
}

.unit-editing {
  border-style: dashed !important;
  border-width: 3px !important;
}

.unit-dragging {
  z-index: 1000;
  opacity: 0.85;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.unit-edit-hint {
  display: flex;
  align-items: center;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.25s ease;
  font-size: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.storage-unit:hover .unit-edit-hint {
  opacity: 1;
}

.edit-text {
  font-size: 7px;
  white-space: nowrap;
}

.unit-grid-container {
  margin-bottom: 24px;
}

.unit-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
  max-width: 400px;
  margin: 0 auto;
}

.storage-space {
  aspect-ratio: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
  transition: all 0.25s ease;
  cursor: pointer;
}

.storage-space:hover {
  background: #e9e9e9;
  transform: scale(1.05);
}

.space-occupied {
  background: linear-gradient(45deg, #98fb98, #32cd32);
  border-color: #228b22;
}

.space-content {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
  color: #666;
}

.space-occupied .space-content {
  color: white;
}

.space-number {
  font-size: 8px;
}

.unit-properties {
  border-radius: 8px;
}

.property-item {
  margin-bottom: 8px;
  font-size: 0.875rem;
}

.property-item strong {
  color: #8b4513;
}

.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

/* Responsive */
@media (max-width: 960px) {
  .cave-admin-background {
    padding: 8px;
  }
  .cave-layout-canvas {
    max-width: 100%;
    height: auto;
    aspect-ratio: 4/3;
  }
  .unit-grid {
    grid-template-columns: repeat(4, 1fr);
    max-width: 300px;
  }
}

@media (max-width: 600px) {
  .cave-admin-background {
    padding: 4px;
  }
  .unit-grid {
    grid-template-columns: repeat(3, 1fr);
    max-width: 250px;
  }
  .storage-space {
    min-height: 30px;
  }
}
</style>
