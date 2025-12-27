<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed, toRaw } from 'vue'
import { useTastingStore } from '@/stores/tasting.js'

const store = useTastingStore()

const testTaste = ref({})

const AROMA_CATEGORIES = [
  'fruitée',
  'florale',
  'boisée',
  'balsamique',
  'épicée',
  'empyreumatique',
  'animale',
  'végétale',
  'alimentaire',
  'minérale',
  'chimique',
  'anormale'
].map((s) => s.toLowerCase())

const selectedTasting = reactive({
  vin: {
    informations: {
      type: null,
      cepage: [],
      region: '',
      aop_igp_vdf: '',
      elevage: '',
      import: '',
      prix_lancement: 0,
      prix_actuel: 0
    },
    visuel: {
      robe_blanche: null,
      robe_rouge: null,
      robe_rose: null,
      disque: null,
      intensite: null,
      limpidite: null,
      brillance: null,
      evolution: null,
      remarques: ''
    },
    nez: {
      intensite: null,
      qualite: null,
      type_aromes: [],
      selectedCategories: [],
      nature_aromes: Array.from({ length: 12 }, () => ({})),
      description: ''
    },
    bouche: {
      attaque: '',
      evolution: '',
      fin_de_bouche: '',
      structure: '',
      texture: '',
      intensite: '',
      qualite: '',
      equilibre: '',
      aromes: [],
      persistance_aromatique: '',
      longueur_en_bouche: '',
      remarques: ''
    },
    longueur: {
      persistance_aromatique: 0,
      caudalies: 0,
      structure: ''
    },
    conclusion: {
      caudalies: 0,
      note_finale: {
        tres_mediocre: false,
        mediocre: false,
        mauvais: false,
        passable: false,
        correct: false,
        bon: false,
        tres_bon: false,
        superbe: false,
        excellent: false,
        exceptionnel: false,
        legendaire: false
      },
      conclusion: ''
    },
    autres: {
      temperature_ideale_de_consommation: 0,
      date_ideale_de_consommation: '',
      evolution_probable: '',
      accords_mets_vins: []
    }
  }
})

const isEditMode = ref(false)
const editingTastingId = ref(null)

const selected = ref({})
const selectedNotes = reactive({})

const myWines = reactive([])

const currentStep = ref(1)
const steps = computed(() => store.tasting_steps)

const tabImg = ref('one')
const drawer = ref(false)

onMounted(async () => {
  document.body.addEventListener('mousemove', handleMouseMove)

  try {
    await store.loadTastingSteps()
  } catch (error) {
    console.error('Failed to load tasting steps:', error)
  }

  try {
    const tastings = await store.getTastings()
    myWines.splice(0, myWines.length, ...(tastings || []))
  } catch (error) {
    console.error('Failed to load tastings:', error)
  }
})

onBeforeUnmount(() => {
  document.body.removeEventListener('mousemove', handleMouseMove)
})

const inferWineTypeFromOption = (opt) => {
  if (!opt) return null

  if (typeof opt === 'string') {
    const s = opt.toLowerCase()
    if (s.includes('rouge')) return 'red'
    if (s.includes('blanc')) return 'white'
    if (s.includes('rosé') || s.includes('rose')) return 'rose'
    return s
  }

  if (opt.wineType) return opt.wineType

  if (typeof opt.value === 'string') {
    const label = opt.value.toLowerCase()
    if (label.includes('rouge')) return 'red'
    if (label.includes('blanc')) return 'white'
    if (label.includes('rosé') || label.includes('rose')) return 'rose'
    return label
  }

  return null
}

const getSelectedWineTypeCode = () => {
  const typeField = toRaw(selectedTasting.vin.informations.type)
  if (!typeField) return null
  return inferWineTypeFromOption(typeField)
}

const asValueString = (v) => {
  if (!v) return ''
  if (typeof v === 'object') return v.value || ''
  return String(v)
}

const convertAromasNatureToMap = (aromasArray) => {
  const result = {}
  if (!Array.isArray(aromasArray)) return result

  aromasArray.forEach((bucket) => {
    if (!bucket || typeof bucket !== 'object') return
    Object.entries(bucket).forEach(([key, value]) => {
      if (Array.isArray(value) && value.length > 0) {
        result[key] = value
      }
    })
  })

  return result
}

