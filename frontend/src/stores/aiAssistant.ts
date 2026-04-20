import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AiAssistantMessage } from '@/types/ai'

interface AiAssistantSettings {
  autoOpen: boolean
  soundEnabled: boolean
  theme: 'light' | 'dark'
  width: number
  position: 'right' | 'bottom'
}

export const useAiAssistantStore = defineStore('aiAssistant', () => {
  // 状态
  const isOpen = ref(true) // 默认打开
  const isMinimized = ref(false)
  const messages = ref<AiAssistantMessage[]>([
    {
      id: '1',
      role: 'assistant',
      content: '您好！我是您的AI旅行助手。我可以帮您搜索航班、分析偏好、优化推荐，或者回答任何关于旅行的问题。请问有什么可以帮您的？',
      timestamp: new Date(Date.now() - 300000).toISOString()
    }
  ])
  const loading = ref(false)
  const suggestions = ref([
    '帮我找北京到上海最便宜的航班',
    '推荐周末旅游的好去处',
    '分析我的航班偏好',
    '我对最近的推荐不满意',
    '设置价格提醒'
  ])

  // 设置
  const settings = ref<AiAssistantSettings>({
    autoOpen: true,
    soundEnabled: false,
    theme: 'light',
    width: 400,
    position: 'right'
  })

  // 统计
  const helpCount = ref(42)
  const satisfactionRate = ref(92)
  const overallSatisfaction = ref(88)
  const recommendationAccuracy = ref(85)
  const responseSpeed = ref(95)

  // 计算属性
  const isMobile = computed(() => window.innerWidth < 768)
  const dialogWidth = computed(() => {
    if (isMobile.value) return '100%'
    if (window.innerWidth < 1024) return '300px'
    return `${settings.value.width}px`
  })
  const dialogPosition = computed(() => {
    if (isMobile.value) return 'bottom'
    return settings.value.position
  })

  // 从本地存储加载设置
  const loadSettings = () => {
    const saved = localStorage.getItem('aiAssistantSettings')
    if (saved) {
      try {
        const parsed = JSON.parse(saved)
        settings.value = { ...settings.value, ...parsed }
      } catch (e) {
        console.error('Failed to load AI assistant settings:', e)
      }
    }

    const savedState = localStorage.getItem('aiAssistantState')
    if (savedState) {
      try {
        const parsed = JSON.parse(savedState)
        isOpen.value = parsed.isOpen !== undefined ? parsed.isOpen : true
        isMinimized.value = parsed.isMinimized || false
      } catch (e) {
        console.error('Failed to load AI assistant state:', e)
      }
    }
  }

  // 保存设置到本地存储
  const saveSettings = () => {
    localStorage.setItem('aiAssistantSettings', JSON.stringify(settings.value))
    localStorage.setItem('aiAssistantState', JSON.stringify({
      isOpen: isOpen.value,
      isMinimized: isMinimized.value
    }))
  }

  // 初始化加载设置
  loadSettings()

  // 动作
  const toggleOpen = () => {
    isOpen.value = !isOpen.value
    saveSettings()
  }

  const toggleMinimized = () => {
    isMinimized.value = !isMinimized.value
    saveSettings()
  }

  const open = () => {
    isOpen.value = true
    isMinimized.value = false
    saveSettings()
  }

  const close = () => {
    isOpen.value = false
    saveSettings()
  }

  const addMessage = (message: AiAssistantMessage) => {
    messages.value.push(message)
    // 限制消息数量，避免内存问题
    if (messages.value.length > 100) {
      messages.value = messages.value.slice(-50)
    }
  }

  const clearMessages = () => {
    messages.value = []
  }

  const updateSuggestions = (newSuggestions: string[]) => {
    suggestions.value = newSuggestions.slice(0, 5)
  }

  const updateSatisfactionStats = (stats: {
    satisfaction?: number
    accuracy?: number
    speed?: number
  }) => {
    if (stats.satisfaction !== undefined) {
      overallSatisfaction.value = Math.round(stats.satisfaction * 100)
    }
    if (stats.accuracy !== undefined) {
      recommendationAccuracy.value = Math.round(stats.accuracy * 100)
    }
    if (stats.speed !== undefined) {
      responseSpeed.value = Math.round(stats.speed * 100)
    }
  }

  const incrementHelpCount = () => {
    helpCount.value += 1
  }

  const updateSettings = (newSettings: Partial<AiAssistantSettings>) => {
    settings.value = { ...settings.value, ...newSettings }
    saveSettings()
  }

  const resetSettings = () => {
    settings.value = {
      autoOpen: true,
      soundEnabled: false,
      theme: 'light',
      width: 400,
      position: 'right'
    }
    saveSettings()
  }

  return {
    // 状态
    isOpen,
    isMinimized,
    messages,
    loading,
    suggestions,
    settings,
    helpCount,
    satisfactionRate,
    overallSatisfaction,
    recommendationAccuracy,
    responseSpeed,

    // 计算属性
    isMobile,
    dialogWidth,
    dialogPosition,

    // 动作
    toggleOpen,
    toggleMinimized,
    open,
    close,
    addMessage,
    clearMessages,
    updateSuggestions,
    updateSatisfactionStats,
    incrementHelpCount,
    updateSettings,
    resetSettings,
    loadSettings,
    saveSettings
  }
})