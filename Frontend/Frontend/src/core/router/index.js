import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import TastingView from '@/views/TastingView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/cellars',
      name: 'cellars',
      component: () => import('@/views/CellarsView.vue'),
    },
    {
      path: '/cellar',
      name: 'cellar-workspace',
      component: () => import('@/views/CellarWorkspaceView.vue'),
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: () => import('@/views/InventoryView.vue'),
    },
    {
      path: '/oli',
      name: 'oli',
      component: () => import('@/views/OliView.vue'),
    },
    {
      path: '/analytics',
      name: 'analytics',
      component: () => import('@/views/AnalyticsView.vue'),
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('@/views/SettingsView.vue'),
    },
    {
      path: '/tasting',
      name: 'tasting',
      component: TastingView,
    },
    {
      path: '/cave-admin',
      name: 'cave-admin',
      component: () => import('@/views/CaveAdminView.vue'),
    },
    {
      path: '/cave-placement',
      name: 'cave-placement',
      component: () => import('@/views/CavePlacementView.vue'),
    },
    {
      path: '/cave-test',
      redirect: '/cellar',
    },
    {
      path: '/about',
      redirect: '/analytics',
    },
  ],
})

export default router
