import { computed, ref } from 'vue'

export const useInventoryFilters = (source) => {
  const query = ref('')

  const filtered = computed(() => {
    const list = Array.isArray(source?.value) ? source.value : []
    const needle = query.value.trim().toLowerCase()
    if (!needle) return list

    return list.filter((item) => {
      const haystack = [
        item?.source,
        item?.notes,
        item?.currency,
        ...(Array.isArray(item?.items)
          ? item.items.flatMap((lotItem) => [lotItem?.wineLabel, lotItem?.producer, lotItem?.vintageYear, lotItem?.bottleFormat])
          : []),
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()

      return haystack.includes(needle)
    })
  })

  return {
    query,
    filtered,
  }
}