const convertFormDataToApiFormat = (formData) => {
  const typeField = formData.vin.informations.type
  const wineType = inferWineTypeFromOption(typeField)

  const noteFinaleObj = formData.vin.conclusion?.note_finale || {}
  const noteKeys = Object.keys(noteFinaleObj)
  const activeNote = noteKeys.find((key) => !!noteFinaleObj[key]) || null

  const accordsArr = Array.isArray(formData.vin.autres?.accords_mets_vins)
      ? formData.vin.autres.accords_mets_vins
      : []
  const accordsMetsVins = accordsArr.join(', ')

  return {
    wineType,
    cepages: formData.vin.informations.cepage,
    region: formData.vin.informations.region,
    aopIgpVdf: formData.vin.informations.aop_igp_vdf,
    elevage: formData.vin.informations.elevage,
    wineImport: formData.vin.informations.import,
    prixLancement: formData.vin.informations.prix_lancement,
    prixActuel: formData.vin.informations.prix_actuel,

    robeRouge: asValueString(formData.vin.visuel.robe_rouge),
    robeBlanche: asValueString(formData.vin.visuel.robe_blanche),
    robeRose: asValueString(formData.vin.visuel.robe_rose),
    disque: formData.vin.visuel.disque,
    intensiteVisuelle: formData.vin.visuel.intensite,
    limpidite: formData.vin.visuel.limpidite,
    brillance: formData.vin.visuel.brillance,
    evolutionVisuelle: formData.vin.visuel.evolution,
    remarquesVisuelles: formData.vin.visuel.remarques,

    intensiteNez: formData.vin.nez.intensite,
    qualiteNez: formData.vin.nez.qualite,
    typeAromes: Array.isArray(formData.vin.nez.type_aromes) ? formData.vin.nez.type_aromes.join(', ') : '',
    descriptionNez: formData.vin.nez.description,
    aromesNature: convertAromasNatureToMap(formData.vin.nez.nature_aromes),

    attaque: formData.vin.bouche.attaque,
    evolutionBouche: formData.vin.bouche.evolution,
    structure: formData.vin.bouche.structure,
    texture: formData.vin.bouche.texture,

    persistanceAromatique: formData.vin.longueur.persistance_aromatique,
    caudaliesLongueur: formData.vin.longueur.caudalies,
    structureLongueur: formData.vin.longueur.structure,

    noteFinale: activeNote,
    caudaliesConclusion: formData.vin.conclusion.caudalies,
    remarquesConclusion: formData.vin.conclusion.conclusion,
    accordsMetsVins,

    temperatureIdeale: formData.vin.autres.temperature_ideale_de_consommation,
    dateIdealeConsommation: formData.vin.autres.date_ideale_de_consommation,
    evolutionProbable: formData.vin.autres.evolution_probable
  }
}

const submitForm = async () => {
  try {
    const raw = toRaw(selectedTasting)
    const apiData = convertFormDataToApiFormat(raw)

    let savedTasting

    if (isEditMode.value) {
      savedTasting = await store.updateTasting(editingTastingId.value, apiData)

      const index = myWines.findIndex((t) => t.id === editingTastingId.value)
      if (index > -1) {
        myWines[index] = savedTasting
      }

      alert('Dégustation mise à jour avec succès !')
    } else {
      savedTasting = await store.createTasting(apiData)
      myWines.push(savedTasting)
      alert('Dégustation enregistrée avec succès !')
    }

    resetForm()
  } catch (error) {
    console.error("Erreur lors de l'enregistrement:", error)
    alert("Erreur lors de l'enregistrement de la dégustation. Veuillez réessayer.")
  }
}

const getCategoryIndex = (category) => {
  if (!category) return -1
  const key = category.toString().toLowerCase()
  return AROMA_CATEGORIES.indexOf(key)
}

const showForm = ref(false)
const newTitle = ref('')
const items = ref([])
const titleRules = [(v) => !!v || 'Title is required']

const toggleForm = () => {
  showForm.value = !showForm.value
}

const confirmNewItem = () => {
  if (newTitle.value) {
    items.value.push(newTitle.value)
    newTitle.value = ''
    showForm.value = false
  }
}

const cancelNewItem = () => {
  newTitle.value = ''
  showForm.value = false
}

const cancelEdit = () => {
  resetForm()
}

