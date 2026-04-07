<script setup>
defineProps({
  pageTitle: {
    type: String,
    default: 'Oenobox',
  },
  showTastingShortcut: {
    type: Boolean,
    default: true,
  },
})
</script>

<template>
  <header class="oeno-topbar">
    <div class="oeno-topbar__inner">
      <RouterLink to="/app/dashboard" class="oeno-brand">
        <div class="oeno-brand__mark">
          <v-icon size="22">mdi-glass-wine</v-icon>
        </div>
        <div>
          <div class="oeno-brand__name">Oenobox</div>
          <div class="oeno-brand__meta">{{ pageTitle }}</div>
        </div>
      </RouterLink>

      <div class="d-flex align-center ga-2 flex-wrap justify-end">
        <RouterLink v-if="showTastingShortcut" to="/app/tastings" class="oeno-link-chip">
          <v-icon size="18">mdi-glass-wine</v-icon>
          Tasting
        </RouterLink>
        <RouterLink to="/app/admin" class="oeno-icon-link" aria-label="Open settings">
          <v-icon size="22">mdi-cog-outline</v-icon>
        </RouterLink>
      </div>
    </div>
  </header>
</template>

