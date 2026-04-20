import api from '@/utils/api'
import type {
  AiFeedbackRequest,
  AiFeedbackResponse,
  AiFeedbackRerankRequest,
  AiFeedbackRerankResponse,
  AiRecommendRequest,
  AiAssistantMessage,
  AiAssistantResponse
} from '@/types/ai'
import type { Flight } from '@/types/flight'

export const aiService = {
  // 提交用户反馈
  async submitFeedback(feedback: AiFeedbackRequest): Promise<AiFeedbackResponse> {
    const response = await api.post('/ai/feedback', feedback)
    return response.data
  },

  // 获取用户反馈历史
  async getFeedbackHistory(): Promise<AiFeedbackResponse[]> {
    const response = await api.get('/ai/feedback')
    return response.data
  },

  // 获取反馈统计
  async getFeedbackStats(): Promise<Record<string, any>> {
    const response = await api.get('/ai/feedback/stats')
    return response.data
  },

  // AI分析推荐效果
  async analyzeRecommendations(period = 'LAST_7_DAYS'): Promise<Record<string, any>> {
    const response = await api.post('/ai/analyze', { period })
    return response.data
  },

  // 获取AI服务状态
  async getAiStatus(): Promise<{
    enabled: boolean
    connected: boolean
    status: string
    timestamp: number
  }> {
    const response = await api.get('/ai/status')
    return response.data
  },

  // 优化推荐策略
  async optimizeStrategy(): Promise<Record<string, any>> {
    const response = await api.post('/ai/optimize')
    return response.data
  },

  // 获取AI个性化推荐
  async getAiRecommendations(request: AiRecommendRequest): Promise<Record<string, Flight[]>> {
    const response = await api.post('/ai/recommend', request)
    return response.data
  },

  // 不满意反馈并重新优化
  async rerankAfterFeedback(request: AiFeedbackRerankRequest): Promise<AiFeedbackRerankResponse> {
    const response = await api.post('/ai/feedback/rerank', request)
    return response.data
  },

  // AI助手对话（满意度助手）
  async chatWithAssistant(message: string, context?: AiAssistantMessage[]): Promise<AiAssistantResponse> {
    try {
      console.log('AI助手请求:', { message, contextLength: context?.length || 0 })
      const axiosResponse = await api.post('/ai/assistant', {
        message,
        context: context || []
      })

      console.log('AI助手响应:', axiosResponse)

      // api拦截器已经处理了统一响应格式，返回的已经是数据部分
      const response = axiosResponse as any

      // 确保有消息内容
      if (!response || typeof response !== 'object') {
        throw new Error('AI助手响应格式异常')
      }

      if (!response.message) {
        console.error('AI助手响应缺少message字段:', response)
        throw new Error('AI助手响应异常: 缺少消息内容')
      }

      return response as AiAssistantResponse
    } catch (error) {
      console.error('AI助手请求失败:', error)
      // 提供更友好的错误信息
      if (error instanceof Error) {
        // 保留原始错误信息
        throw error
      } else {
        throw new Error(`AI助手请求失败: ${String(error)}`)
      }
    }
  },

  // 获取AI分析报告
  async getAnalysisReport(): Promise<{
    satisfaction: number
    recommendations: number
    accuracy: number
    insights: string[]
  }> {
    const response = await api.get('/ai/analysis/report')
    return response.data
  },

  // 批量分析反馈
  async analyzeFeedbacksBatch(feedbackIds: number[]): Promise<Record<string, any>[]> {
    const response = await api.post('/ai/feedback/analyze/batch', { feedbackIds })
    return response.data
  }
}