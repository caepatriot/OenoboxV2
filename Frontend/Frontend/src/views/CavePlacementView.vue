<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useCaveStore } from '@/stores/cave.js'

const caveStore = useCaveStore()

// UI state
const showAddBottleDialog = ref(false)
const showNewWineDialog = ref(false)
const searchQuery = ref('')
const selectedSpaceForPlacement = ref(null)

// Form data for bottle placement
const bottlePlacementData = reactive({
  wine: null,
  quantity: 1,
  preferredStorageDuration: 5,
  notes: ''
})

// Form data for new wine
const newWineData = reactive({
  name: '',
  type: 'red',
  cepage: [],
  region: '',
  year: new Date().getFullYear(),
  aopIgpVdf: '',
  prixLancement: 0
})

// Wine type options
const wineTypes = [
  { value: 'red', title: 'Rouge' },
  { value: 'white', title: 'Blanc' },
  { value: 'rose', title: 'Rosé' }
]

// Load data on mount
onMounted(async () => {
  try {
    await caveStore.loadCaves()
    await caveStore.getWines()
  } catch (error) {
    console.error('Failed to load data:', error)
  }
})

// Computed properties
const selectedCave = computed(() => caveStore.selectedCave)
const selectedUnit = computed(() => caveStore.selectedUnit)
const availableSpaces = computed(() => caveStore.availableSpaces)
const occupiedSpaces = computed(() => caveStore.occupiedSpaces)

