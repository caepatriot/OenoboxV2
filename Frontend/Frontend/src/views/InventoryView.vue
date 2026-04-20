<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const tabs = [
  { to: '/inventory/overview', label: 'Overview', icon: 'mdi-view-dashboard-outline' },
  { to: '/inventory/intake', label: 'Intake', icon: 'mdi-truck-delivery-outline' },
  { to: '/inventory/lots', label: 'Lots', icon: 'mdi-format-list-bulleted-square' },
]

const active = computed(() => route.path)
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Inventory</p>
        <h1 class="oeno-title">Intake and stock dispatch</h1>
        <p class="oeno-subtitle">Receive wines into intake lots, keep stock unassigned, then dispatch bottles to cellar when ready.</p>
      </div>
    </div>

    <v-card class="oeno-card mb-6" variant="flat">
      <v-card-text class="pa-3">
        <v-tabs color="#7a2838" :model-value="active" align-tabs="start">
          <v-tab
            v-for="tab in tabs"
            :key="tab.to"
            :value="tab.to"
            :to="tab.to"
            class="text-none"
          >
            <v-icon start size="18">{{ tab.icon }}</v-icon>
            {{ tab.label }}
          </v-tab>
        </v-tabs>
      </v-card-text>
    </v-card>

    <RouterView />
  </section>
</template>
