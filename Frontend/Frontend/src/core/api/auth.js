import api from './http'

export const authApi = {
  login: (payload) => api.post('/auth/login', payload),
  refresh: (payload) => api.post('/auth/refresh', payload),
  logout: () => api.post('/auth/logout'),
}
