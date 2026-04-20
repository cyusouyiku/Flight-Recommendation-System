import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import type { LoginRequest, RegisterRequest, UserInfo } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const userInfo = ref<UserInfo | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const clearAuth = () => {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    delete axios.defaults.headers.common['Authorization']
  }

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await axios.post('/api/auth/login', credentials)
      const { data } = response.data

      setToken(data.token)
      setUserInfo(data.user)

      return data
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '登录失败')
      }
      throw error
    }
  }

  const register = async (userData: RegisterRequest) => {
    try {
      const response = await axios.post('/api/auth/register', userData)
      const { data } = response.data

      setToken(data.token)
      setUserInfo(data.user)

      return data
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(error.response?.data?.message || '注册失败')
      }
      throw error
    }
  }

  const logout = () => {
    clearAuth()
  }

  const checkAuth = async () => {
    if (!token.value) return false

    try {
      const response = await axios.get('/api/auth/me')
      setUserInfo(response.data.data)
      return true
    } catch (error) {
      clearAuth()
      return false
    }
  }

  // 初始化axios拦截器
  if (token.value) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    setToken,
    setUserInfo,
    clearAuth,
    login,
    register,
    logout,
    checkAuth
  }
})