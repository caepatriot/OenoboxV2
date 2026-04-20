import { acquisitionLotApi } from '@/core/api/http'

export const storedBottleApi = {
  // Stored bottle endpoints are not exposed yet on backend.
  // Keep a minimal bridge through lot details for inventory UI composition.
  getFromLot: (lotId) => acquisitionLotApi.getById(lotId),
}