const filteredWines = computed(() => {
  if (!searchQuery.value) return caveStore.wines
  return caveStore.wines.filter(wine =>
    wine.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    wine.region.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    wine.cepage.some(c => c.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

const totalBottlesInCave = computed(() => {
  if (!selectedCave.value) return 0
  return selectedCave.value.units.reduce((total, unit) => {
    return total + unit.spaces.filter(space =>
      caveStore.bottlePlacements.some(p => p.spaceId === space.id)
    ).length
  }, 0)
})

// Watch for cave selection to auto-select first unit
watch(selectedCave, (newCave) => {
  if (newCave && newCave.units.length > 0) {
    caveStore.selectUnit(newCave.units[0])
  }
})

// Methods
const selectCave = (cave) => {
  caveStore.selectCave(cave)
}

const selectUnit = (unit) => {
  caveStore.selectUnit(unit)
}

const selectSpaceForPlacement = (space) => {
  selectedSpaceForPlacement.value = space
  bottlePlacementData.wine = null
  bottlePlacementData.quantity = 1
  bottlePlacementData.preferredStorageDuration = 5
  bottlePlacementData.notes = ''
  showAddBottleDialog.value = true
}

const addBottlePlacement = async () => {
  if (!selectedSpaceForPlacement.value || !bottlePlacementData.wine) return

  try {
    await caveStore.addBottlePlacement({
      spaceId: selectedSpaceForPlacement.value.id,
      wine: bottlePlacementData.wine,
      quantity: bottlePlacementData.quantity,
      preferredStorageDuration: bottlePlacementData.preferredStorageDuration,
      notes: bottlePlacementData.notes
    })

    showAddBottleDialog.value = false
    selectedSpaceForPlacement.value = null
  } catch (error) {
    console.error('Failed to add bottle placement:', error)
    alert('Erreur lors de l\'ajout de la bouteille')
  }
}

const removeBottlePlacement = async (space) => {
  const placement = caveStore.bottlePlacements.find(p => p.spaceId === space.id)
  if (!placement) return

  if (confirm('Êtes-vous sûr de vouloir retirer cette bouteille ?')) {
    try {
      await caveStore.removeBottlePlacement(placement.id)
    } catch (error) {
      console.error('Failed to remove bottle placement:', error)
      alert('Erreur lors de la suppression de la bouteille')
    }
  }
}

const createNewWine = async () => {
  // Add the new wine to the store
  const newWine = {
    id: Math.max(...caveStore.wines.map(w => w.id)) + 1,
    ...newWineData
  }

  caveStore.wines.push(newWine)
  bottlePlacementData.wine = newWine

  showNewWineDialog.value = false
  resetNewWineData()
}

const selectWine = (wine) => {
  bottlePlacementData.wine = wine
}

const resetNewWineData = () => {
  Object.assign(newWineData, {
    name: '',
    type: 'red',
    cepage: [],
    region: '',
    year: new Date().getFullYear(),
    aopIgpVdf: '',
    prixLancement: 0
  })
}

// Helper methods
const getWineDisplayName = (wine) => {
  return `${wine.name} ${wine.year} - ${wine.region}`
}

const getSpaceStatus = (space) => {
  const placement = caveStore.bottlePlacements.find(p => p.spaceId === space.id)
  return placement ? 'occupied' : 'available'
}

const getSpaceContent = (space) => {
  const placement = caveStore.bottlePlacements.find(p => p.spaceId === space.id)
  if (placement) {
    return {
      wine: placement.wine,
      quantity: placement.quantity,
      dateAdded: placement.dateAdded
    }
  }
  return null
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="cave-placement-background">
    <v-container fluid class="cave-placement-container">
      <!-- Header -->
      <v-row class="mb-4">
        <v-col>
          <v-card class="header-card" elevation="4">
            <v-card-title class="d-flex align-center justify-space-between">
              <div class="d-flex align-center">
                <v-icon class="mr-3" size="32" color="green">mdi-bottle-wine</v-icon>
                <div>
                  <h1 class="text-h4 mb-1">Placement des Bouteilles</h1>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    Gérez le stockage de vos bouteilles dans les espaces disponibles
                  </p>
                </div>
              </div>
              <div class="d-flex align-center">
                <v-chip color="green" variant="flat" class="mr-3">
                  <v-icon class="mr-1">mdi-bottle-wine</v-icon>
                  {{ caveStore.totalBottles }} bouteilles
                </v-chip>
              </div>
            </v-card-title>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <!-- Cave/Unit Selection Sidebar -->
        <v-col cols="12" md="3">
          <!-- Cave Selection -->
          <v-card class="selection-card" elevation="4">
            <v-card-title class="selection-title">
              <v-icon class="mr-2">mdi-storefront</v-icon>
              Sélectionner une cave
            </v-card-title>
            <v-card-text class="pa-0">
              <v-list class="cave-list">
                <v-list-item
                  v-for="cave in caveStore.caves"
                  :key="cave.id"
                  @click="selectCave(cave)"
                  :class="{ 'list-item-active': selectedCave?.id === cave.id }"
                  class="list-item"
                >
                  <v-icon class="mr-3" color="brown">mdi-home</v-icon>
                  <v-list-item-content>
                    <v-list-item-title class="item-name">{{ cave.name }}</v-list-item-title>
                    <v-list-item-subtitle class="item-details">
                      {{ totalBottlesInCave }}/{{ cave.units.reduce((total, unit) => total + unit.capacity, 0) }} bouteilles
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>

          <!-- Unit Selection -->
          <v-card v-if="selectedCave" class="selection-card mt-4" elevation="4">
            <v-card-title class="selection-title">
              <v-icon class="mr-2">mdi-package-variant-closed</v-icon>
              Unités de stockage
            </v-card-title>
            <v-card-text class="pa-0">
              <v-list class="unit-list">
                <v-list-item
                  v-for="unit in selectedCave.units"
                  :key="unit.id"
                  @click="selectUnit(unit)"
                  :class="{ 'list-item-active': selectedUnit?.id === unit.id }"
                  class="list-item"
                >
                  <v-icon class="mr-3" :color="getUnitColor(unit.type)">mdi-package-variant-closed</v-icon>
                  <v-list-item-content>
                    <v-list-item-title class="item-name">{{ unit.name }}</v-list-item-title>
                    <v-list-item-subtitle class="item-details">
                      {{ occupiedSpaces.length }}/{{ unit.capacity }} emplacements
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Main Content Area -->
        <v-col cols="12" md="9">
          <!-- Unit Detail View -->
          <div v-if="selectedUnit">
            <v-card class="unit-detail-card" elevation="4">
              <v-card-title class="unit-detail-title">
                <v-icon class="mr-3" :icon="getUnitIcon(selectedUnit.type)" :color="getUnitColor(selectedUnit.type)"></v-icon>
                <div>
                  <h2 class="text-h5 mb-1">{{ selectedUnit.name }}</h2>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    {{ selectedUnit.type }} • {{ occupiedSpaces.length }}/{{ selectedUnit.capacity }} emplacements occupés
                  </p>
                </div>
              </v-card-title>

              <v-card-text>
                <!-- Unit Statistics -->
                <v-row class="mb-4">
                  <v-col cols="12" sm="4">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="green" class="mb-2">mdi-bottle-wine</v-icon>
                        <div class="text-h6">{{ occupiedSpaces.length }}</div>
                        <div class="text-caption">Bouteilles stockées</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="4">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="blue" class="mb-2">mdi-package-variant-closed</v-icon>
                        <div class="text-h6">{{ availableSpaces.length }}</div>
                        <div class="text-caption">Emplacements libres</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="4">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="orange" class="mb-2">mdi-percent</v-icon>
                        <div class="text-h6">{{ Math.round((occupiedSpaces.length / selectedUnit.capacity) * 100) }}%</div>
                        <div class="text-caption">Taux d'occupation</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>

                <!-- Storage Grid -->
                <div class="storage-grid-container">
                  <h3 class="text-h6 mb-3">Emplacements de stockage</h3>
                  <div class="storage-grid">
                    <div
                      v-for="space in selectedUnit.spaces"
                      :key="space.id"
                      class="storage-space"
                      :class="`space-${getSpaceStatus(space)}`"
                      @click="getSpaceStatus(space) === 'available' ? selectSpaceForPlacement(space) : null"
                    >
                      <div class="space-header">
                        <span class="space-coordinates">{{ space.position.row + 1 }}-{{ space.position.column + 1 }}</span>
                        <v-btn
                          v-if="getSpaceStatus(space) === 'occupied'"
                          icon
                          size="small"
                          variant="text"
                          color="error"
                          @click.stop="removeBottlePlacement(space)"
                          class="remove-btn"
                        >
                          <v-icon size="14">mdi-close</v-icon>
                        </v-btn>
                      </div>

                      <div v-if="getSpaceStatus(space) === 'occupied'" class="space-content">
                        <div class="wine-info">
                          <div class="wine-name">{{ getSpaceContent(space).wine.name }}</div>
                          <div class="wine-details">
                            {{ getSpaceContent(space).wine.year }} • {{ getSpaceContent(space).wine.region }}
                          </div>
                          <div class="wine-date">
                            Ajouté: {{ formatDate(getSpaceContent(space).dateAdded) }}
                          </div>
                        </div>
                      </div>

                      <div v-else class="space-empty">
                        <v-icon color="grey" size="24">mdi-plus</v-icon>
                        <div class="empty-text">Ajouter</div>
                      </div>
                    </div>
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </div>

          <!-- No Unit Selected -->
          <div v-else-if="selectedCave" class="no-selection">
            <v-card class="no-selection-card" elevation="4">
              <v-card-text class="text-center pa-8">
                <v-icon size="64" color="grey" class="mb-4">mdi-package-variant-closed</v-icon>
                <h3 class="text-h5 mb-2">Aucune unité sélectionnée</h3>
                <p class="text-body-1 text-medium-emphasis mb-4">
                  Sélectionnez une unité de stockage pour voir les emplacements disponibles.
                </p>
              </v-card-text>
            </v-card>
          </div>

          <!-- No Cave Selected -->
          <div v-else class="no-selection">
            <v-card class="no-selection-card" elevation="4">
              <v-card-text class="text-center pa-8">
                <v-icon size="64" color="grey" class="mb-4">mdi-storefront-outline</v-icon>
                <h3 class="text-h5 mb-2">Aucune cave sélectionnée</h3>
                <p class="text-body-1 text-medium-emphasis mb-4">
                  Sélectionnez une cave pour commencer le placement des bouteilles.
                </p>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <!-- Add Bottle Dialog -->
    <v-dialog v-model="showAddBottleDialog" max-width="800px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="green">mdi-bottle-wine</v-icon>
          Ajouter une bouteille
          <v-chip size="small" variant="outlined" color="blue" class="ml-3">
            Emplacement {{ selectedSpaceForPlacement?.position.row + 1 }}-{{ selectedSpaceForPlacement?.position.column + 1 }}
          </v-chip>
        </v-card-title>
        <v-card-text>
          <!-- Wine Selection -->
          <div class="wine-selection-section">
            <h4 class="text-subtitle-1 mb-3">Sélectionner un vin</h4>

            <!-- Search -->
            <v-text-field
              v-model="searchQuery"
              label="Rechercher un vin..."
              variant="outlined"
              prepend-inner-icon="mdi-magnify"
              class="mb-3"
              clearable
            />

            <!-- Wine List -->
            <div class="wine-list-container">
              <v-list class="wine-list" max-height="200">
                <v-list-item
                  v-for="wine in filteredWines"
                  :key="wine.id"
                  @click="selectWine(wine)"
                  :class="{ 'wine-selected': bottlePlacementData.wine?.id === wine.id }"
                  class="wine-item"
                >
                  <v-icon class="mr-3" :color="wine.type === 'red' ? 'red' : wine.type === 'white' ? 'yellow' : 'pink'">
                    mdi-bottle-wine
                  </v-icon>
                  <v-list-item-content>
                    <v-list-item-title>{{ wine.name }}</v-list-item-title>
                    <v-list-item-subtitle>
                      {{ wine.year }} • {{ wine.region }} • {{ wineTypes.find(t => t.value === wine.type)?.title }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-btn
                      v-if="bottlePlacementData.wine?.id === wine.id"
                      icon
                      color="green"
                      size="small"
                    >
                      <v-icon>mdi-check</v-icon>
                    </v-btn>
                  </v-list-item-action>
                </v-list-item>
              </v-list>
            </div>

            <!-- Add New Wine Button -->
            <v-btn
              variant="outlined"
              color="primary"
              @click="showNewWineDialog = true"
              class="mt-3"
            >
              <v-icon class="mr-2">mdi-plus</v-icon>
              Ajouter un nouveau vin
            </v-btn>
          </div>

          <!-- Placement Details -->
          <v-divider class="my-4" />
          <div v-if="bottlePlacementData.wine" class="placement-details-section">
            <h4 class="text-subtitle-1 mb-3">Détails du placement</h4>

            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="bottlePlacementData.quantity"
                  label="Quantité"
                  type="number"
                  variant="outlined"
                  :min="1"
                  :max="selectedSpaceForPlacement?.capacity || 1"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model.number="bottlePlacementData.preferredStorageDuration"
                  label="Durée de stockage souhaitée (années)"
                  type="number"
                  variant="outlined"
                  :min="1"
                />
              </v-col>
            </v-row>

            <v-textarea
              v-model="bottlePlacementData.notes"
              label="Notes (optionnel)"
              variant="outlined"
              rows="2"
              class="mt-3"
            />
          </div>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showAddBottleDialog = false">Annuler</v-btn>
          <v-btn
            color="green"
            variant="flat"
            @click="addBottlePlacement"
            :disabled="!bottlePlacementData.wine"
          >
            <v-icon class="mr-2">mdi-content-save</v-icon>
            Ajouter la bouteille
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- New Wine Dialog -->
    <v-dialog v-model="showNewWineDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-plus</v-icon>
          Ajouter un nouveau vin
        </v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field
              v-model="newWineData.name"
              label="Nom du vin"
              variant="outlined"
              required
              class="mb-3"
            />
            <v-select
              v-model="newWineData.type"
              :items="wineTypes"
              label="Type de vin"
              variant="outlined"
              class="mb-3"
            />
            <v-combobox
              v-model="newWineData.cepage"
              label="Cépages"
              variant="outlined"
              multiple
              chips
              class="mb-3"
              hint="Séparez les cépages par des virgules"
            />
            <v-text-field
              v-model="newWineData.region"
              label="Région"
              variant="outlined"
              class="mb-3"
            />
            <v-text-field
              v-model.number="newWineData.year"
              label="Millésime"
              type="number"
              variant="outlined"
              class="mb-3"
            />
            <v-text-field
              v-model="newWineData.aopIgpVdf"
              label="AOP/IGP/VDF"
              variant="outlined"
              class="mb-3"
            />
            <v-text-field
              v-model.number="newWineData.prixLancement"
              label="Prix de lancement (€)"
              type="number"
              variant="outlined"
              prefix="€"
            />
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showNewWineDialog = false">Annuler</v-btn>
          <v-btn color="primary" variant="flat" @click="createNewWine" :disabled="!newWineData.name">
            Ajouter le vin
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped>
.cave-placement-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 16px;
}

.cave-placement-container {
  max-width: 1400px;
  margin: 0 auto;
}

.header-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

.selection-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
  height: fit-content;
}

.selection-title {
  background: linear-gradient(90deg, #8b4513, #a0522d);
  color: white;
  border-radius: 10px 10px 0 0;
}

.list-item {
  border-radius: 6px;
  margin: 2px 8px;
  transition: all 0.3s ease;
}

.list-item:hover {
  background: rgba(139, 69, 19, 0.1);
}

.list-item-active {
  background: rgba(139, 69, 19, 0.2);
  border-left: 3px solid #8b4513;
}

.item-name {
  font-weight: 600;
  color: #8b4513;
}

.item-details {
  font-size: 0.75rem;
}

.unit-detail-card,
.no-selection-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

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

.storage-grid-container {
  margin-top: 24px;
}

.storage-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
  max-width: 600px;
  margin: 0 auto;
}

.storage-space {
  aspect-ratio: 1;
  border: 2px solid #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.storage-space:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.space-available {
  background: linear-gradient(45deg, #f9f9f9, #e9e9e9);
  border-color: #ddd;
}

.space-available:hover {
  background: linear-gradient(45deg, #e9e9e9, #d9d9d9);
  border-color: #8b4513;
}

.space-occupied {
  background: linear-gradient(45deg, #98fb98, #32cd32);
  border-color: #228b22;
}

.space-occupied:hover {
  background: linear-gradient(45deg, #32cd32, #228b22);
}

.space-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 10px;
  font-weight: bold;
  color: #666;
}

.space-coordinates {
  font-size: 9px;
}

.remove-btn {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.storage-space:hover .remove-btn {
  opacity: 1;
}

.space-content {
  flex: 1;
  padding: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.wine-info {
  text-align: center;
}

.wine-name {
  font-size: 11px;
  font-weight: bold;
  color: white;
  margin-bottom: 2px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.wine-details {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 2px;
}

.wine-date {
  font-size: 8px;
  color: rgba(255, 255, 255, 0.8);
}

.space-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.empty-text {
  font-size: 10px;
  margin-top: 2px;
}

.wine-selection-section {
  margin-bottom: 16px;
}

.wine-list-container {
  border: 1px solid #ddd;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.wine-list {
  padding: 0;
}

.wine-item {
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.wine-item:hover {
  background: rgba(139, 69, 19, 0.1);
}

.wine-selected {
  background: rgba(76, 175, 80, 0.1);
  border-left: 3px solid #4caf50;
}

.placement-details-section {
  margin-top: 16px;
}

.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

/* Responsive Design */
@media (max-width: 960px) {
  .cave-placement-background {
    padding: 8px;
  }

  .storage-grid {
    grid-template-columns: repeat(4, 1fr);
    max-width: 400px;
  }
}

@media (max-width: 600px) {
  .cave-placement-background {
    padding: 4px;
  }

  .storage-grid {
    grid-template-columns: repeat(3, 1fr);
    max-width: 300px;
  }

  .storage-space {
    min-height: 60px;
  }

  .wine-name {
    font-size: 10px;
  }

  .wine-details {
    font-size: 8px;
  }
}
</style>