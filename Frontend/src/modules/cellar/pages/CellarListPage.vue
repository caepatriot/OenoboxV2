<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>
            <h1>My Cellars</h1>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="showCreateDialog = true">
              <v-icon left>mdi-plus</v-icon>
              New Cellar
            </v-btn>
          </v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="caves"
              :loading="loading"
              item-key="id"
              class="elevation-1"
            >
              <template v-slot:item.actions="{ item }">
                <v-btn icon small @click="editCave(item)">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon small @click="deleteCave(item)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
                <v-btn icon small :to="{ name: 'cellar-workspace', params: { cellarId: item.id } }">
                  <v-icon>mdi-open-in-new</v-icon>
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Create/Edit Dialog -->
    <v-dialog v-model="showCreateDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editingCave ? 'Edit Cellar' : 'Create New Cellar' }}
        </v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="caveForm.name"
              label="Name"
              :rules="[v => !!v || 'Name is required']"
              required
            ></v-text-field>
            <v-textarea
              v-model="caveForm.description"
              label="Description"
            ></v-textarea>
            <v-row>
              <v-col cols="4">
                <v-text-field
                  v-model="caveForm.dimensions.width"
                  label="Width (m)"
                  type="number"
                  step="0.1"
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  v-model="caveForm.dimensions.height"
                  label="Height (m)"
                  type="number"
                  step="0.1"
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  v-model="caveForm.dimensions.depth"
                  label="Depth (m)"
                  type="number"
                  step="0.1"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="6">
                <v-text-field
                  v-model="caveForm.temperature"
                  label="Temperature (°C)"
                  type="number"
                  step="0.1"
                ></v-text-field>
              </v-col>
              <v-col cols="6">
                <v-text-field
                  v-model="caveForm.humidity"
                  label="Humidity (%)"
                  type="number"
                  step="0.1"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" @click="saveCave" :loading="saving">
            {{ editingCave ? 'Update' : 'Create' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation -->
    <v-dialog v-model="showDeleteDialog" max-width="400px">
      <v-card>
        <v-card-title>Confirm Delete</v-card-title>
        <v-card-text>
          Are you sure you want to delete "{{ caveToDelete?.name }}"?
          This action cannot be undone.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="showDeleteDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="confirmDelete" :loading="deleting">
            Delete
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useCellarStore } from '@/modules/cellar/store/cellar.store'

const cellarStore = useCellarStore()

const caves = computed(() => cellarStore.caves)
const loading = computed(() => cellarStore.loading)
const error = computed(() => cellarStore.error)

const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Description', key: 'description' },
  { title: 'Temperature', key: 'temperature' },
  { title: 'Humidity', key: 'humidity' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const showCreateDialog = ref(false)
const editingCave = ref(null)
const valid = ref(false)
const saving = ref(false)
const showDeleteDialog = ref(false)
const deleting = ref(false)
const caveToDelete = ref(null)

const caveForm = ref({
  name: '',
  description: '',
  dimensions: {
    width: null,
    height: null,
    depth: null
  },
  temperature: null,
  humidity: null
})

onMounted(() => {
  cellarStore.fetchCaves()
})

const editCave = (cave) => {
  editingCave.value = cave
  caveForm.value = {
    name: cave.name,
    description: cave.description,
    dimensions: { ...cave.dimensions },
    temperature: cave.temperature,
    humidity: cave.humidity
  }
  showCreateDialog.value = true
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingCave.value = null
  caveForm.value = {
    name: '',
    description: '',
    dimensions: { width: null, height: null, depth: null },
    temperature: null,
    humidity: null
  }
}

const saveCave = async () => {
  if (!valid.value) return

  saving.value = true
  try {
    const caveData = {
      ...caveForm.value,
      dimensions: caveForm.value.dimensions.width ? caveForm.value.dimensions : null
    }

    if (editingCave.value) {
      await cellarStore.updateCave(editingCave.value.id, caveData)
    } else {
      await cellarStore.createCave(caveData)
    }
    closeDialog()
  } finally {
    saving.value = false
  }
}

const deleteCave = (cave) => {
  caveToDelete.value = cave
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  deleting.value = true
  try {
    await cellarStore.deleteCave(caveToDelete.value.id)
    showDeleteDialog.value = false
    caveToDelete.value = null
  } finally {
    deleting.value = false
  }
}
</script>