import { wineApi } from '@/core/api/http'

const toWinesParams = (filtersOrQuery) => {
  if (typeof filtersOrQuery === 'string') {
    const query = filtersOrQuery.trim()
    return query ? { query } : undefined
  }

  const source = filtersOrQuery && typeof filtersOrQuery === 'object' ? filtersOrQuery : {}
  const params = {}

  for (const key of ['query', 'region', 'country', 'producer', 'wineType']) {
    const raw = source[key]
    if (raw === null || raw === undefined) continue
    const value = String(raw).trim()
    if (value) params[key] = value
  }

  return Object.keys(params).length ? params : undefined
}

export const listWines = (filtersOrQuery) => wineApi.getAll(toWinesParams(filtersOrQuery))
export const getWineById = (id) => wineApi.getById(id)
export const createWine = (payload) => wineApi.create(payload)
export const updateWine = (id, payload) => wineApi.update(id, payload)
export const deleteWine = (id) => wineApi.delete(id)
export const suggestWines = (query) => wineApi.getSuggestions(query)
