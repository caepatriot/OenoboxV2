import axios from 'axios';

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Tasting API endpoints
export const tastingApi = {
  // Get all tastings
  getAll: () => api.get('/tastings'),

  // Get tasting by ID
  getById: (id) => api.get(`/tastings/${id}`),

  // Create new tasting
  create: (tastingData) => api.post('/tastings', tastingData),

  // Update tasting
  update: (id, tastingData) => api.put(`/tastings/${id}`, tastingData),

  // Delete tasting
  delete: (id) => api.delete(`/tastings/${id}`),

  // Get tastings by wine type
  getByWineType: (wineType) => api.get(`/tastings/wine-type/${wineType}`),

  // Get tastings by region
  getByRegion: (region) => api.get(`/tastings/region/${region}`),

  // Get tastings by cepage
  getByCepage: (cepage) => api.get(`/tastings/cepage/${cepage}`),

  // Get recent tastings
  getRecent: () => api.get('/tastings/recent'),

  // Count tastings by wine type
  countByWineType: (wineType) => api.get(`/tastings/count/wine-type/${wineType}`),

  // Search tastings
  search: (params) => api.get('/tastings/search', { params }),

  // Get tasting steps configuration
  getSteps: () => api.get('/tastings/steps/dynamic'),
};

// Cepage API endpoints (if needed)
export const cepageApi = {
  getAll: () => api.get('/cepages'),
  getById: (id) => api.get(`/cepages/${id}`),
  create: (cepageData) => api.post('/cepages', cepageData),
  update: (id, cepageData) => api.put(`/cepages/${id}`, cepageData),
  delete: (id) => api.delete(`/cepages/${id}`),
};

// Wine API endpoints (if needed)
export const wineApi = {
  getAll: () => api.get('/wines'),
  getById: (id) => api.get(`/wines/${id}`),
  create: (wineData) => api.post('/wines', wineData),
  update: (id, wineData) => api.put(`/wines/${id}`, wineData),
  delete: (id) => api.delete(`/wines/${id}`),
};

// Cave Management API endpoints
export const caveApi = {
  getAll: () => api.get('/caves'),
  getById: (id) => api.get(`/caves/${id}`),
  create: (caveData) => api.post('/caves', caveData),
  update: (id, caveData) => api.put(`/caves/${id}`, caveData),
  delete: (id) => api.delete(`/caves/${id}`),
  createUnit: (caveId, unitData) => api.post(`/caves/${caveId}/units`, unitData),
  updateUnit: (unitId, unitData) => api.put(`/caves/units/${unitId}`, unitData),
  deleteUnit: (unitId) => api.delete(`/caves/units/${unitId}`),
  addPlacement: (placementData) => api.post('/caves/placements', placementData),
  updatePlacement: (id, placementData) => api.put(`/caves/placements/${id}`, placementData),
  deletePlacement: (id) => api.delete(`/caves/placements/${id}`),
};

export default api;
