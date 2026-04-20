<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { useInventoryStore } from '@/modules/inventory/store/inventory.store'

const route = useRoute()
const inventoryStore = useInventoryStore()

const lotId = computed(() => Number(route.params.lotId))
const dispatchForms = reactive({})

const lot = computed(() => inventoryStore.selectedLot)

onMounted(async () => {
  if (Number.isFinite(lotId.value)) {
    const loaded = await inventoryStore.fetchLotById(lotId.value)
    for (const item of loaded?.items || []) {
      dispatchForms[item.id] = 1
    }
  }
})

const dispatchItem = async (item) => {
  const quantity = Number(dispatchForms[item.id] || 0)
  if (!quantity || quantity <= 0) return
  const updated = await inventoryStore.dispatchLotItem(lotId.value, {
    lotItemId: item.id,
    quantity,
    notes: 'Dispatched from lot detail',
  })
  for (const refreshedItem of updated?.items || []) {
    dispatchForms[refreshedItem.id] = Math.min(1, Number(refreshedItem.quantityAvailable || 0)) || 1
  }
}
</script>

<template>
  <v-card class="oeno-card" variant="flat">
    <v-card-text class="pa-6">
      <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
        <div>
          <p class="oeno-card-label mb-1">Lot detail</p>
          <h2 class="oeno-card-title">Acquisition lot #{{ lot?.id || lotId }}</h2>
          <p class="oeno-muted mt-1">
            {{ lot?.source || 'Unknown source' }} · {{ lot?.acquiredOn || 'No date' }}
          </p>
        </div>
        <RouterLink to="/inventory/lots" class="oeno-link-chip">
          <v-icon size="18">mdi-arrow-left</v-icon>
          Back to lots
        </RouterLink>
      </div>

      <v-progress-linear v-if="inventoryStore.isLoading" indeterminate color="#7a2838" class="mb-4" />
      <v-alert v-if="inventoryStore.error" type="error" variant="tonal" class="mb-4">
        {{ inventoryStore.error }}
      </v-alert>

      <div v-if="lot?.items?.length" class="oeno-list">
        <div v-for="item in lot.items" :key="item.id" class="oeno-list-item">
          <div>
            <div class="oeno-strong">
              {{ item.wineLabel }}<span v-if="item.vintageYear"> {{ item.vintageYear }}</span>
            </div>
            <div class="oeno-muted mt-1">
              {{ item.producer || 'Unknown producer' }} · {{ item.bottleFormat || 'Format n/a' }}
            </div>
            <div class="oeno-muted mt-1">
              Received {{ item.quantityReceived || 0 }} · Unassigned {{ item.quantityAvailable || 0 }} · Dispatched {{ item.quantityDispatched || 0 }}
            </div>
          </div>

          <div class="d-flex ga-2 align-center">
            <v-text-field
              v-model.number="dispatchForms[item.id]"
              type="number"
              min="1"
              :max="item.quantityAvailable || 1"
              label="Qty"
              variant="outlined"
              hide-details
              style="max-width: 120px;"
            />
            <v-btn
              class="oeno-btn oeno-btn--primary"
              :disabled="(item.quantityAvailable || 0) <= 0 || Number(dispatchForms[item.id] || 0) <= 0 || Number(dispatchForms[item.id] || 0) > Number(item.quantityAvailable || 0)"
              :loading="inventoryStore.isSaving"
              @click="dispatchItem(item)"
            >
              Dispatch
            </v-btn>
          </div>
        </div>
      </div>
      <p v-else class="oeno-muted">No lot items found.</p>
    </v-card-text>
  </v-card>
</template>
