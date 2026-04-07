<script setup>
const groups = [
  {
    title: 'Account',
    icon: 'mdi-account-circle-outline',
    items: [
      ['Profile', 'Claude Ribeiro'],
      ['Workspace style', 'Wine cellar concept'],
      ['Plan', 'Prototype'],
    ],
  },
  {
    title: 'Preferences',
    icon: 'mdi-tune-variant',
    items: [
      ['Temperature unit', 'Celsius'],
      ['Currency', 'EUR'],
      ['Language', 'French / English'],
    ],
  },
  {
    title: 'Notifications',
    icon: 'mdi-bell-ring-outline',
    items: [
      ['Cellar alerts', 'Enabled'],
      ['Inventory reminders', 'Enabled'],
      ['Oli suggestions', 'Daily'],
    ],
  },
]
</script>

<template>
  <section class="oeno-page">
    <div class="oeno-page__heading">
      <div>
        <p class="oeno-eyebrow">Settings</p>
        <h1 class="oeno-title">Preferences</h1>
        <p class="oeno-subtitle">A simple settings screen added so the whole app feels consistent with the target design system.</p>
      </div>
    </div>

    <div class="oeno-stack">
      <v-card v-for="group in groups" :key="group.title" class="oeno-card" variant="flat">
        <v-card-text class="pa-6">
          <div class="d-flex align-center ga-3 mb-4">
            <div class="oeno-icon-badge oeno-icon-badge--soft">
              <v-icon size="24">{{ group.icon }}</v-icon>
            </div>
            <h2 class="oeno-card-title">{{ group.title }}</h2>
          </div>
          <div class="oeno-list">
            <div v-for="item in group.items" :key="item[0]" class="oeno-list-item">
              <div class="oeno-strong">{{ item[0] }}</div>
              <div class="oeno-muted">{{ item[1] }}</div>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </section>
</template>
