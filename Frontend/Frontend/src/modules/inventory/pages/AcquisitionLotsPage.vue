<script setup>
import { onMounted } from 'vue'
import { useInventoryStore } from '@/modules/inventory/store/inventory.store'
import { useInventoryFilters } from '@/modules/inventory/composables/useInventoryFilters'

const inventoryStore = useInventoryStore()
const { query, filtered } = useInventoryFilters(inventoryStore.lots)

onMounted(async () => {
  await inventoryStore.fetchLots()
})
</script>

<template>
  <v-card class="oeno-card" variant="flat">
    <v-card-text class="pa-6">
      <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
        <div>
          <p class="oeno-card-label mb-1">Lots</p>
          <h2 class="oeno-card-title">Acquisition lot register</h2>
        </div>
        <RouterLink to="/inventory/intake" class="oeno-link-chip">
          <v-icon size="18">mdi-plus</v-icon>
          New intake
        </RouterLink>
      </div>

      <v-text-field
        v-model="query"
        placeholder="Filter by source, producer, label, vintage..."
        prepend-inner-icon="mdi-magnify"
        variant="solo-filled"
        hide-details
        class="mb-4"
      />

      <v-progress-linear v-if="inventoryStore.isLoading" indeterminate color="#7a2838" class="mb-4" />
      <v-alert v-if="inventoryStore.error" type="error" variant="tonal" class="mb-4">
        {{ inventoryStore.error }}
      </v-alert>

      <div v-if="filtered.length" class="oeno-list">
        <RouterLink
          v-for="lot in filtered"
          :key="lot.id"
          :to="`/inventory/lots/${lot.id}`"
          class="oeno-list-item text-decoration-none"
        >
          <div>
            <div class="oeno-strong">Lot #{{ lot.id }} · {{ lot.source || 'Unknown source' }}</div>
            <div class="oeno-muted mt-1">
              {{ lot.acquiredOn || 'No date' }} · {{ lot.items?.length || 0 }} item(s) · {{ lot.currency || 'EUR' }}
            </div>
          </div>
          <div class="text-right">
            <v-chip class="oeno-chip mb-1" variant="flat">{{ lot.totalAvailable || 0 }} unassigned</v-chip>
            <div class="oeno-muted">{{ lot.totalDispatched || 0 }} dispatched</div>
          </div>
        </RouterLink>
      </div>
      <p v-else class="oeno-muted">No lots found.</p>
    </v-card-text>
  </v-card>
</template>
