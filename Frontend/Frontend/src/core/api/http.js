
import axios from 'axios'
import {
  handleApiError,
  handleApiSuccess,
} from '@/core/connectivity/serverStatus'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => {
    handleApiSuccess()
    return response
  },
  (error) => {
    handleApiError(error)
    console.error('API Error:', error.response?.data || error.message)
    return Promise.reject(error)
  },
)

export const tastingApi = {
  getAll: () => api.get('/tastings'),
  getById: (id) => api.get(`/tastings/${id}`),
  create: (tastingData) => api.post('/tastings', tastingData),
  update: (id, tastingData) => api.put(`/tastings/${id}`, tastingData),
  delete: (id) => api.delete(`/tastings/${id}`),
  getByWineType: (wineType) => api.get(`/tastings/wine-type/${wineType}`),
  getByRegion: (region) => api.get(`/tastings/region/${region}`),
  getByCepage: (cepage) => api.get(`/tastings/cepage/${cepage}`),
  getRecent: () => api.get('/tastings/recent'),
  countByWineType: (wineType) => api.get(`/tastings/count/wine-type/${wineType}`),
  search: (params) => api.get('/tastings/search', { params }),
  getSteps: () => api.get('/tastings/steps/dynamic'),
}

export const cepageApi = {
  getAll: () => api.get('/cepages'),
  getById: (id) => api.get(`/cepages/${id}`),
  create: (cepageData) => api.post('/cepages', cepageData),
  update: (id, cepageData) => api.put(`/cepages/${id}`, cepageData),
  delete: (id) => api.delete(`/cepages/${id}`),
}

export const wineApi = {
  getAll: (params) => api.get('/wines', { params }),
  getById: (id) => api.get(`/wines/${id}`),
  create: (wineData) => api.post('/wines', wineData),
  update: (id, wineData) => api.put(`/wines/${id}`, wineData),
  delete: (id) => api.delete(`/wines/${id}`),
  getSuggestions: (query) => api.get('/wines/suggestions', { params: { query } }),
}

export const cellarApi = {
  getAll: () => api.get('/caves'),
  getById: (id) => api.get(`/caves/${id}`),
  getWorkspace: (id) => api.get(`/caves/${id}/workspace`),
  export: (id) => api.get(`/caves/${id}/export`),
  import: (payload) => api.post('/caves/import', payload),
  restore: (id, payload) => api.put(`/caves/${id}/restore`, payload),

  create: (caveData) => api.post('/caves', caveData),
  update: (id, caveData) => api.put(`/caves/${id}`, caveData),
  delete: (id) => api.delete(`/caves/${id}`),

  createUnit: (caveId, unitData) => api.post(`/caves/${caveId}/units`, unitData),
  updateUnit: (unitId, unitData) => api.put(`/caves/units/${unitId}`, unitData),
  deleteUnit: (unitId) => api.delete(`/caves/units/${unitId}`),

  updateSpace: (spaceId, spaceData) => api.put(`/caves/spaces/${spaceId}`, spaceData),
  bulkUpdateSpaces: (payload) => api.post('/caves/spaces/bulk', payload),

  addPlacement: (placementData) => api.post('/caves/placements', placementData),
  bulkPlace: (payload) => api.post('/caves/placements/bulk', payload),
  autoPlace: (caveId, payload) => api.post(`/caves/${caveId}/auto-place`, payload),
  updatePlacement: (id, placementData) => api.put(`/caves/placements/${id}`, placementData),
  deletePlacement: (id) => api.delete(`/caves/placements/${id}`),
}

export const caveApi = cellarApi

export const inventoryIntakeApi = {
  search: (query) => api.get('/inventory/intake/search', { params: { q: query } }),
  ocrLookup: (payload) => api.post('/inventory/intake/ocr', payload),
  scanImage: (file, options = {}) => {
    const formData = new FormData()
    formData.append('file', file)
    if (options.language) formData.append('language', options.language)
    return api.post('/inventory/intake/ocr/scan', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export const acquisitionLotApi = {
  create: (payload) => api.post('/inventory/lots', payload),
  getAll: () => api.get('/inventory/lots'),
  getById: (id) => api.get(`/inventory/lots/${id}`),
  dispatch: (id, payload) => api.post(`/inventory/lots/${id}/dispatch`, payload),
}

export const catalogSubmissionApi = {
  create: (payload) => api.post('/catalog/submissions', payload),
}

export default api