const getTastingTitle = (tasting) => {
  const cepages = tasting?.cepages?.length ? tasting.cepages.join(', ') : 'Vin'
  return `${cepages} - ${tasting?.region || 'Région inconnue'}`
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const editTasting = async (tasting) => {
  try {
    const fullTasting = await store.getTastingById(tasting.id)
    populateFormWithTastingData(fullTasting)

    isEditMode.value = true
    editingTastingId.value = tasting.id
    showForm.value = true
    currentStep.value = 1
  } catch (error) {
    console.error('Failed to load tasting for editing:', error)
    alert('Erreur lors du chargement de la dégustation pour modification.')
  }
}

const deleteTasting = async (tastingId) => {
  if (!confirm('Êtes-vous sûr de vouloir supprimer cette dégustation ?')) return

  try {
    await store.deleteTasting(tastingId)

    const index = myWines.findIndex((t) => t.id === tastingId)
    if (index > -1) {
      myWines.splice(index, 1)
    }

    if (editingTastingId.value === tastingId) {
      resetForm()
    }
  } catch (error) {
    console.error('Failed to delete tasting:', error)
    alert('Erreur lors de la suppression de la dégustation.')
  }
}

const populateFormWithTastingData = (tasting) => {
  const wineTypeCode = tasting?.wineType || null
  let typeLabel = null
  if (wineTypeCode === 'red') typeLabel = 'Rouge'
  else if (wineTypeCode === 'white') typeLabel = 'Blanc'
  else if (wineTypeCode === 'rose') typeLabel = 'Rosé'

  selectedTasting.vin.informations.type = wineTypeCode ? { value: typeLabel || wineTypeCode, wineType: wineTypeCode } : null
  selectedTasting.vin.informations.cepage = tasting?.cepages || []
  selectedTasting.vin.informations.region = tasting?.region || ''
  selectedTasting.vin.informations.aop_igp_vdf = tasting?.aopIgpVdf || ''
  selectedTasting.vin.informations.elevage = tasting?.elevage || ''
  selectedTasting.vin.informations.import = tasting?.wineImport || ''
  selectedTasting.vin.informations.prix_lancement = tasting?.prixLancement || 0
  selectedTasting.vin.informations.prix_actuel = tasting?.prixActuel || 0

  selectedTasting.vin.visuel.robe_rouge = tasting?.robeRouge || null
  selectedTasting.vin.visuel.robe_blanche = tasting?.robeBlanche || null
  selectedTasting.vin.visuel.robe_rose = tasting?.robeRose || null
  selectedTasting.vin.visuel.disque = tasting?.disque || null
  selectedTasting.vin.visuel.intensite = tasting?.intensiteVisuelle || null
  selectedTasting.vin.visuel.limpidite = tasting?.limpidite || null
  selectedTasting.vin.visuel.brillance = tasting?.brillance || null
  selectedTasting.vin.visuel.evolution = tasting?.evolutionVisuelle || null
  selectedTasting.vin.visuel.remarques = tasting?.remarquesVisuelles || ''

  selectedTasting.vin.nez.intensite = tasting?.intensiteNez || null
  selectedTasting.vin.nez.qualite = tasting?.qualiteNez || null
  selectedTasting.vin.nez.type_aromes = tasting?.typeAromes
      ? tasting.typeAromes.split(',').map((s) => s.trim()).filter(Boolean)
      : []
  selectedTasting.vin.nez.description = tasting?.descriptionNez || ''

  selectedTasting.vin.nez.nature_aromes = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}]
  selectedTasting.vin.nez.selectedCategories = []

  if (tasting?.aromesNature) {
    Object.entries(tasting.aromesNature).forEach(([category, notes]) => {
      const idx = getCategoryIndex(category)
      if (idx !== -1) {
        if (!selectedTasting.vin.nez.nature_aromes[idx]) {
          selectedTasting.vin.nez.nature_aromes[idx] = {}
        }
        selectedTasting.vin.nez.nature_aromes[idx][category.toLowerCase()] = notes || []
      }
    })
  }

  selectedTasting.vin.bouche.attaque = tasting?.attaque || ''
  selectedTasting.vin.bouche.evolution = tasting?.evolutionBouche || ''
  selectedTasting.vin.bouche.structure = tasting?.structure || ''
  selectedTasting.vin.bouche.texture = tasting?.texture || ''

  selectedTasting.vin.longueur.persistance_aromatique = tasting?.persistanceAromatique || 0
  selectedTasting.vin.longueur.caudalies = tasting?.caudaliesLongueur || 0
  selectedTasting.vin.longueur.structure = tasting?.structureLongueur || ''

  selectedTasting.vin.conclusion.caudalies = tasting?.caudaliesConclusion || 0

  let noteFinaleObj = {
    tres_mediocre: false,
    mediocre: false,
    mauvais: false,
    passable: false,
    correct: false,
    bon: false,
    tres_bon: false,
    superbe: false,
    excellent: false,
    exceptionnel: false,
    legendaire: false
  }

  if (tasting?.noteFinale && typeof tasting.noteFinale === 'string') {
    const key = tasting.noteFinale.toLowerCase().replace(/\s+/g, '_')
    if (Object.prototype.hasOwnProperty.call(noteFinaleObj, key)) {
      noteFinaleObj[key] = true
    }
  } else if (tasting?.noteFinale && typeof tasting.noteFinale === 'object') {
    noteFinaleObj = { ...noteFinaleObj, ...tasting.noteFinale }
  }

  selectedTasting.vin.conclusion.note_finale = noteFinaleObj
  selectedTasting.vin.conclusion.conclusion = tasting?.remarquesConclusion || ''

  selectedTasting.vin.autres.temperature_ideale_de_consommation = tasting?.temperatureIdeale || 0
  selectedTasting.vin.autres.date_ideale_de_consommation = tasting?.dateIdealeConsommation || ''
  selectedTasting.vin.autres.evolution_probable = tasting?.evolutionProbable || ''
  selectedTasting.vin.autres.accords_mets_vins = tasting?.accordsMetsVins
      ? tasting.accordsMetsVins.split(',').map((s) => s.trim()).filter(Boolean)
      : []
}

