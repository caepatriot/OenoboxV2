<template>
  <section class="oeno-page">
    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <h1 class="oeno-card-title mb-2">Stored bottle detail moved</h1>
        <p class="oeno-muted mb-4">Bottle-level detail now comes from acquisition lot dispatch context.</p>
        <RouterLink to="/inventory/lots" class="oeno-link-chip">
          <v-icon size="18">mdi-arrow-right</v-icon>
          Open acquisition lots
        </RouterLink>
      </v-card-text>
    </v-card>
  </section>
</template>
