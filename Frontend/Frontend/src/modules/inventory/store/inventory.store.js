import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  createAcquisitionLot,
  dispatchAcquisitionLotItem,
  getAcquisitionLotById,
  listAcquisitionLots,
  scanLabelImage,
  searchIntakeByOcr,
  searchIntakeCandidates,
} from '@/modules/inventory/api/acquisitionLot.api'
import { catalogSubmissionApi } from '@/core/api/http'

export const useInventoryStore = defineStore('inventory', () => {
  const lots = ref([])
  const selectedLot = ref(null)
  const intakeResults = ref([])
  const ocrScanResult = ref(null)
  const isLoading = ref(false)
  const isSaving = ref(false)
  const error = ref(null)

  const totals = computed(() => {
    return lots.value.reduce(
      (acc, lot) => {
        acc.received += Number(lot?.totalReceived || 0)
        acc.available += Number(lot?.totalAvailable || 0)
        acc.dispatched += Number(lot?.totalDispatched || 0)
        return acc
      },
      { received: 0, available: 0, dispatched: 0 },
    )
  })

  const withLoading = async (task, { saving = false } = {}) => {
    try {
      if (saving) isSaving.value = true
      else isLoading.value = true
      error.value = null
      return await task()
    } catch (err) {
      error.value = err?.response?.data || err.message
      throw err
    } finally {
      if (saving) isSaving.value = false
      else isLoading.value = false
    }
  }

  const fetchLots = async () =>
    withLoading(async () => {
      const response = await listAcquisitionLots()
      lots.value = Array.isArray(response?.data) ? response.data : []
      return lots.value
    })

  const fetchLotById = async (lotId) =>
    withLoading(async () => {
      const response = await getAcquisitionLotById(lotId)
      selectedLot.value = response?.data || null
      if (selectedLot.value?.id) {
        const index = lots.value.findIndex((lot) => lot.id === selectedLot.value.id)
        if (index >= 0) lots.value[index] = selectedLot.value
      }
      return selectedLot.value
    })

  const createLot = async (payload) =>
    withLoading(
      async () => {
        const response = await createAcquisitionLot(payload)
        const created = response?.data
        if (!created) return null
        lots.value = [created, ...lots.value.filter((lot) => lot.id !== created.id)]
        selectedLot.value = created
        return created
      },
      { saving: true },
    )

  const dispatchLotItem = async (lotId, payload) =>
    withLoading(
      async () => {
        const response = await dispatchAcquisitionLotItem(lotId, payload)
        const updated = response?.data
        if (!updated) return null
        selectedLot.value = updated
        const index = lots.value.findIndex((lot) => lot.id === updated.id)
        if (index >= 0) lots.value[index] = updated
        else lots.value = [updated, ...lots.value]
        return updated
      },
      { saving: true },
    )

  const lookupIntake = async (query) => {
    const q = String(query || '').trim()
    if (q.length < 2) {
      intakeResults.value = []
      return intakeResults.value
    }
    return withLoading(async () => {
      const response = await searchIntakeCandidates(q)
      intakeResults.value = Array.isArray(response?.data) ? response.data : []
      return intakeResults.value
    })
  }

  const lookupByOcrText = async (text) => {
    const t = String(text || '').trim()
    if (!t) {
      intakeResults.value = []
      ocrScanResult.value = null
      return intakeResults.value
    }
    return withLoading(async () => {
      const response = await searchIntakeByOcr(t)
      const result = response?.data || null
      ocrScanResult.value = result
      intakeResults.value = Array.isArray(result?.candidates) ? result.candidates : []
      return intakeResults.value
    })
  }

  const scanOcrImage = async (file, options = {}) => {
    if (!file) {
      ocrScanResult.value = null
      intakeResults.value = []
      return null
    }
    return withLoading(async () => {
      const response = await scanLabelImage(file, options)
      const result = response?.data || null
      ocrScanResult.value = result
      intakeResults.value = Array.isArray(result?.candidates) ? result.candidates : []
      return result
    })
  }

  const clearOcrScan = () => {
    ocrScanResult.value = null
  }

  const submitCatalogContribution = async (payload) =>
    withLoading(
      async () => {
        const response = await catalogSubmissionApi.create(payload)
        return response?.data || null
      },
      { saving: true },
    )

  return {
    lots,
    selectedLot,
    intakeResults,
    ocrScanResult,
    isLoading,
    isSaving,
    error,
    totals,
    fetchLots,
    fetchLotById,
    createLot,
    dispatchLotItem,
    lookupIntake,
    lookupByOcrText,
    scanOcrImage,
    clearOcrScan,
    submitCatalogContribution,
  }
})
