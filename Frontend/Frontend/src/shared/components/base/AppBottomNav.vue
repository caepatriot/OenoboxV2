<script setup>
import { useRoute } from 'vue-router'

defineProps({
  items: {
    type: Array,
    default: () => [],
  },
})

const route = useRoute()

function isActive(path) {
  return path === '/'
    ? route.path === '/'
    : route.path === path || route.path.startsWith(`${path}/`)
}
</script>

<template>
  <nav class="oeno-bottom-nav">
    <div class="oeno-bottom-nav__inner">
      <RouterLink
        v-for="item in items"
        :key="item.to"
        :to="item.to"
        class="oeno-nav-item"
        :class="{ 'is-active': isActive(item.to) }"
      >
        <v-icon size="22">{{ item.icon }}</v-icon>
        <span>{{ item.label }}</span>
      </RouterLink>
    </div>
  </nav>
</template>