const resetForm = () => {
  isEditMode.value = false
  editingTastingId.value = null
  showForm.value = false
  newTitle.value = ''
  currentStep.value = 1

  Object.assign(selectedTasting, {
    vin: {
      informations: {
        type: null,
        cepage: [],
        region: '',
        aop_igp_vdf: '',
        elevage: '',
        import: '',
        prix_lancement: 0,
        prix_actuel: 0
      },
      visuel: {
        robe_blanche: null,
        robe_rouge: null,
        robe_rose: null,
        disque: null,
        intensite: null,
        limpidite: null,
        brillance: null,
        evolution: null,
        remarques: ''
      },
      nez: {
        intensite: null,
        qualite: null,
        type_aromes: [],
        selectedCategories: [],
        nature_aromes: [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}],
        description: ''
      },
      bouche: {
        attaque: '',
        evolution: '',
        fin_de_bouche: '',
        structure: '',
        texture: '',
        intensite: '',
        qualite: '',
        equilibre: '',
        aromes: [],
        persistance_aromatique: '',
        longueur_en_bouche: '',
        remarques: ''
      },
      longueur: {
        persistance_aromatique: 0,
        caudalies: 0,
        structure: ''
      },
      conclusion: {
        caudalies: 0,
        note_finale: {
          tres_mediocre: false,
          mediocre: false,
          mauvais: false,
          passable: false,
          correct: false,
          bon: false,
          tres_bon: false,
          superbe: false,
          excellent: false,
          exceptionnel: false,
          legendaire: false
        },
        conclusion: ''
      },
      autres: {
        temperature_ideale_de_consommation: 0,
        date_ideale_de_consommation: '',
        evolution_probable: '',
        accords_mets_vins: []
      }
    }
  })
}

const handleMouseMove = (event) => {
  if (event.clientX <= 20) {
    drawer.value = true
  }
}

const handleMouseLeave = () => {
  drawer.value = false
}

const isSelected = (val, groupId) => {
  return selected.value[groupId]?.some((item) => item.id === val.id) || false
}

const updateSelectedNotes = (group, val, selectedValues) => {
  if (!selectedTasting.vin.nez.nature_aromes[val.id]) {
    selectedTasting.vin.nez.nature_aromes[val.id] = []
  }

  if (selectedValues.length > 0) {
    selectedTasting.vin.nez.nature_aromes[val.id] = selectedValues
  } else {
    selectedTasting.vin.nez.nature_aromes[val.id] = []
  }
}

const test = (...args) => {
  testTaste.value = args
}

const filteredWineTypeValues = (itemsArg = []) => {
  const selectedWineType = getSelectedWineTypeCode()
  if (!selectedWineType) return itemsArg

  return itemsArg.filter((item) => {
    const wineType = toRaw(item?.wineType)
    if (!wineType) return true
    if (Array.isArray(wineType)) return wineType.includes(selectedWineType)
    return wineType === selectedWineType
  })
}

const getStepIcon = (stepName) => {
  const icons = {
    informations: 'mdi-information',
    visuel: 'mdi-eye',
    nez: 'mdi-nose',
    bouche: 'mdi-mouth',
    longueur: 'mdi-timer',
    conclusion: 'mdi-check-circle'
  }
  return icons[stepName] || 'mdi-circle'
}

const getFieldIcon = (fieldType) => {
  const icons = {
    text: 'mdi-text',
    textarea: 'mdi-text-long',
    autocomplete: 'mdi-magnify',
    select: 'mdi-menu-down',
    number: 'mdi-numeric',
    slider: 'mdi-tune',
    'select-button': 'mdi-format-list-bulleted'
  }
  return icons[fieldType] || 'mdi-circle'
}

const getCheckboxColor = (val) => {
  if (val?.color) return val.color
  if (val?.negatif) return 'error'
  return 'wine-primary'
}

const getSliderLabel = (field, modelValue) => {
  if (field?.name === 'persistance_aromatique') {
    const labels = ['nulle', 'courte', 'moyenne', 'bonne', 'longue', 'très longue', 'infinie']
    return labels[Math.min(Math.floor((modelValue || 0) / 14.28), 6)] || 'moyenne'
  }
  return modelValue
}

const ensureAromaBucket = (stepName, val, index) => {
  const stepObj = selectedTasting.vin[stepName]
  if (!stepObj || !Array.isArray(stepObj.nature_aromes)) return

  const aromas = stepObj.nature_aromes
  const idx = typeof index === 'number' ? index : getCategoryIndex(val?.value)
  if (idx === -1) return

  const key = (val?.value || '').toLowerCase()
  if (!aromas[idx]) aromas[idx] = {}
  if (!Array.isArray(aromas[idx][key])) aromas[idx][key] = []
}

const getTypeAromesGroups = (field) => {
  if (!field?.groups || !Array.isArray(field.groups)) return []
  return field.groups.map((g, index) => ({
    id: g.id ?? index + 1,
    required: !!g.required,
    options: g.groupValues || []
  }))
}

