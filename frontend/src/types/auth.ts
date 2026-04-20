export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone?: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  phone?: string
  avatar?: string
  createdAt: string
  updatedAt: string
}

export interface AuthResponse {
  token: string
  user: UserInfo
}