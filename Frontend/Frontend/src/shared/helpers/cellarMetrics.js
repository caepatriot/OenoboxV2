export function safeArray(value) {
  return Array.isArray(value) ? value : []
}

export function safeNumber(value, fallback = 0) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

export function extractUnits(caves = []) {
  return safeArray(caves).flatMap((cave) => safeArray(cave.units))
}

export function extractSpaces(caves = []) {
  return extractUnits(caves).flatMap((unit) => safeArray(unit.spaces))
}

export function spaceOccupiedQuantity(space) {
  return safeArray(space?.currentBottles).reduce((sum, placement) => {
    return sum + safeNumber(placement?.quantity, 1)
  }, 0)
}

export function extractPlacements(caves = []) {
  return safeArray(caves).flatMap((cave) => {
    return safeArray(cave.units).flatMap((unit) => {
      return safeArray(unit.spaces).flatMap((space, index) => {
        return safeArray(space.currentBottles).map((placement) => ({
          ...placement,
          quantity: safeNumber(placement?.quantity, 1),
          caveId: cave.id,
          caveName: cave.name || 'Unnamed cellar',
          unitId: unit.id,
          unitName: unit.name || 'Unnamed rack',
          spaceId: space.id,
          spaceLabel: space.name || space.label || space.code || `Slot ${index + 1}`,
          spaceCapacity: safeNumber(space.capacity, 1),
        }))
      })
    })
  })
}

export function cellarCapacity(cave) {
  const spaceCapacity = safeArray(cave?.units).reduce((sum, unit) => {
    return sum + safeArray(unit.spaces).reduce((spaceSum, space) => {
      return spaceSum + Math.max(1, safeNumber(space.capacity, 1))
    }, 0)
  }, 0)

  if (spaceCapacity > 0) return spaceCapacity

  return safeArray(cave?.units).length
}

export function cellarOccupied(cave) {
  return safeArray(cave?.units).reduce((sum, unit) => {
    return sum + safeArray(unit.spaces).reduce((spaceSum, space) => {
      return spaceSum + spaceOccupiedQuantity(space)
    }, 0)
  }, 0)
}

export function occupancyRate(occupied, capacity) {
  if (!capacity) return 0
  return Math.min(100, Math.round((safeNumber(occupied) / safeNumber(capacity, 1)) * 100))
}

export function inventoryValue(placements = []) {
  return safeArray(placements).reduce((sum, placement) => {
    const price = safeNumber(
      placement?.wine?.prixActuel ?? placement?.wine?.prixLancement ?? placement?.wine?.price ?? 0,
      0,
    )
    return sum + price * safeNumber(placement?.quantity, 1)
  }, 0)
}

export function typeLabel(value) {
  const map = {
    red: 'Red',
    white: 'White',
    rose: 'Rose',
    sparkling: 'Sparkling',
    dessert: 'Dessert',
  }
  return map[value] || value || 'Unknown'
}

export function buildTypeDistribution(placements = []) {
  const counter = new Map()
  safeArray(placements).forEach((placement) => {
    const key = typeLabel(placement?.wine?.type)
    counter.set(key, (counter.get(key) || 0) + safeNumber(placement?.quantity, 1))
  })

  return [...counter.entries()]
    .map(([label, value]) => ({ label, value }))
    .sort((a, b) => b.value - a.value)
}

export function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 0,
  }).format(safeNumber(value, 0))
}

export function formatBottleTitle(wine) {
  if (!wine) return 'Unknown wine'
  const parts = [wine.name, wine.year || wine.vintage, wine.region].filter(Boolean)
  return parts.join(' - ')
}

export function buildOliSuggestions({ caves = [], placements = [] }) {
  const suggestions = []
  const totalBottles = safeArray(placements).reduce((sum, placement) => sum + safeNumber(placement.quantity, 1), 0)
  const warmCellars = safeArray(caves).filter((cave) => safeNumber(cave.temperature, 12) > 14)
  const humidCellars = safeArray(caves).filter((cave) => safeNumber(cave.humidity, 70) > 80)
  const topBottle = [...safeArray(placements)].sort((a, b) => safeNumber(b.quantity, 0) - safeNumber(a.quantity, 0))[0]

  if (topBottle?.wine) {
    suggestions.push({
      title: 'Most available bottle',
      description: `${formatBottleTitle(topBottle.wine)} is your easiest bottle to reach right now with ${topBottle.quantity} in stock.`,
      tone: 'primary',
    })
  }

  if (warmCellars.length) {
    const cellar = warmCellars[0]
    suggestions.push({
      title: 'Temperature check',
      description: `${cellar.name} is at ${safeNumber(cellar.temperature, 0)} degrees C.`,
      tone: 'warning',
    })
  }

  if (humidCellars.length) {
    const cellar = humidCellars[0]
    suggestions.push({
      title: 'Humidity is high',
      description: `${cellar.name} is around ${safeNumber(cellar.humidity, 0)} percent humidity.`,
      tone: 'info',
    })
  }

  if (!suggestions.length) {
    suggestions.push({
      title: 'Collection overview looks healthy',
      description: `${totalBottles} bottles are tracked across ${safeArray(caves).length} cellar spaces.`,
      tone: 'success',
    })
  }

  return suggestions
}
