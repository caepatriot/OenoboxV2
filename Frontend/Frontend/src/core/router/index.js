import { createRouter, createWebHistory } from 'vue-router'
import { registerGuards } from './guards'

import DashboardPage from '@/modules/dashboard/pages/DashboardPage.vue'
import CellarListPage from '@/modules/cellar/pages/CellarListPage.vue'
import CellarWorkspacePage from '@/modules/cellar/pages/CellarWorkspacePage.vue'
import CellarAnalyticsPage from '@/modules/cellar/pages/CellarAnalyticsPage.vue'
import BottleLocatorPage from '@/modules/cellar/pages/BottleLocatorPage.vue'
import InventoryOverviewPage from '@/modules/inventory/pages/InventoryOverviewPage.vue'
import AcquisitionLotsPage from '@/modules/inventory/pages/AcquisitionLotsPage.vue'
import StoredBottleDetailPage from '@/modules/inventory/pages/StoredBottleDetailPage.vue'
import WineListPage from '@/modules/winecatalog/pages/WineListPage.vue'
import WineDetailPage from '@/modules/winecatalog/pages/WineDetailPage.vue'
import WineEditPage from '@/modules/winecatalog/pages/WineEditPage.vue'
import TastingNotebookPage from '@/modules/tasting/pages/TastingNotebookPage.vue'
import NewTastingPage from '@/modules/tasting/pages/NewTastingPage.vue'
import TastingDetailPage from '@/modules/tasting/pages/TastingDetailPage.vue'
import AssistantPage from '@/modules/assistant/pages/AssistantPage.vue'
import ImportsPage from '@/modules/imports/pages/ImportsPage.vue'
import AdminPage from '@/modules/admin/pages/AdminPage.vue'

const routes = [
  { path: '/', redirect: '/app' },
  {
    path: '/app',
    children: [
      { path: '', redirect: '/app/dashboard' },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardPage,
        meta: { title: 'Dashboard', mainNav: { label: 'Home', icon: 'mdi-home-variant-outline', order: 1 } },
      },
      {
        path: 'cellars',
        name: 'cellars',
        component: CellarListPage,
        meta: { title: 'Cellars', mainNav: { label: 'Cellars', icon: 'mdi-view-grid-outline', order: 2 } },
      },
      { path: 'cellars/:cellarId', redirect: (to) => `/app/cellars/${to.params.cellarId}/workspace` },
      { path: 'cellars/:cellarId/workspace', name: 'cellar-workspace', component: CellarWorkspacePage, meta: { title: 'Cellar Workspace' } },
      { path: 'cellars/:cellarId/analytics', name: 'cellar-analytics', component: CellarAnalyticsPage, meta: { title: 'Cellar Analytics' } },
      { path: 'cellars/:cellarId/find', name: 'cellar-find', component: BottleLocatorPage, meta: { title: 'Bottle Locator' } },
      {
        path: 'inventory',
        name: 'inventory',
        component: InventoryOverviewPage,
        meta: { title: 'Inventory', mainNav: { label: 'Inventory', icon: 'mdi-bottle-wine-outline', order: 3 } },
      },
      { path: 'inventory/lots', name: 'inventory-lots', component: AcquisitionLotsPage, meta: { title: 'Lots' } },
      { path: 'inventory/bottles/:storedBottleId', name: 'inventory-bottle-detail', component: StoredBottleDetailPage, meta: { title: 'Stored Bottle' } },
      {
        path: 'wines',
        name: 'wines',
        component: WineListPage,
        meta: { title: 'Wines', mainNav: { label: 'Wines', icon: 'mdi-glass-wine', order: 4 } },
      },
      { path: 'wines/new', name: 'wine-new', component: WineEditPage, meta: { title: 'Add Wine' } },
      { path: 'wines/:wineId', name: 'wine-detail', component: WineDetailPage, meta: { title: 'Wine Detail' } },
      { path: 'wines/:wineId/edit', name: 'wine-edit', component: WineEditPage, meta: { title: 'Edit Wine' } },
      { path: 'wine-vintages/:wineVintageId', name: 'wine-vintage-detail', component: WineDetailPage, meta: { title: 'Wine Vintage' } },
      {
        path: 'tastings',
        name: 'tastings',
        component: TastingNotebookPage,
        meta: { title: 'Tastings', mainNav: { label: 'Tastings', icon: 'mdi-notebook-outline', order: 5 } },
      },
      { path: 'tastings/new', name: 'new-tasting', component: NewTastingPage, meta: { title: 'New Tasting' } },
      { path: 'tastings/:tastingId', name: 'tasting-detail', component: TastingDetailPage, meta: { title: 'Tasting Detail' } },
      {
        path: 'assistant',
        name: 'assistant',
        component: AssistantPage,
        meta: { title: 'Assistant', mainNav: { label: 'Oli', icon: 'mdi-sparkles', order: 6 } },
      },
      { path: 'imports', name: 'imports', component: ImportsPage, meta: { title: 'Imports' } },
      { path: 'admin', name: 'admin', component: AdminPage, meta: { title: 'Admin' } },
    ],
  },
  { path: '/cellars', redirect: '/app/cellars' },
  { path: '/inventory', redirect: '/app/inventory' },
  { path: '/tasting', redirect: '/app/tastings' },
  { path: '/oli', redirect: '/app/assistant' },
  { path: '/analytics', redirect: '/app/dashboard' },
  { path: '/settings', redirect: '/app/admin' },
  { path: '/cave-admin', redirect: '/app/cellars/1/workspace' },
  { path: '/cave-placement', redirect: '/app/cellars/1/find' },
  { path: '/cave-test', redirect: '/app/cellars/1/workspace' },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

registerGuards(router)

export default router

