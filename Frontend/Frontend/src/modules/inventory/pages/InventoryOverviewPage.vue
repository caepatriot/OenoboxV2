<script setup>
import { computed, onMounted } from 'vue'
import { useInventoryStore } from '@/modules/inventory/store/inventory.store'

const inventoryStore = useInventoryStore()

onMounted(async () => {
  if (!inventoryStore.lots.length) {
    await inventoryStore.fetchLots()
  }
})

const latestLots = computed(() => inventoryStore.lots.slice(0, 5))
</script>

<template>
  <div class="oeno-stack">
    <div class="oeno-card-grid">
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Received</p>
          <div class="oeno-metric">{{ inventoryStore.totals.received }}</div>
          <p class="oeno-muted mt-2">Total bottles received through intake lots.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Unassigned</p>
          <div class="oeno-metric">{{ inventoryStore.totals.available }}</div>
          <p class="oeno-muted mt-2">Still in inventory intake and not dispatched.</p>
        </v-card-text>
      </v-card>
      <v-card class="oeno-card" variant="flat">
        <v-card-text class="pa-5">
          <p class="oeno-card-label mb-2">Dispatched</p>
          <div class="oeno-metric">{{ inventoryStore.totals.dispatched }}</div>
          <p class="oeno-muted mt-2">Moved from intake to cellar-ready stock.</p>
        </v-card-text>
      </v-card>
    </div>

    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
          <div>
            <p class="oeno-card-label mb-1">Recent activity</p>
            <h2 class="oeno-card-title">Latest acquisition lots</h2>
          </div>
          <RouterLink to="/inventory/lots" class="oeno-link-chip">
            <v-icon size="18">mdi-arrow-right</v-icon>
            Open lots
          </RouterLink>
        </div>

        <div v-if="inventoryStore.isLoading" class="oeno-muted">Loading lots...</div>
        <v-alert v-else-if="inventoryStore.error" type="error" variant="tonal" class="mb-2">
          {{ inventoryStore.error }}
        </v-alert>
        <div v-else-if="latestLots.length" class="oeno-list">
          <RouterLink
            v-for="lot in latestLots"
            :key="lot.id"
            :to="`/inventory/lots/${lot.id}`"
            class="oeno-list-item text-decoration-none"
          >
            <div>
              <div class="oeno-strong">Lot #{{ lot.id }} · {{ lot.source || 'Unknown source' }}</div>
              <div class="oeno-muted mt-1">{{ lot.acquiredOn || 'No date' }} · {{ lot.items?.length || 0 }} item(s)</div>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ lot.totalAvailable || 0 }} unassigned</v-chip>
          </RouterLink>
        </div>
        <p v-else class="oeno-muted">No acquisition lot yet. Use the intake tab to create your first lot.</p>
      </v-card-text>
    </v-card>
  </div>
</template>
