import { onBeforeUnmount, ref, watch } from 'vue'

export const useWineSearch = (searchFn, options = {}) => {
  const debounceMs = options.debounceMs ?? 350
  const minChars = options.minChars ?? 2

  const query = ref('')
  const results = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  let timer = null

  const clear = () => {
    results.value = []
    error.value = null
  }

  const runSearch = async (value) => {
    const normalized = value?.trim() || ''
    query.value = normalized

    if (normalized.length < minChars) {
      clear()
      return []
    }

    isLoading.value = true
    error.value = null
    try {
      const data = await searchFn(normalized)
      results.value = Array.isArray(data) ? data : []
      return results.value
    } catch (err) {
      error.value = err?.response?.data || err?.message || 'Search failed'
      results.value = []
      return []
    } finally {
      isLoading.value = false
    }
  }

  watch(query, (value) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      runSearch(value)
    }, debounceMs)
  })

  onBeforeUnmount(() => {
    if (timer) clearTimeout(timer)
  })

  return {
    query,
    results,
    isLoading,
    error,
    runSearch,
    clear,
  }
}
