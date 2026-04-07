import api from '@/core/api/http'

export const assistantApi = {
  query: (payload) => api.post('/assistant/query', payload),
}

