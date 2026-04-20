import { acquisitionLotApi } from '@/core/api/http'

export const stockTransactionApi = {
  // Transaction feed endpoint does not exist yet.
  // Reuse lot detail as the current source of dispatch state.
  getLotActivity: (lotId) => acquisitionLotApi.getById(lotId),
}