const onTypeAromeToggle = (group, value, checked) => {
  const current = Array.isArray(selectedTasting.vin.nez.type_aromes)
      ? [...selectedTasting.vin.nez.type_aromes]
      : []

  const groupValues = (group?.options || []).map((o) => o.value)
  let next = current.filter((v) => !groupValues.includes(v))

  if (checked) next.push(value)
  selectedTasting.vin.nez.type_aromes = next
}

const getFieldValues = (field) => {
  if (Array.isArray(field?.values) && field.values.length) return field.values
  if (Array.isArray(field?.groups) && field.groups.length && Array.isArray(field.groups[0]?.groupValues)) {
    return field.groups[0].groupValues
  }
  return []
}
</script>

<template>
  <div class="tasting-background">
    <v-navigation-drawer
        v-model="drawer"
        temporary
        @mouseleave="handleMouseLeave"
        class="wine-drawer"
    >
      <div class="d-flex flex-column justify-center pa-4">
        <v-btn
            v-if="!showForm"
            class="ma-2 wine-btn-primary"
            @click="toggleForm"
            elevation="2"
        >
          <v-icon class="mr-2">mdi-plus</v-icon>
          Nouvelle dégustation
        </v-btn>

        <v-card v-if="showForm" class="ma-2 wine-card" elevation="4">
          <v-card-title class="wine-card-title">
            <v-icon class="mr-2">mdi-file-document-plus</v-icon>
            Nouvelle fiche
          </v-card-title>
          <v-card-text>
            <v-form>
              <v-text-field
                  variant="outlined"
                  label="Titre de la dégustation"
                  v-model="newTitle"
                  :rules="titleRules"
                  required
                  class="wine-text-field"
                  prepend-inner-icon="mdi-file-document-edit"
              />
            </v-form>
          </v-card-text>
          <v-card-actions class="pa-4">
            <v-btn
                @click="confirmNewItem"
                class="wine-btn-primary"
                :disabled="!newTitle"
                elevation="2"
            >
              <v-icon class="mr-2">mdi-check</v-icon>
              Confirmer
            </v-btn>
            <v-btn
                @click="cancelNewItem"
                variant="outlined"
                class="wine-btn-secondary"
            >
              <v-icon class="mr-2">mdi-close</v-icon>
              Annuler
            </v-btn>
          </v-card-actions>
        </v-card>

        <v-list class="wine-list">
          <v-list-item
              v-for="(tasting, index) in myWines"
              :key="tasting.id || index"
              @click="editTasting(tasting)"
              class="wine-list-item"
              :class="{ 'wine-list-item-active': editingTastingId === tasting.id }"
          >
            <template #prepend>
              <v-icon class="mr-3">mdi-wine</v-icon>
            </template>

            <v-list-item-title class="wine-title">
              {{ getTastingTitle(tasting) }}
            </v-list-item-title>

            <v-list-item-subtitle class="wine-subtitle">
              {{ tasting.wineType }} • {{ tasting.region }} • {{ tasting.noteFinale }}
            </v-list-item-subtitle>

            <v-list-item-subtitle class="wine-date">
              {{ formatDate(tasting.createdAt) }}
            </v-list-item-subtitle>

            <template #append>
              <v-btn
                  icon
                  variant="text"
                  size="small"
                  @click.stop="deleteTasting(tasting.id)"
              >
                <v-icon color="error">mdi-delete</v-icon>
              </v-btn>
            </template>
          </v-list-item>
        </v-list>
      </div>
    </v-navigation-drawer>

    <v-container width="100%" fluid class="tasting-container">
      <v-row class="tasting-row" no-gutters>
        <v-col cols="12" lg="4" class="image-section">
          <v-card class="wine-card image-card" elevation="8">
            <v-tabs v-model="tabImg" class="wine-tabs" bg-color="rgba(139, 69, 19, 0.1)">
              <v-tab value="one" class="wine-tab">
                <v-icon class="mr-2">mdi-bottle-wine</v-icon>
                <span class="d-none d-sm-inline">Bouteille</span>
              </v-tab>
              <v-tab value="two" class="wine-tab">
                <v-icon class="mr-2">mdi-glass-wine</v-icon>
                <span class="d-none d-sm-inline">Verre</span>
              </v-tab>
            </v-tabs>

            <v-card-text class="pa-0">
              <v-tabs-window v-model="tabImg">
                <v-tabs-window-item class="relative image-window" value="one">
                  <v-img
                      width="100%"
                      :height="$vuetify.display.xs ? '250px' : '100%'"
                      cover
                      src="https://cuisinedecheffe.com/87427-large_default/vin-rouge-bordeaux-le-bedat-aoc-hve-bouteille-750ml.jpg"
                      class="wine-image"
                  >
                    <div class="image-overlay"></div>
                  </v-img>
                  <v-btn
                      class="position-absolute bottom-0 right-0 ma-2 wine-btn-camera"
                      icon="mdi-camera-outline"
                      :size="$vuetify.display.xs ? 'default' : 'large'"
                      elevation="4"
                  />
                </v-tabs-window-item>

                <v-tabs-window-item class="relative image-window" value="two">
                  <v-img
                      width="100%"
                      :height="$vuetify.display.xs ? '250px' : '100%'"
                      cover
                      src="https://lesraisinsdelajoie.fr/214-large_default/4-verres-a-bordeaux.jpg"
                      class="wine-image"
                  >
                    <div class="image-overlay"></div>
                  </v-img>
                  <v-btn
                      class="position-absolute bottom-0 right-0 ma-2 wine-btn-camera"
                      icon="mdi-camera-outline"
                      :size="$vuetify.display.xs ? 'default' : 'large'"
                      elevation="4"
                  />
                </v-tabs-window-item>
              </v-tabs-window>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" class="form-section">
          <v-card class="wine-card form-card" elevation="8">
            <v-stepper
                v-model="currentStep"
                :items="steps"
                show-actions
                class="wine-stepper"
                elevation="0"
            >
              <template
                  v-for="step in steps"
                  :key="step.step"
                  v-slot:[`item.${step.step}`]
              >
                <div class="step-content">
                  <div class="step-header">
                    <v-icon class="step-icon mr-2" :icon="getStepIcon(step.name)" />
                    <h2 class="step-title">{{ step.title }}</h2>
                  </div>

                  <div class="step-scroll-container">
                    <v-container class="step-container">
                      <v-row>
                        <v-col class="d-flex flex-column ga-3">
                          <template v-if="step.fields">
                            <template v-for="field in step.fields" :key="field.id">
                              <v-card
                                  class="field-card wine-card"
                                  elevation="2"
                                  :class="{ 'aroma-field': field.name === 'nature_aromes' }"
                              >
                                <v-card-title class="field-title pa-2">
                                  <v-icon class="mr-2" size="small" :icon="getFieldIcon(field.type)" />
                                  <span class="field-label">{{ field.label }}</span>
                                </v-card-title>

                                <v-card-text class="pa-3">
                                  <template v-if="field.type === 'text'">
                                    <v-text-field
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        class="wine-text-field"
                                        prepend-inner-icon="mdi-text"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-else-if="field.type === 'textarea'">
                                    <v-textarea
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        variant="outlined"
                                        density="comfortable"
                                        class="wine-textarea"
                                        prepend-inner-icon="mdi-text-long"
                                        :rows="$vuetify.display.xs ? 2 : 3"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template
                                      v-else-if="(field.type === 'autocomplete' || field.type === 'select-button')
                                      && field.name !== 'type_aromes'
                                      && field.name !== 'nature_aromes'"
                                  >
                                    <v-autocomplete
                                        :label="field.label"
                                        density="comfortable"
                                        chips
                                        return-object
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :items="filteredWineTypeValues(getFieldValues(field))"
                                        item-title="value"
                                        item-value="value"
                                        variant="outlined"
                                        :multiple="field.multi"
                                        class="wine-autocomplete"
                                        prepend-inner-icon="mdi-magnify"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-else-if="field.type === 'select'">
                                    <v-select
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        :items="field.values"
                                        class="wine-select"
                                        prepend-inner-icon="mdi-menu-down"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-else-if="field.type === 'number'">
                                    <v-text-field
                                        density="comfortable"
                                        variant="outlined"
                                        v-model="selectedTasting.vin[step.name][field.name]"
                                        :label="field.label"
                                        prefix="€"
                                        type="number"
                                        class="wine-text-field"
                                        prepend-inner-icon="mdi-currency-eur"
                                        :hide-details="$vuetify.display.xs"
                                    />
                                  </template>

                                  <template v-else-if="field.name === 'type_aromes'">
                                    <div class="type-aromes-groups">
                                      <div
                                          v-for="group in getTypeAromesGroups(field)"
                                          :key="group.id"
                                          class="type-aromes-group mb-3"
                                      >
                                        <div class="d-flex align-center mb-1">
                                          <span class="text-body-2 font-weight-medium">
                                            Groupe {{ group.id }}
                                          </span>
                                          <v-chip
                                              v-if="group.required"
                                              size="x-small"
                                              class="ml-2"
                                              color="error"
                                              label
                                          >
                                            obligatoire
                                          </v-chip>
                                        </div>

                                        <div class="checkbox-group">
                                          <v-checkbox
                                              v-for="opt in group.options"
                                              :key="opt.id"
                                              class="wine-checkbox mb-2"
                                              :label="opt.value"
                                              :true-icon="opt.icon || 'mdi-checkbox-marked'"
                                              :color="getCheckboxColor(opt)"
                                              :model-value="selectedTasting.vin.nez.type_aromes.includes(opt.value)"
                                              @update:model-value="checked => onTypeAromeToggle(group, opt.value, checked)"
                                              density="comfortable"
                                              hide-details
                                              :class="{ 'checkbox-mobile': $vuetify.display.xs }"
                                          />
                                        </div>
                                      </div>
                                    </div>
                                  </template>

                                  <template v-else-if="field.name === 'nature_aromes' && field.groups">
                                    <div class="checkbox-group">
                                      <template
                                          v-for="(val, index2) in field.groups[0].groupValues"
                                          :key="val.id"
                                      >
                                        <v-checkbox
                                            :value="val"
                                            class="wine-checkbox mb-2"
                                            :color="getCheckboxColor(val)"
                                            :true-icon="val.icon ? val.icon : 'mdi-checkbox-marked'"
                                            v-model="selectedTasting.vin[step.name].selectedCategories"
                                            :multiple="field.groups[0].multi"
                                            :label="val.value"
                                            density="comfortable"
                                            hide-details
                                            :class="{ 'checkbox-mobile': $vuetify.display.xs }"
                                            @update:model-value="checked => { if (checked) ensureAromaBucket(step.name, val, index2) }"
                                        />

                                        <v-expand-transition>
                                          <v-autocomplete
                                              v-if="
                                              val.notes &&
                                              Array.isArray(selectedTasting.vin[step.name].selectedCategories) &&
                                              selectedTasting.vin[step.name].selectedCategories.includes(val) &&
                                              selectedTasting.vin[step.name].nature_aromes[index2]
                                            "
                                              :label="`Notes pour ${val.value}`"
                                              density="comfortable"
                                              chips
                                              :items="val.notes"
                                              v-model="selectedTasting.vin[step.name].nature_aromes[index2][val.value.toLowerCase()]"
                                              item-title="libelle"
                                              item-value="libelle"
                                              variant="outlined"
                                              multiple
                                              class="wine-autocomplete aroma-select"
                                              prepend-inner-icon="mdi-fruit-grapes"
                                              :hide-details="$vuetify.display.xs"
                                          />
                                        </v-expand-transition>
                                      </template>
                                    </div>
                                  </template>

                                  <template v-else-if="field.type === 'slider'">
                                    <div class="slider-container">
                                      <v-slider
                                          v-model="selectedTasting.vin[step.name][field.name]"
                                          thumb-label="always"
                                          class="wine-slider"
                                          color="wine-primary"
                                      >
                                        <template #thumb-label="{ modelValue }">
                                          {{ getSliderLabel(field, modelValue) }}
                                        </template>
                                      </v-slider>
                                    </div>
                                  </template>
                                </v-card-text>
                              </v-card>
                            </template>
                          </template>
                        </v-col>
                      </v-row>
                    </v-container>
                  </div>
                </div>
              </template>
            </v-stepper>

            <v-card-actions class="pa-4 justify-center">
              <v-btn
                  v-if="isEditMode"
                  @click="cancelEdit"
                  variant="outlined"
                  class="wine-btn-secondary mr-2"
                  :size="$vuetify.display.xs ? 'default' : 'large'"
                  elevation="2"
              >
                <v-icon class="mr-2">mdi-close</v-icon>
                <span class="d-none d-sm-inline">Annuler</span>
                <span class="d-sm-none">Annuler</span>
              </v-btn>

              <v-btn
                  @click="submitForm"
                  class="wine-btn-primary submit-btn"
                  :size="$vuetify.display.xs ? 'default' : 'large'"
                  elevation="4"
                  block
              >
                <v-icon class="mr-2">
                  {{ isEditMode ? 'mdi-content-save-edit' : 'mdi-content-save' }}
                </v-icon>
                <span class="d-none d-sm-inline">
                  {{ isEditMode ? 'Mettre à jour' : 'Enregistrer la dégustation' }}
                </span>
                <span class="d-sm-none">
                  {{ isEditMode ? 'Mettre à jour' : 'Enregistrer' }}
                </span>
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.tasting-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 8px;
  display: flex;
  flex-direction: column;
}

