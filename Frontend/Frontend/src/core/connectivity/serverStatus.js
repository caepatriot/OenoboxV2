import { reactive, readonly } from 'vue'

const DEFAULT_INTERVAL_MS = 15000
const DEFAULT_TIMEOUT_MS = 5000

const state = reactive({
  isServerReachable: true,
  lastCheckedAt: null,
  isChecking: false,
})

let pollTimer = null

function resolveHealthUrl(apiBaseUrl) {
  const normalizedBaseUrl = (apiBaseUrl || '').replace(/\/+$/, '')
  return normalizedBaseUrl.endsWith('/api')
    ? `${normalizedBaseUrl}/health`
    : `${normalizedBaseUrl}/api/health`
}

function isTransportError(error) {
  return !error?.response
}

function markReachable() {
  state.isServerReachable = true
  state.lastCheckedAt = new Date().toISOString()
}

function markUnreachable() {
  state.isServerReachable = false
  state.lastCheckedAt = new Date().toISOString()
}

async function checkServerReachability(apiBaseUrl, timeoutMs = DEFAULT_TIMEOUT_MS) {
  state.isChecking = true
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs)

  try {
    const healthUrl = resolveHealthUrl(apiBaseUrl)
    const response = await fetch(healthUrl, {
      method: 'GET',
      cache: 'no-store',
      signal: controller.signal,
    })

    if (response.ok) {
      markReachable()
      return true
    }

    markUnreachable()
    return false
  } catch (error) {
    markUnreachable()
    return false
  } finally {
    clearTimeout(timeoutId)
    state.isChecking = false
  }
}

export function startServerStatusPolling(apiBaseUrl, options = {}) {
  const intervalMs = options.intervalMs ?? DEFAULT_INTERVAL_MS
  const timeoutMs = options.timeoutMs ?? DEFAULT_TIMEOUT_MS

  if (!pollTimer) {
    checkServerReachability(apiBaseUrl, timeoutMs)
    pollTimer = setInterval(() => {
      checkServerReachability(apiBaseUrl, timeoutMs)
    }, intervalMs)
  }
}

export function stopServerStatusPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

export function handleApiSuccess() {
  markReachable()
}

export function handleApiError(error) {
  if (isTransportError(error)) {
    markUnreachable()
  }
}

export function useServerStatus() {
  return readonly(state)
}

