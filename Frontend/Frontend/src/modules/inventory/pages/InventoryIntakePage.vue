<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useInventoryStore } from '@/modules/inventory/store/inventory.store'

const router = useRouter()
const inventoryStore = useInventoryStore()

const lookupQuery = ref('')
const selectedCandidate = ref(null)
const createdLot = ref(null)
const dispatchQuantity = ref(1)
const ocrText = ref('')
const ocrLanguage = ref('fre')
const selectedImageFile = ref(null)
const imagePreviewUrl = ref('')

const form = reactive({
  source: '',
  acquiredOn: new Date().toISOString().slice(0, 10),
  quantity: 1,
  bottleFormat: '',
  unitPrice: null,
  currency: 'EUR',
  notes: '',
  createStoredBottles: true,
})

const hasVintage = computed(() => Number.isFinite(Number(selectedCandidate.value?.wineVintageId)))
const canCreateLot = computed(() => hasVintage.value && Number(form.quantity) > 0)
const ocrResult = computed(() => inventoryStore.ocrScanResult)

const submissionPayload = computed(() => ({
  rawLabelText: (ocrResult.value?.rawText || lookupQuery.value || '').trim(),
  sourceType: ocrResult.value?.provider || 'manual',
  confidenceScore: Number(selectedCandidate.value?.confidence || 0.3),
  submitter: 'inventory-intake-ui',
  proposedDataJson: JSON.stringify({
    label: lookupQuery.value.trim(),
    ocrText: ocrResult.value?.rawText || null,
    source: 'inventory-intake',
  }),
}))

watch(lookupQuery, async (value) => {
  const query = String(value || '').trim()
  selectedCandidate.value = null
  createdLot.value = null
  if (query.length < 2) {
    inventoryStore.intakeResults = []
    return
  }
  await inventoryStore.lookupIntake(query)
})

const chooseCandidate = (candidate) => {
  selectedCandidate.value = candidate
}

const submitCandidate = async () => {
  await inventoryStore.submitCatalogContribution(submissionPayload.value)
}

const runOcrLookup = async () => {
  await inventoryStore.lookupByOcrText(ocrText.value)
  hydrateQueryFromOcr()
}

const createLot = async () => {
  if (!canCreateLot.value) return
  const payload = {
    source: form.source || null,
    acquiredOn: form.acquiredOn || null,
    currency: form.currency || 'EUR',
    notes: form.notes || null,
    createStoredBottles: Boolean(form.createStoredBottles),
    items: [
      {
        wineVintageId: selectedCandidate.value.wineVintageId,
        quantityReceived: Number(form.quantity),
        unitPrice: form.unitPrice === null || form.unitPrice === '' ? null : Number(form.unitPrice),
        currency: form.currency || 'EUR',
        bottleFormat: form.bottleFormat || null,
        notes: form.notes || null,
      },
    ],
  }

  createdLot.value = await inventoryStore.createLot(payload)
  dispatchQuantity.value = Math.min(Number(form.quantity) || 1, 1)
}

const dispatchNow = async () => {
  if (!createdLot.value?.id || !createdLot.value?.items?.length) return
  const firstItem = createdLot.value.items[0]
  await inventoryStore.dispatchLotItem(createdLot.value.id, {
    lotItemId: firstItem.id,
    quantity: Number(dispatchQuantity.value),
    notes: 'Immediate dispatch from intake wizard',
  })
  createdLot.value = inventoryStore.selectedLot
}

const openLot = async () => {
  if (!createdLot.value?.id) return
  await router.push(`/inventory/lots/${createdLot.value.id}`)
}

const hydrateQueryFromOcr = () => {
  if (ocrResult.value?.suggestedQuery) {
    lookupQuery.value = ocrResult.value.suggestedQuery
  }
}

const onPickImage = async (value) => {
  const file = Array.isArray(value) ? value[0] : value
  if (!file) return
  selectedImageFile.value = file
  imagePreviewUrl.value = URL.createObjectURL(file)
  await scanImage()
}

const scanImage = async () => {
  if (!selectedImageFile.value) return
  await inventoryStore.scanOcrImage(selectedImageFile.value, { language: ocrLanguage.value })
  ocrText.value = ocrResult.value?.rawText || ''
  hydrateQueryFromOcr()
}

const clearImage = () => {
  selectedImageFile.value = null
  if (imagePreviewUrl.value) {
    URL.revokeObjectURL(imagePreviewUrl.value)
  }
  imagePreviewUrl.value = ''
  ocrText.value = ''
  inventoryStore.clearOcrScan()
}
</script>

