<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()

const navItems = [
  { to: '/', label: 'Home', icon: 'mdi-home-variant-outline', match: '/' },
  { to: '/cellars', label: 'Cellars', icon: 'mdi-view-grid-outline', match: '/cellars' },
  { to: '/inventory', label: 'Inventory', icon: 'mdi-bottle-wine-outline', match: '/inventory' },
  { to: '/oli', label: 'Oli', icon: 'mdi-sparkles', match: '/oli' },
  { to: '/analytics', label: 'Analytics', icon: 'mdi-chart-box-outline', match: '/analytics' },
]

const pageTitle = computed(() => {
  const labels = {
    '/': 'Dashboard',
    '/cellars': 'My cellars',
    '/inventory': 'Inventory',
    '/oli': 'Oli assistant',
    '/analytics': 'Analytics',
    '/settings': 'Settings',
    '/tasting': 'Tasting',
    '/cave-admin': 'Cellar layout',
    '/cave-placement': 'Bottle placement',
    '/cave-test': 'Workspace',
  }

  const entry = Object.entries(labels).find(([path]) => route.path === path || route.path.startsWith(`${path}/`))
  return entry?.[1] || 'Oenobox'
})

const isLegacyRoute = computed(() => ['/cave-admin', '/cave-placement', '/cave-test', '/tasting'].some((path) => route.path.startsWith(path)))

function isActive(item) {
  return item.to === '/'
    ? route.path === '/'
    : route.path === item.to || route.path.startsWith(`${item.to}/`)
}
</script>

<template>
  <v-app class="oeno-shell">
    <div class="oeno-app-bg"></div>

    <header class="oeno-topbar">
      <div class="oeno-topbar__inner">
        <RouterLink to="/" class="oeno-brand">
          <div class="oeno-brand__mark">
            <v-icon size="22">mdi-glass-wine</v-icon>
          </div>
          <div>
            <div class="oeno-brand__name">Oenobox</div>
            <div class="oeno-brand__meta">{{ pageTitle }}</div>
          </div>
        </RouterLink>

        <div class="d-flex align-center ga-2 flex-wrap justify-end">
          <RouterLink v-if="!isLegacyRoute" to="/tasting" class="oeno-link-chip">
            <v-icon size="18">mdi-glass-wine</v-icon>
            Tasting
          </RouterLink>
          <RouterLink to="/settings" class="oeno-icon-link">
            <v-icon size="22">mdi-cog-outline</v-icon>
          </RouterLink>
        </div>
      </div>
    </header>

    <main class="oeno-main">
      <RouterView />
    </main>

    <nav class="oeno-bottom-nav">
      <div class="oeno-bottom-nav__inner">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="oeno-nav-item"
          :class="{ 'is-active': isActive(item) }"
        >
          <v-icon size="22">{{ item.icon }}</v-icon>
          <span>{{ item.label }}</span>
        </RouterLink>
      </div>
    </nav>
  </v-app>
</template>
