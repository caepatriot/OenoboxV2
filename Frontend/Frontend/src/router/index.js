import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import TastingView from '../views/TastingView.vue'
// import TastingStaticView from '../views/TastingStaticView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/tasting',
            name: 'tasting',
            component: TastingView
        },
        {
            path: '/cave-admin',
            name: 'cave-admin',
            component: () => import('../views/CaveAdminView.vue')
        },
        {
            path: '/cave-placement',
            name: 'cave-placement',
            component: () => import('../views/CavePlacementView.vue')
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('../views/AboutView.vue')
        }
    ]
})

export default router
