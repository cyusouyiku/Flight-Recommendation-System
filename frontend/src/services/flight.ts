import api from '@/utils/api'
import type {
  Flight,
  FlightSearchParams,
  FlightSearchResponse,
  Recommendation,
  PriceTrend,
  PriceAlert
} from '@/types/flight'

export const flightService = {
  // 搜索航班
  async searchFlights(params: FlightSearchParams): Promise<FlightSearchResponse> {
    const response = await api.get('/flights/search', { params })
    return response.data
  },

  // 获取航班详情
  async getFlightById(id: number): Promise<Flight> {
    const response = await api.get(`/flights/${id}`)
    return response.data
  },

  // 获取个性化推荐
  async getRecommendations(): Promise<Recommendation[]> {
    const response = await api.get('/recommendations')
    return response.data
  },

  // 获取热门航线
  async getPopularRoutes(limit = 10): Promise<Flight[]> {
    const response = await api.get('/routes/popular', { params: { limit } })
    return response.data
  },

  // 获取价格趋势
  async getPriceTrend(params: {
    departure: string
    arrival: string
    departureDate?: string
    period?: string
  }): Promise<PriceTrend[]> {
    const response = await api.get('/price/trend', { params })
    return response.data
  },

  // 创建价格提醒
  async createPriceAlert(alertData: {
    departureAirportCode: string
    arrivalAirportCode: string
    targetPrice: number
    expiryDate?: string
  }): Promise<PriceAlert> {
    const response = await api.post('/price/alert', alertData)
    return response.data
  },

  // 获取用户的价格提醒列表
  async getUserPriceAlerts(): Promise<PriceAlert[]> {
    const response = await api.get('/price/alert')
    return response.data
  },

  // 更新价格提醒
  async updatePriceAlert(id: number, updates: Partial<PriceAlert>): Promise<PriceAlert> {
    const response = await api.put(`/price/alert/${id}`, updates)
    return response.data
  },

  // 删除价格提醒
  async deletePriceAlert(id: number): Promise<void> {
    await api.delete(`/price/alert/${id}`)
  },

  // 获取常搜航线
  async getFrequentRoutes(limit = 10): Promise<Flight[]> {
    const response = await api.get('/routes/frequent', { params: { limit } })
    return response.data
  }
}