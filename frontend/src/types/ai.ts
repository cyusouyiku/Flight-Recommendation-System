export interface AiFeedbackRequest {
  flightId: number
  feedbackType: 'LIKE' | 'DISLIKE' | 'NEUTRAL'
  feedbackText?: string
  metadata?: Record<string, any>
}

export interface AiFeedbackResponse {
  id: number
  userId: number
  flightId: number
  feedbackType: string
  feedbackText?: string
  aiAnalysis?: string
  confidenceScore?: number
  metadata?: Record<string, any>
  createdAt: string
  updatedAt: string
}

export interface AiFeedbackRerankRequest {
  currentRecommendations: FlightFeedbackItem[]
  feedbackSummary: string
  recommendationType: string
}

export interface FlightFeedbackItem {
  flightId: number
  feedbackType: 'LIKE' | 'DISLIKE' | 'NEUTRAL'
  reason?: string
  metadata?: Record<string, any>
}

export interface AiFeedbackRerankResponse {
  feedbackId?: number
  userId: number
  aiAnalysis: string
  confidenceScore: number
  optimizationReason: string
  rerankedRecommendations: Record<string, any[]>
  processingTimeMs: number
  processedAt: string
  additionalInfo: string
}

export interface AiRecommendRequest {
  departure?: string
  arrival?: string
  departureDate?: string
  budget?: number
  preferences?: {
    preferredAirlines?: string[]
    preferredSeatClass?: string
    maxStops?: number
    timePreference?: 'MORNING' | 'AFTERNOON' | 'EVENING' | 'ANY'
  }
}

export interface AiAssistantMessage {
  id: string
  role: 'user' | 'assistant'
  content: string
  timestamp: string
}

export interface AiAssistantResponse {
  message: string
  suggestions?: string[]
  actions?: {
    type: string
    label: string
    data: any
  }[]
  analysis?: string
  satisfaction?: number
  accuracy?: number
  speed?: number
}