.wine-drawer {
  background: linear-gradient(180deg, #8b4513 0%, #654321 100%);
  color: white;
}

.wine-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
  box-shadow: 0 4px 20px rgba(139, 69, 19, 0.1);
  transition: all 0.3s ease;
}

.wine-card-title {
  background: linear-gradient(90deg, #8b4513, #a0522d);
  color: white;
  border-radius: 10px 10px 0 0;
}

.wine-btn-primary {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  color: white;
  border-radius: 20px;
  text-transform: none;
  font-weight: 600;
  transition: all 0.3s ease;
  min-height: 44px;
}

.wine-btn-secondary {
  border-radius: 20px;
  text-transform: none;
  font-weight: 600;
  border-color: #8b4513;
  color: #8b4513;
}

.wine-list-item {
  border-radius: 6px;
  margin: 2px 0;
  transition: all 0.3s ease;
}

.wine-list-item-active {
  background: rgba(255, 255, 255, 0.2);
}

.tasting-container {
  flex: 1;
  max-width: 100%;
  margin: 0 auto;
  height: calc(100vh - 16px);
  display: flex;
  flex-direction: column;
}

.tasting-row {
  flex: 1;
  gap: 8px;
  height: 100%;
}

.image-section {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.image-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.wine-tabs {
  border-radius: 10px 10px 0 0;
  flex-shrink: 0;
}

.wine-tab {
  text-transform: none;
  font-weight: 600;
  min-height: 40px;
}

.wine-tab .v-tab__content {
  font-size: 0.875rem;
}

.image-window {
  flex: 1;
  border-radius: 0 0 10px 10px;
  overflow: hidden;
  position: relative;
}

.wine-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(139, 69, 19, 0.1), transparent);
  pointer-events: none;
}

