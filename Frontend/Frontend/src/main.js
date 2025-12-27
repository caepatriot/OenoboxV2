import './assets/main.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'

// Vuetify
import {aliases, mdi} from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const app = createApp(App)

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light',
        themes: {
            light: {
                colors: {
                    'wine-primary': '#8b4513',
                    'wine-secondary': '#a0522d',
                    'wine-accent': '#654321',
                }
            }
        }
    },
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        },
    }
})

app.use(vuetify)
app.use(createPinia())
app.use(router)

app.mount('#app')
