import { acquisitionLotApi as api, inventoryIntakeApi } from '@/core/api/http'

export const listAcquisitionLots = () => api.getAll()
export const getAcquisitionLotById = (id) => api.getById(id)
export const createAcquisitionLot = (payload) => api.create(payload)
export const dispatchAcquisitionLotItem = (lotId, payload) => api.dispatch(lotId, payload)

export const searchIntakeCandidates = (query) => inventoryIntakeApi.search(query)
export const searchIntakeByOcr = (extractedText) => inventoryIntakeApi.ocrLookup({ extractedText })
export const scanLabelImage = (file, options) => inventoryIntakeApi.scanImage(file, options)