.wine-btn-camera {
  background: rgba(255, 255, 255, 0.9);
  color: #8b4513;
  border-radius: 50%;
  min-width: 40px;
  width: 40px;
  height: 40px;
}

.form-section {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.form-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.wine-stepper {
  background: transparent;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.step-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(139, 69, 19, 0.2);
  flex-shrink: 0;
}

.step-icon {
  color: #8b4513;
  font-size: 24px;
  flex-shrink: 0;
}

.step-title {
  font-size: 20px;
  font-weight: 700;
  color: #8b4513;
  margin: 0 0 0 8px;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
  flex: 1;
}

.step-scroll-container {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.step-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.step-scroll-container::-webkit-scrollbar-track {
  background: rgba(139, 69, 19, 0.1);
  border-radius: 3px;
}

.step-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(139, 69, 19, 0.3);
  border-radius: 3px;
}

.step-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(139, 69, 19, 0.5);
}

.step-container {
  padding: 0;
}

.field-card {
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.field-title {
  background: linear-gradient(90deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  color: #8b4513;
  font-weight: 600;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 6px 6px 0 0;
  display: flex;
  align-items: center;
}

.field-label {
  font-size: 14px;
  line-height: 1.2;
}

.aroma-field {
  border-left: 3px solid #8b4513;
}

.wine-text-field,
.wine-textarea,
.wine-autocomplete,
.wine-select {
  border-radius: 6px;
}

.wine-text-field :deep(.v-field) {
  border-radius: 6px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.wine-checkbox {
  border-radius: 6px;
  transition: all 0.3s ease;
  margin: 0;
}

.checkbox-mobile {
  width: 100%;
  margin-bottom: 8px;
}

.aroma-select {
  margin-top: 8px;
  margin-left: 16px;
  border-left: 2px solid rgba(139, 69, 19, 0.3);
  padding-left: 8px;
}

.slider-container {
  padding: 12px 0;
}

.wine-slider {
  --v-slider-track-color: rgba(139, 69, 19, 0.2);
  --v-slider-track-filled-color: #8b4513;
}

.submit-btn {
  min-width: 200px;
  height: 48px;
  font-size: 14px;
  text-transform: none;
  font-weight: 700;
  margin: 0 auto;
}

@media (min-width: 1264px) {
  .tasting-background {
    padding: 16px;
  }

  .tasting-container {
    height: calc(100vh - 32px);
  }

  .tasting-row {
    gap: 16px;
  }

  .step-content {
    padding: 24px;
  }

  .step-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
  }

  .step-icon {
    font-size: 32px;
  }

  .step-title {
    font-size: 28px;
    margin-left: 12px;
  }

  .field-card {
    margin-bottom: 16px;
  }

  .field-title {
    padding: 12px 16px;
    font-size: 16px;
  }

  .field-label {
    font-size: 16px;
  }

  .checkbox-group {
    gap: 8px;
  }

  .wine-checkbox {
    margin: 0 4px 4px 0;
  }

  .aroma-select {
    margin-left: 24px;
    padding-left: 12px;
  }

  .submit-btn {
    min-width: 250px;
    height: 56px;
    font-size: 16px;
  }
}

@media (max-width: 600px) {
  .tasting-background {
    padding: 4px;
  }

  .tasting-container {
    height: 100vh;
  }

  .tasting-row {
    gap: 4px;
  }

  .step-content {
    padding: 12px;
  }

  .step-header {
    margin-bottom: 12px;
    padding-bottom: 8px;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .step-icon {
    font-size: 20px;
  }

  .step-title {
    font-size: 18px;
    margin: 0;
  }

  .checkbox-group {
    flex-direction: column;
    gap: 4px;
  }

  .wine-checkbox {
    width: 100%;
    margin: 0 0 4px 0;
  }

  .aroma-select {
    margin-left: 0;
    border-left: none;
    padding-left: 0;
  }

  .field-card {
    margin-bottom: 8px;
  }

  .field-title {
    padding: 6px 8px;
    font-size: 13px;
  }

  .field-label {
    font-size: 13px;
  }

  .wine-btn-camera {
    min-width: 36px;
    width: 36px;
    height: 36px;
  }
}
</style>
