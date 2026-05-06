import test from 'node:test'
import assert from 'node:assert/strict'

import {
  safeArray,
  safeNumber,
  occupancyRate,
  inventoryValue,
  formatBottleTitle,
  buildTypeDistribution,
} from '../src/shared/helpers/cellarMetrics.js'

test('safeArray returns array or empty array fallback', () => {
  assert.deepEqual(safeArray([1, 2]), [1, 2])
  assert.deepEqual(safeArray(null), [])
})

test('safeNumber parses numeric values and uses fallback', () => {
  assert.equal(safeNumber('42'), 42)
  assert.equal(safeNumber('invalid', 7), 7)
})

test('occupancyRate returns bounded percentage', () => {
  assert.equal(occupancyRate(50, 100), 50)
  assert.equal(occupancyRate(250, 100), 100)
  assert.equal(occupancyRate(10, 0), 0)
})

test('inventoryValue multiplies wine price by quantity with fallbacks', () => {
  const placements = [
    { quantity: 2, wine: { price: 10 } },
    { quantity: 3, wine: { prixActuel: 5 } },
    { wine: { price: 4 } },
  ]

  assert.equal(inventoryValue(placements), 39)
})

test('formatBottleTitle builds display name from available fields', () => {
  assert.equal(
    formatBottleTitle({ name: 'Riesling', year: 2020, region: 'Mosel' }),
    'Riesling - 2020 - Mosel',
  )
  assert.equal(formatBottleTitle(null), 'Unknown wine')
})

test('buildTypeDistribution aggregates placement quantity by wine type', () => {
  const result = buildTypeDistribution([
    { quantity: 2, wine: { type: 'red' } },
    { quantity: 1, wine: { type: 'red' } },
    { quantity: 4, wine: { type: 'white' } },
  ])

  assert.deepEqual(result, [
    { label: 'White', value: 4 },
    { label: 'Red', value: 3 },
  ])
})
