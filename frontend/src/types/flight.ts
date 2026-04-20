export interface Flight {
  id: number
  flightNumber: string
  airlineId: number
  airlineName: string
  departureAirportId: number
  departureAirportCode: string
  departureAirportName: string
  arrivalAirportId: number
  arrivalAirportCode: string
  arrivalAirportName: string
  departureTime: string
  arrivalTime: string
  duration: number // 分钟
  price: number
  currency: string
  seatClass: string
  stops: number
  aircraftType: string
  availableSeats: number
  isDirect: boolean
  createdAt: string
  updatedAt: string
}

export interface FlightSearchParams {
  departure?: string
  arrival?: string
  departureDate?: string
  returnDate?: string
  minPrice?: number
  maxPrice?: number
  airlines?: string[]
  seatClass?: string
  stops?: number
  isDirect?: boolean
  page?: number
  size?: number
  sortBy?: string
}

export interface FlightSearchResponse {
  flights: Flight[]
  total: number
  page: number
  size: number
  totalPages: number
}

export interface Recommendation {
  type: string
  title: string
  description: string
  flights: Flight[]
}

export interface PriceTrend {
  date: string
  minPrice: number
  avgPrice: number
  maxPrice: number
}

export interface PriceAlert {
  id: number
  userId: number
  departureAirportCode: string
  arrivalAirportCode: string
  targetPrice: number
  currentPrice: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}