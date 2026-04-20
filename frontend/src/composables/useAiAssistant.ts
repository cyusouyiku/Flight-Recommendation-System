import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { AiAssistantMessage } from '@/types/ai'
import { aiService } from '@/services/ai'
import { useAuthStore } from '@/stores/auth'
import { useAiAssistantStore } from '@/stores/aiAssistant'

export function useAiAssistant() {
  const authStore = useAuthStore()
  const aiAssistantStore = useAiAssistantStore()

  const userInput = ref('')

  // 计算属性
  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const userName = computed(() => authStore.userInfo?.username || '用户')
  const userInitials = computed(() => {
    if (!authStore.userInfo?.username) return '用户'
    const name = authStore.userInfo.username
    if (name.length >= 2) {
      return name.substring(0, 2).toUpperCase()
    }
    return name.charAt(0).toUpperCase()
  })

  // 方法
  const sendMessage = async (content: string) => {
    const { addMessage, updateSuggestions, updateSatisfactionStats, incrementHelpCount } = aiAssistantStore

    if (aiAssistantStore.loading) return


    const userMessage: AiAssistantMessage = {
      id: Date.now().toString(),
      role: 'user',
      content,
      timestamp: new Date().toISOString()
    }

    addMessage(userMessage)
    aiAssistantStore.loading = true

    try {
      // 调用实际的AI助手API
      const context = aiAssistantStore.messages.map(msg => ({
        id: msg.id,
        role: msg.role,
        content: msg.content,
        timestamp: msg.timestamp
      }))

      const response = await aiService.chatWithAssistant(content, context)

      // 检查响应是否有效
      if (!response || !response.message) {
        throw new Error('AI助手响应异常')
      }

      const assistantMessage: AiAssistantMessage = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.message,
        timestamp: new Date().toISOString()
      }

      addMessage(assistantMessage)
      incrementHelpCount()

      // 如果API返回了建议，更新快速提问建议
      if (response.suggestions && Array.isArray(response.suggestions)) {
        updateSuggestions(response.suggestions.slice(0, 5))
      }

      // 更新满意度统计数据
      if (response.analysis && response.satisfaction !== undefined) {
        updateSatisfactionStats({
          satisfaction: response.satisfaction,
          accuracy: response.accuracy,
          speed: response.speed
        })
      }

      return assistantMessage
    } catch (error) {
      console.error('AI助手对话失败:', error)

      // 提供更详细的错误提示
      const errorMessage = getErrorMessage(error)
      ElMessage.error(`发送消息失败：${errorMessage}`)

      // 如果API调用失败，提供友好的错误提示
      const assistantErrorMessage: AiAssistantMessage = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: getAssistantErrorMessage(error),
        timestamp: new Date().toISOString()
      }

      addMessage(assistantErrorMessage)
      return assistantErrorMessage
    } finally {
      aiAssistantStore.loading = false
    }
  }

  const formatMessage = (content: string) => {
    // 简单的Markdown转换
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\n/g, '<br>')
      .replace(/•/g, '•')
  }

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp)
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const getErrorMessage = (error: any): string => {
    if (error?.response?.status === 401) {
      return '认证失败，请重新登录'
    } else if (error?.response?.status === 403) {
      return '没有权限访问此功能'
    } else if (error?.response?.status === 404) {
      return '服务暂时不可用'
    } else if (error?.response?.status === 500) {
      return '服务器内部错误'
    } else if (error?.request) {
      return '网络连接失败，请检查网络设置'
    } else if (error?.message) {
      return error.message
    }
    return '未知错误，请稍后重试'
  }

  const getAssistantErrorMessage = (error: any): string => {
    const errorMsg = getErrorMessage(error)
    return `抱歉，AI助手服务遇到问题：${errorMsg}\n\n建议：\n1. 检查网络连接\n2. 刷新页面重试\n3. 稍后再使用AI助手`
  }

  return {
    userInput,
    isAuthenticated,
    userName,
    userInitials,
    sendMessage,
    formatMessage,
    formatTime,
    getErrorMessage,
    getAssistantErrorMessage
  }
}