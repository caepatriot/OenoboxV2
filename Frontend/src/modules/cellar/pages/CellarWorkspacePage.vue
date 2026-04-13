<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>
            <h1>{{ cave?.name || 'Cellar Workspace' }}</h1>
            <v-spacer></v-spacer>
            <v-btn-toggle v-model="mode" mandatory>
              <v-btn value="view">
                <v-icon left>mdi-eye</v-icon>
                View
              </v-btn>
              <v-btn value="edit">
                <v-icon left>mdi-pencil</v-icon>
                Edit
              </v-btn>
            </v-btn-toggle>
            <v-btn color="primary" @click="showSetupWizard = true" v-if="mode === 'edit'">
              <v-icon left>mdi-plus</v-icon>
              Add Unit
            </v-btn>
            <v-btn color="secondary" @click="showAssignmentDialog = true">
              <v-icon left>mdi-package-variant</v-icon>
              Assign Bottles
            </v-btn>
          </v-card-title>
          <v-card-text>
            <div v-if="loading" class="text-center">
              <v-progress-circular indeterminate></v-progress-circular>
            </div>
            <div v-else-if="cave" class="cellar-canvas" ref="canvas">
              <div
                v-for="unit in cave.units"
                :key="unit.id"
                class="storage-unit"
                :style="getUnitStyle(unit)"
                @click="selectUnit(unit)"
                :class="{ selected: selectedUnit?.id === unit.id }"
              >
                <div class="unit-header">
                  {{ unit.name }} ({{ unit.type }})
                </div>
                <div class="unit-grid">
                  <div
                    v-for="space in unit.spaces"
                    :key="space.id"
                    class="bottle-space"
                    :class="{
                      occupied: space.currentBottles.length > 0,
                      selected: selectedSpace?.id === space.id,
                      'group-red': space.groupName === 'red',
                      'group-white': space.groupName === 'white'
                    }"
                    @click.stop="selectSpace(space)"
                    @drop="onDrop($event, space)"
                    @dragover.prevent
                    @dragleave.prevent
                  >
                    <div v-if="space.currentBottles.length > 0" class="bottle-label">
                      {{ space.currentBottles[0].wine.name }}
                    </div>
                    <div v-else class="empty-space">
                      {{ space.groupName || 'Empty' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Setup Wizard -->
    <v-dialog v-model="showSetupWizard" max-width="800px">
      <v-card>
        <v-card-title>Add Storage Unit</v-card-title>
        <v-card-text>
          <v-stepper v-model="wizardStep">
            <v-stepper-header>
              <v-stepper-step step="1">Unit Type</v-stepper-step>
              <v-stepper-step step="2">Dimensions</v-stepper-step>
              <v-stepper-step step="3">Position</v-stepper-step>
            </v-stepper-header>
            <v-stepper-items>
              <v-stepper-content step="1">
                <v-select
                  v-model="unitForm.type"
                  :items="unitTypes"
                  label="Unit Type"
                  item-text="text"
                  item-value="value"
                ></v-select>
                <v-text-field v-model="unitForm.name" label="Unit Name"></v-text-field>
                <v-btn color="primary" @click="wizardStep = 2">Next</v-btn>
              </v-stepper-content>
              <v-stepper-content step="2">
                <v-row>
                  <v-col cols="6">
                    <v-text-field v-model="unitForm.dimensions.width" label="Width (m)" type="number" step="0.1"></v-text-field>
                  </v-col>
                  <v-col cols="6">
                    <v-text-field v-model="unitForm.dimensions.height" label="Height (m)" type="number" step="0.1"></v-text-field>
                  </v-col>
                </v-row>
                <v-btn @click="wizardStep = 1">Back</v-btn>
                <v-btn color="primary" @click="wizardStep = 3">Next</v-btn>
              </v-stepper-content>
              <v-stepper-content step="3">
                <v-row>
                  <v-col cols="6">
                    <v-text-field v-model="unitForm.position.x" label="X Position" type="number" step="0.1"></v-text-field>
                  </v-col>
                  <v-col cols="6">
                    <v-text-field v-model="unitForm.position.y" label="Y Position" type="number" step="0.1"></v-text-field>
                  </v-col>
                </v-row>
                <v-btn @click="wizardStep = 2">Back</v-btn>
                <v-btn color="primary" @click="createUnit">Create Unit</v-btn>
              </v-stepper-content>
            </v-stepper-items>
          </v-stepper>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Assignment Dialog -->
    <v-dialog v-model="showAssignmentDialog" max-width="1000px">
      <v-card>
        <v-card-title>Assign Bottles to Spaces</v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="6">
              <v-text-field v-model="searchQuery" label="Search Wines" prepend-icon="mdi-magnify"></v-text-field>
              <v-list>
                <v-list-item
                  v-for="wine in filteredWines"
                  :key="wine.id"
                  @click="selectWine(wine)"
                  :class="{ selected: selectedWine?.id === wine.id }"
                >
                  <v-list-item-content>
                    <v-list-item-title>{{ wine.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ wine.wineTypeName }} - {{ wine.region }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="6">
              <div v-if="selectedWine">
                <h3>Suggested Spaces for {{ selectedWine.name }}</h3>
                <v-btn color="primary" @click="autoAssign">Auto Assign</v-btn>
                <div class="suggested-spaces">
                  <div
                    v-for="space in suggestedSpaces"
                    :key="space.id"
                    class="space-suggestion"
                    @click="assignToSpace(space)"
                  >
                    Unit: {{ getUnitName(space.unitId) }}, Position: {{ space.position.row }},{{ space.position.column }}
                  </div>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="showAssignmentDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Group Assignment -->
    <v-menu v-if="selectedSpace && mode === 'edit'" v-model="showGroupMenu" :position-x="groupMenuX" :position-y="groupMenuY">
      <v-list>
        <v-list-item @click="assignGroup('red')">
          <v-list-item-title>Red Wines</v-list-item-title>
        </v-list-item>
        <v-list-item @click="assignGroup('white')">
          <v-list-item-title>White Wines</v-list-item-title>
        </v-list-item>
        <v-list-item @click="assignGroup('sparkling')">
          <v-list-item-title>Sparkling Wines</v-list-item-title>
        </v-list-item>
        <v-list-item @click="assignGroup('dessert')">
          <v-list-item-title>Dessert Wines</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <v-text-field v-model="customGroup" label="Custom Group" @keyup.enter="assignGroup(customGroup)"></v-text-field>
        </v-list-item>
      </v-list>
    </v-menu>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useCellarStore } from '@/modules/cellar/store/cellar.store'
import { useWineStore } from '@/modules/winecatalog/store/winecatalog.store'

const route = useRoute()
const cellarStore = useCellarStore()
const wineStore = useWineStore()

const cave = computed(() => cellarStore.currentCave)
const loading = computed(() => cellarStore.loading)
const wines = computed(() => wineStore.wines)

const mode = ref('view')
const selectedUnit = ref(null)
const selectedSpace = ref(null)
const showSetupWizard = ref(false)
const showAssignmentDialog = ref(false)
const wizardStep = ref(1)
const searchQuery = ref('')
const selectedWine = ref(null)
const suggestedSpaces = ref([])
const showGroupMenu = ref(false)
const groupMenuX = ref(0)
const groupMenuY = ref(0)
const customGroup = ref('')

const unitTypes = [
  { text: 'Rack', value: 'rack' },
  { text: 'Shelf', value: 'shelf' },
  { text: 'Cabinet', value: 'cabinet' }
]

const unitForm = ref({
  name: '',
  type: 'rack',
  dimensions: { width: 1, height: 2 },
  position: { x: 0, y: 0 }
})

const filteredWines = computed(() => {
  if (!searchQuery.value) return wines.value.slice(0, 10)
  return wines.value.filter(wine =>
    wine.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    wine.region?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

onMounted(async () => {
  const cellarId = route.params.cellarId
  await cellarStore.fetchCave(cellarId)
  await wineStore.fetchWines()
})

const getUnitStyle = (unit) => {
  return {
    left: `${unit.position.x * 100}px`,
    top: `${unit.position.y * 100}px`,
    width: `${unit.dimensions.width * 100}px`,
    height: `${unit.dimensions.height * 100}px`
  }
}

const selectUnit = (unit) => {
  if (mode.value === 'edit') {
    selectedUnit.value = unit
    selectedSpace.value = null
  }
}

const selectSpace = (space) => {
  selectedSpace.value = space
  if (mode.value === 'edit') {
    showGroupMenu.value = true
    // Position menu at mouse or center
    groupMenuX.value = event.clientX || 0
    groupMenuY.value = event.clientY || 0
  }
}

const createUnit = async () => {
  await cellarStore.createUnit(cave.value.id, unitForm.value)
  showSetupWizard.value = false
  resetUnitForm()
}

const resetUnitForm = () => {
  unitForm.value = {
    name: '',
    type: 'rack',
    dimensions: { width: 1, height: 2 },
    position: { x: 0, y: 0 }
  }
  wizardStep.value = 1
}

const selectWine = (wine) => {
  selectedWine.value = wine
  loadSuggestedSpaces()
}

const loadSuggestedSpaces = async () => {
  if (selectedWine.value) {
    suggestedSpaces.value = await cellarStore.suggestSpacesForWine(cave.value.id, selectedWine.value.id)
  }
}

const assignToSpace = async (space) => {
  if (selectedWine.value) {
    await cellarStore.addPlacement({
      spaceId: space.id,
      wine: selectedWine.value,
      quantity: 1,
      dateAdded: new Date().toISOString().split('T')[0]
    })
    selectedWine.value = null
    suggestedSpaces.value = []
  }
}

const autoAssign = async () => {
  if (suggestedSpaces.value.length > 0) {
    await assignToSpace(suggestedSpaces.value[0])
  }
}

const assignGroup = async (groupName) => {
  if (selectedSpace.value) {
    await cellarStore.assignGroupToSpace(selectedSpace.value.id, groupName)
    showGroupMenu.value = false
  }
}

const getUnitName = (unitId) => {
  const unit = cave.value?.units.find(u => u.id === unitId)
  return unit?.name || 'Unknown'
}

const onDrop = (event, space) => {
  const wineId = event.dataTransfer.getData('wineId')
  if (wineId) {
    const wine = wines.value.find(w => w.id == wineId)
    if (wine) {
      selectedWine.value = wine
      assignToSpace(space)
    }
  }
}
</script>

<style scoped>
.cellar-canvas {
  position: relative;
  height: 600px;
  border: 1px solid #ccc;
  background: #f5f5f5;
}

.storage-unit {
  position: absolute;
  border: 2px solid #666;
  background: white;
  cursor: pointer;
}

.storage-unit.selected {
  border-color: #1976d2;
  box-shadow: 0 0 10px rgba(25, 118, 210, 0.3);
}

.unit-header {
  background: #eee;
  padding: 4px;
  font-size: 12px;
  text-align: center;
}

.unit-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, 40px);
  gap: 2px;
  padding: 4px;
}

.bottle-space {
  width: 40px;
  height: 40px;
  border: 1px solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  cursor: pointer;
  position: relative;
}

.bottle-space.occupied {
  background: #4caf50;
  color: white;
}

.bottle-space.selected {
  border-color: #1976d2;
  box-shadow: inset 0 0 5px rgba(25, 118, 210, 0.5);
}

.bottle-space.group-red {
  background: #f44336;
}

.bottle-space.group-white {
  background: #ffeb3b;
}

.bottle-label {
  font-size: 8px;
  text-align: center;
  word-wrap: break-word;
}

.empty-space {
  opacity: 0.6;
}

.space-suggestion {
  padding: 8px;
  border: 1px solid #ccc;
  margin: 4px 0;
  cursor: pointer;
  background: #f9f9f9;
}

.space-suggestion:hover {
  background: #e0e0e0;
}
</style>