<template>
  <div class="oeno-stack">
    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <div class="d-flex flex-wrap justify-space-between ga-4 align-start mb-4">
          <div>
            <p class="oeno-card-label mb-1">Step 1</p>
            <h2 class="oeno-card-title mb-2">Scan or identify wine</h2>
            <p class="oeno-muted mb-0">Upload a wine label photo, let OCR extract the text, then confirm the best catalog match.</p>
          </div>
          <v-chip class="oeno-chip" variant="flat">OCR + intake</v-chip>
        </div>

        <v-alert v-if="inventoryStore.error" type="error" variant="tonal" class="mb-4">
          {{ inventoryStore.error }}
        </v-alert>

        <div class="oeno-two-col align-start mb-4">
          <div>
            <v-file-input
              label="Wine label image"
              accept="image/*"
              prepend-icon="mdi-camera-outline"
              variant="outlined"
              show-size
              @update:model-value="onPickImage"
            />
            <div class="d-flex ga-2 mt-2 flex-wrap">
              <v-select
                v-model="ocrLanguage"
                :items="[
                  { title: 'French', value: 'fre' },
                  { title: 'English', value: 'eng' },
                  { title: 'Portuguese', value: 'por' },
                  { title: 'German', value: 'ger' },
                ]"
                item-title="title"
                item-value="value"
                label="OCR language"
                variant="outlined"
                style="max-width: 220px"
                hide-details
              />
              <v-btn class="oeno-btn oeno-btn--primary" :disabled="!selectedImageFile" :loading="inventoryStore.isLoading" @click="scanImage">
                Scan label
              </v-btn>
              <v-btn variant="outlined" :disabled="!selectedImageFile" @click="clearImage">
                Reset
              </v-btn>
            </div>
          </div>

          <div class="oeno-upload-preview" v-if="imagePreviewUrl">
            <img :src="imagePreviewUrl" alt="Wine label preview" class="oeno-upload-preview__image" />
          </div>
        </div>

        <div class="oeno-two-col align-start">
          <div>
            <v-text-field
              v-model="lookupQuery"
              label="Quick search"
              placeholder="Type producer, label, or appellation"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              hide-details
            />

            <div class="d-flex ga-2 mt-3">
              <v-textarea
                v-model="ocrText"
                label="OCR extracted text"
                placeholder="Paste label text from another OCR tool or edit the scan result"
                prepend-inner-icon="mdi-text-box-search-outline"
                variant="outlined"
                auto-grow
                rows="4"
                hide-details
              />
            </div>

            <div class="d-flex ga-2 mt-3 flex-wrap">
              <v-btn class="oeno-btn oeno-btn--primary" :loading="inventoryStore.isLoading" @click="runOcrLookup">
                Parse text
              </v-btn>
              <v-btn variant="outlined" :disabled="!ocrResult?.suggestedQuery" @click="hydrateQueryFromOcr">
                Use suggested query
              </v-btn>
            </div>
          </div>

          <v-card v-if="ocrResult" class="oeno-card oeno-card--soft" variant="flat">
            <v-card-text class="pa-4">
              <div class="d-flex justify-space-between align-center mb-3 ga-3 flex-wrap">
                <h3 class="text-subtitle-1 font-weight-medium mb-0">OCR extraction</h3>
                <v-chip size="small" variant="flat">{{ ocrResult.provider || 'manual' }}</v-chip>
              </div>

              <div class="d-flex flex-wrap ga-2 mb-3" v-if="ocrResult.extractedFields?.length">
                <v-chip
                  v-for="field in ocrResult.extractedFields"
                  :key="`${field.field}-${field.value}`"
                  class="oeno-chip"
                  variant="outlined"
                >
                  {{ field.field }}: {{ field.value }}
                </v-chip>
              </div>

              <div v-if="ocrResult.lines?.length" class="oeno-lines-preview">
                <div class="oeno-muted text-caption mb-2">Detected lines</div>
                <div class="oeno-lines-preview__list">
                  <span v-for="(line, index) in ocrResult.lines.slice(0, 8)" :key="`${index}-${line}`" class="oeno-lines-preview__line">
                    {{ line }}
                  </span>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </div>

        <div v-if="inventoryStore.intakeResults.length" class="oeno-list mt-4">
          <button
            v-for="candidate in inventoryStore.intakeResults"
            :key="`${candidate.sourceReference}-${candidate.wineVintageId || 'no-vintage'}`"
            type="button"
            class="oeno-list-item w-100 text-left"
            :class="{ 'oeno-list-item--soft': selectedCandidate?.sourceReference === candidate.sourceReference && selectedCandidate?.wineVintageId === candidate.wineVintageId }"
            @click="chooseCandidate(candidate)"
          >
            <div>
              <div class="oeno-strong">{{ candidate.label }}</div>
              <div class="oeno-muted mt-1">
                {{ [candidate.producer, candidate.appellation, candidate.country].filter(Boolean).join(' · ') || 'No extra details' }}
              </div>
            </div>
            <v-chip class="oeno-chip" variant="flat">{{ Math.round(Number(candidate.confidence || 0) * 100) }}%</v-chip>
          </button>
        </div>

        <div v-else-if="lookupQuery.trim().length >= 2 || ocrText.trim().length >= 2" class="mt-4">
          <p class="oeno-muted mb-3">No strong match found in local catalog.</p>
          <v-btn variant="outlined" @click="submitCandidate" :loading="inventoryStore.isSaving">
            Submit as catalog candidate
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <v-card class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <p class="oeno-card-label mb-1">Step 2</p>
        <h2 class="oeno-card-title mb-4">Create intake lot</h2>
        <p class="oeno-muted mb-4" v-if="selectedCandidate">
          Selected: <strong>{{ selectedCandidate.label }}</strong>
        </p>
        <v-alert v-if="selectedCandidate && !hasVintage" type="warning" variant="tonal" class="mb-4">
          This match has no wine vintage id yet. Pick a candidate with a vintage or complete it in catalog first.
        </v-alert>

        <div class="oeno-two-col">
          <v-text-field v-model="form.source" label="Merchant / source" variant="outlined" />
          <v-text-field v-model="form.acquiredOn" label="Acquired on" type="date" variant="outlined" />
          <v-text-field v-model.number="form.quantity" label="Quantity" type="number" min="1" variant="outlined" />
          <v-text-field v-model="form.bottleFormat" label="Bottle format" variant="outlined" />
          <v-text-field v-model.number="form.unitPrice" label="Unit price" type="number" min="0" step="0.01" variant="outlined" />
          <v-text-field v-model="form.currency" label="Currency" variant="outlined" />
        </div>
        <v-textarea v-model="form.notes" label="Notes" variant="outlined" rows="3" auto-grow />
        <v-switch v-model="form.createStoredBottles" label="Create stored bottles immediately (unassigned)" color="#7a2838" />

        <v-btn class="oeno-btn oeno-btn--primary mt-2" :disabled="!canCreateLot" :loading="inventoryStore.isSaving" @click="createLot">
          Create acquisition lot
        </v-btn>
      </v-card-text>
    </v-card>

    <v-card v-if="createdLot" class="oeno-card" variant="flat">
      <v-card-text class="pa-6">
        <p class="oeno-card-label mb-1">Step 3</p>
        <h2 class="oeno-card-title mb-4">Dispatch now or later</h2>
        <p class="oeno-muted mb-4">
          Lot #{{ createdLot.id }} created. Available in intake: {{ createdLot.totalAvailable }} bottle(s).
        </p>

        <div class="d-flex ga-2 flex-wrap align-center">
          <v-text-field
            v-model.number="dispatchQuantity"
            label="Dispatch quantity"
            type="number"
            min="1"
            :max="createdLot.totalAvailable || 1"
            style="max-width: 220px;"
            variant="outlined"
            hide-details
          />
          <v-btn
            class="oeno-btn oeno-btn--primary"
            :loading="inventoryStore.isSaving"
            :disabled="!createdLot.items?.length || dispatchQuantity <= 0 || dispatchQuantity > Number(createdLot.totalAvailable || 0)"
            @click="dispatchNow"
          >
            Dispatch to cellar
          </v-btn>
          <v-btn variant="outlined" @click="openLot">
            Open lot detail
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<style scoped>
.oeno-upload-preview {
  background: rgba(122, 40, 56, 0.05);
  border: 1px dashed rgba(122, 40, 56, 0.2);
  border-radius: 20px;
  min-height: 220px;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.oeno-upload-preview__image {
  width: 100%;
  max-height: 320px;
  object-fit: contain;
  border-radius: 14px;
}

.oeno-lines-preview {
  border-top: 1px solid rgba(120, 120, 120, 0.15);
  padding-top: 12px;
}

.oeno-lines-preview__list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.oeno-lines-preview__line {
  font-size: 0.88rem;
  color: rgba(38, 24, 27, 0.82);
}
</style>
