import {ref, computed} from 'vue'
import {defineStore} from 'pinia'
import { tastingApi } from '@/services/api'

export const useTastingStore = defineStore('tasting', () => {

    // Reactive state for API-loaded data
    const tastingSteps = ref([])
    const isLoading = ref(false)
    const error = ref(null)

    // Load tasting steps from API
    const loadTastingSteps = async () => {
        try {
            isLoading.value = true
            error.value = null

            // Load from API
            const response = await tastingApi.getSteps()
            tastingSteps.value = response.data
        } catch (apiErr) {
            console.error('Failed to load steps from API:', apiErr.message)
            error.value = apiErr.message
            throw apiErr
        } finally {
            isLoading.value = false
        }
    }
    // const myTastings = [
    //     {
    //         "vin": {
    //             "cepage": [],
    //             "region": "",
    //             "aop_igp_vdf": "",
    //             "elevage": "",
    //             "import": "",
    //             "prix_lancement": 0,
    //             "prix_actuel": 0,
    //             "aspect_visuel": {
    //                 "robe": {
    //                     "couleur": "",
    //                     "disque": "",
    //                     "intensité": {},
    //                     "limpidité": {},
    //                     "brillance": {},
    //                     "evolution": {},
    //                     "remarques": ""
    //
    //                 },
    //                 "teinte": "",
    //                 "disque": "",
    //                 "intensite": "",
    //                 "limpidite": "",
    //                 "brillance": "",
    //                 "evolution": "",
    //                 "remarques": ""
    //             },
    //             "nez": {
    //                 "intensite": "",
    //                 "qualite": "",
    //                 "aromes": {
    //                     "type": "",
    //                     "nature": []
    //                 },
    //                 "description": ""
    //             },
    //             "bouche": {
    //                 "attaque": "",
    //                 "evolution": "",
    //                 "fin_de_bouche": "",
    //                 "structure": "",
    //                 "texture": "",
    //                 "intensite": "",
    //                 "qualite": "",
    //                 "equilibre": "",
    //                 "aromes": [],
    //                 "longueur_en_bouche": "",
    //                 "persistance_aromatique": "",
    //                 "remarques": ""
    //             },
    //             "notes": {
    //                 "caudalies": 0,
    //                 "note_finale": {
    //                     "tres_mediocre": false,
    //                     "mediocre": false,
    //                     "mauvais": false,
    //                     "passable": false,
    //                     "correct": false,
    //                     "bon": false,
    //                     "tres_bon": false,
    //                     "superbe": false,
    //                     "excellent": false,
    //                     "exceptionnel": false,
    //                     "legendaire": false
    //                 },
    //                 "conclusion": ""
    //             },
    //             "temperature_ideale_de_consommation": 0,
    //             "date_ideale_de_consommation": "",
    //             "evolution_probable": "",
    //             "accords_mets_vins": []
    //         }
    //     }
    // ]

    // const doubleCount = computed(() => count.value * 2)

    function increment() {
        // count.value++
    }

    // API integration functions
    const createTasting = async (tastingData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await tastingApi.create(tastingData)
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const getTastings = async () => {
        try {
            isLoading.value = true
            error.value = null
            const response = await tastingApi.getAll()
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const getTastingById = async (id) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await tastingApi.getById(id)
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const updateTasting = async (id, tastingData) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await tastingApi.update(id, tastingData)
            return response.data
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const deleteTasting = async (id) => {
        try {
            isLoading.value = true
            error.value = null
            await tastingApi.delete(id)
        } catch (err) {
            error.value = err.message
            throw err
        } finally {
            isLoading.value = false
        }
    }

    return {
        tastingSteps,
        tasting_steps: computed(() => tastingSteps.value),
        isLoading,
        error,
        loadTastingSteps,
        createTasting,
        getTastings,
        getTastingById,
        updateTasting,
        deleteTasting,
        increment
    }
})
