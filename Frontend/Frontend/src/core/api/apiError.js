export function toApiError(error) {
  return {
    message: error?.response?.data?.message || error?.message || 'Unexpected error',
    status: error?.response?.status || 500,
    details: error?.response?.data || null,
  }
}
