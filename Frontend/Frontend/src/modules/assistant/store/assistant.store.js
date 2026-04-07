import { defineStore } from 'pinia'

export const useAssistantStore = defineStore('assistant', { state: () => ({ messages: [] }) })

