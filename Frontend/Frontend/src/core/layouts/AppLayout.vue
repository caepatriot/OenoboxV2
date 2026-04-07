<script setup>
import { computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import AppBottomNav from '@/shared/components/base/AppBottomNav.vue'
import AppTopBar from '@/shared/components/base/AppTopBar.vue'

const route = useRoute()
const router = useRouter()

const mainNavItems = computed(() => {
  return router
    .getRoutes()
    .filter((record) => Boolean(record.meta?.mainNav && record.path && !record.redirect))
    .map((record) => ({
      to: record.path,
      label: record.meta.mainNav.label,
      icon: record.meta.mainNav.icon,
      order: record.meta.mainNav.order ?? 0,
    }))
    .sort((first, second) => first.order - second.order)
})

const pageTitle = computed(() => {
  const matchingRecord = [...route.matched]
    .reverse()
    .find((record) => typeof record.meta?.title === 'string')

  return matchingRecord?.meta.title || 'Oenobox'
})

const showTastingShortcut = computed(() => !route.matched.some((record) => Boolean(record.meta?.legacyWorkspace)))
</script>

<template>
  <v-app class="oeno-shell">
    <div class="oeno-app-bg"></div>

    <AppTopBar :page-title="pageTitle" :show-tasting-shortcut="showTastingShortcut" />

    <main class="oeno-main">
      <RouterView />
    </main>

    <AppBottomNav :items="mainNavItems" />
  </v-app>
</template>

