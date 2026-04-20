import api from '@/utils/api'
import type { LoginRequest, RegisterRequest, AuthResponse, UserInfo } from '@/types/auth'

export const authService = {
  // 用户登录
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post('/auth/login', credentials)
    return response.data
  },

  // 用户注册
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post('/auth/register', userData)
    return response.data
  },

  // 获取当前用户信息
  async getCurrentUser(): Promise<UserInfo> {
    const response = await api.get('/auth/me')
    return response.data
  },

  // 退出登录
  async logout(): Promise<void> {
    // 通常前端只需要清除token，后端可能没有logout接口
    await api.post('/auth/logout')
  },

  // 刷新token
  async refreshToken(refreshToken: string): Promise<{ token: string }> {
    const response = await api.post('/auth/refresh', { refreshToken })
    return response.data
  }
}