<template>
  <div class="recommendations-view">
    <div class="recommendations-header">
      <h1>✨ 个性化推荐</h1>
      <p>基于您的偏好和AI分析的智能航班推荐</p>
    </div>

    <div class="recommendation-tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="AI个性化推荐" name="ai">
          <div class="tab-content">
            <div class="ai-recommendation-form">
              <el-form
                ref="aiRecommendFormRef"
                :model="aiRecommendForm"
                label-width="120px"
                @submit.prevent="getAiRecommendations"
              >
                <div class="form-row">
                  <el-form-item label="出发地" prop="departure">
                    <el-input
                      v-model="aiRecommendForm.departure"
                      placeholder="例如：PEK（北京首都）"
                      :prefix-icon="Location"
                    />
                  </el-form-item>

                  <el-form-item label="目的地" prop="arrival">
                    <el-input
                      v-model="aiRecommendForm.arrival"
                      placeholder="例如：PVG（上海浦东）"
                      :prefix-icon="Location"
                    />
                  </el-form-item>
                </div>

                <div class="form-row">
                  <el-form-item label="出发日期" prop="departureDate">
                    <el-date-picker
                      v-model="aiRecommendForm.departureDate"
                      type="date"
                      placeholder="选择出发日期"
                      :prefix-icon="Calendar"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>

                  <el-form-item label="预算" prop="budget">
                    <el-input-number
                      v-model="aiRecommendForm.budget"
                      :min="0"
                      :max="10000"
                      placeholder="最高预算"
                      :prefix-icon="Coin"
                    />
                  </el-form-item>
                </div>

                <el-collapse v-model="activePreferences">
                  <el-collapse-item title="偏好设置" name="preferences">
                    <div class="preferences-form">
                      <el-form-item label="首选航司">
                        <el-select
                          v-model="aiRecommendForm.preferences.preferredAirlines"
                          multiple
                          placeholder="选择偏好的航空公司"
                          clearable
                        >
                          <el-option label="中国国际航空" value="CA" />
                          <el-option label="中国东方航空" value="MU" />
                          <el-option label="中国南方航空" value="CZ" />
                          <el-option label="海南航空" value="HU" />
                        </el-select>
                      </el-form-item>

                      <el-form-item label="舱位偏好">
                        <el-select
                          v-model="aiRecommendForm.preferences.preferredSeatClass"
                          placeholder="选择偏好的舱位"
                          clearable
                        >
                          <el-option label="经济舱" value="ECONOMY" />
                          <el-option label="商务舱" value="BUSINESS" />
                          <el-option label="头等舱" value="FIRST" />
                        </el-select>
                      </el-form-item>

                      <el-form-item label="最大中转次数">
                        <el-select
                          v-model="aiRecommendForm.preferences.maxStops"
                          placeholder="选择最大中转次数"
                          clearable
                        >
                          <el-option label="直飞" :value="0" />
                          <el-option label="1次中转" :value="1" />
                          <el-option label="2次中转" :value="2" />
                        </el-select>
                      </el-form-item>

                      <el-form-item label="时间偏好">
                        <el-select
                          v-model="aiRecommendForm.preferences.timePreference"
                          placeholder="选择偏好的出发时间"
                          clearable
                        >
                          <el-option label="早晨" value="MORNING" />
                          <el-option label="下午" value="AFTERNOON" />
                          <el-option label="晚上" value="EVENING" />
                          <el-option label="任意时间" value="ANY" />
                        </el-select>
                      </el-form-item>
                    </div>
                  </el-collapse-item>
                </el-collapse>

                <div class="form-actions">
                  <el-button type="primary" :loading="aiLoading" @click="getAiRecommendations">
                    <el-icon><MagicStick /></el-icon>
                    AI智能推荐
                  </el-button>
                  <el-button @click="resetAiForm">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </div>
              </el-form>
            </div>

            <div v-if="aiRecommendations" class="ai-recommendations-results">
              <div class="results-header">
                <h2>AI推荐结果</h2>
                <div class="ai-analysis" v-if="aiAnalysis">
                  <el-tag type="info">
                    <el-icon><InfoFilled /></el-icon>
                    AI分析：{{ aiAnalysis }}
                  </el-tag>
                </div>
              </div>

              <div class="recommendation-categories">
                <div
                  v-for="(category, categoryName) in aiRecommendations"
                  :key="categoryName"
                  class="category-section"
                >
                  <h3>{{ getCategoryTitle(categoryName) }}</h3>
                  <p class="category-description">{{ getCategoryDescription(categoryName) }}</p>

                  <div class="flight-list">
                    <div
                      v-for="flight in category"
                      :key="flight.id"
                      class="flight-card"
                    >
                      <div class="flight-header">
                        <div class="flight-info">
                          <h4>{{ flight.flightNumber }} - {{ flight.airlineName }}</h4>
                          <el-tag :type="getSeatClassTagType(flight.seatClass)" size="small">
                            {{ formatSeatClass(flight.seatClass) }}
                          </el-tag>
                        </div>
                        <div class="flight-price">
                          <span class="price">{{ flight.price.toLocaleString() }}</span>
                          <span class="currency">{{ flight.currency }}</span>
                        </div>
                      </div>

                      <div class="flight-details">
                        <div class="route-summary">
                          <span class="departure">{{ flight.departureAirportCode }}</span>
                          <el-icon><Right /></el-icon>
                          <span class="arrival">{{ flight.arrivalAirportCode }}</span>
                          <span class="duration">{{ formatDuration(flight.duration) }}</span>
                          <el-tag size="small" :type="flight.stops > 0 ? 'info' : 'success'">
                            {{ flight.stops > 0 ? `${flight.stops}次中转` : '直飞' }}
                          </el-tag>
                        </div>
                        <div class="time-info">
                          <span>{{ formatTime(flight.departureTime) }} - {{ formatTime(flight.arrivalTime) }}</span>
                        </div>
                      </div>

                      <div class="flight-actions">
                        <el-button type="primary" size="small" @click="viewFlightDetails(flight)">
                          查看详情
                        </el-button>
                        <el-button type="success" size="small" @click="bookFlight(flight)">
                          立即预订
                        </el-button>
                        <el-button size="small" @click="giveFeedback(flight, 'LIKE')">
                          喜欢
                        </el-button>
                        <el-button size="small" @click="giveFeedback(flight, 'DISLIKE')">
                          不喜欢
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="热门推荐" name="popular">
          <div class="tab-content">
            <div v-if="popularRecommendations" class="popular-recommendations">
              <div class="recommendation-categories">
                <div class="category-section">
                  <h3>🔥 热门航线</h3>
                  <p class="category-description">近期搜索量最高的热门航线</p>
                  <div class="flight-list">
                    <!-- 热门航线列表 -->
                    <div v-for="flight in popularRecommendations" :key="flight.id" class="flight-card">
                      <!-- 航班卡片内容 -->
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <el-button type="primary" @click="loadPopularRecommendations">
                加载热门推荐
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="常搜航线" name="frequent">
          <div class="tab-content">
            <div v-if="frequentRecommendations" class="frequent-recommendations">
              <!-- 常搜航线内容 -->
            </div>
            <div v-else class="empty-state">
              <el-empty description="您还没有常搜航线记录" />
              <el-button type="primary" @click="$router.push('/search')">
                开始搜索航班
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Location,
  Calendar,
  Coin,
  MagicStick,
  Refresh,
  Right,
  InfoFilled
} from '@element-plus/icons-vue'
import type { AiRecommendRequest } from '@/types/ai'
import type { Flight } from '@/types/flight'

const router = useRouter()

const activeTab = ref('ai')
const activePreferences = ref(['preferences'])
const aiLoading = ref(false)
const aiRecommendations = ref<Record<string, Flight[]>>({})
const aiAnalysis = ref('')
const popularRecommendations = ref<Flight[] | null>(null)
const frequentRecommendations = ref<Flight[] | null>(null)

const aiRecommendFormRef = ref()

const aiRecommendForm = reactive({
  departure: '',
  arrival: '',
  departureDate: '',
  budget: 0,
  preferences: {
    preferredAirlines: [] as string[],
    preferredSeatClass: '',
    maxStops: undefined as number | undefined,
    timePreference: ''
  }
})

const handleTabChange = (tabName: string | number | undefined) => {
  if (tabName === 'popular' && !popularRecommendations.value) {
    loadPopularRecommendations()
  }
}

const getAiRecommendations = async () => {
  aiLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 这里应该调用实际的API
    // const response = await api.post('/ai/recommend', aiRecommendForm)
    // aiRecommendations.value = response.data.recommendations
    // aiAnalysis.value = response.data.analysis

    // 模拟数据
    aiRecommendations.value = {
      personalized: [
        {
          id: 101,
          flightNumber: 'CA1501',
          airlineId: 1,
          airlineName: '中国国际航空',
          departureAirportId: 1,
          departureAirportCode: 'PEK',
          departureAirportName: '北京首都国际机场',
          arrivalAirportId: 2,
          arrivalAirportCode: 'SHA',
          arrivalAirportName: '上海虹桥国际机场',
          departureTime: '2026-04-20T08:30:00',
          arrivalTime: '2026-04-20T10:45:00',
          duration: 135,
          price: 950,
          currency: 'CNY',
          seatClass: 'ECONOMY',
          stops: 0,
          aircraftType: 'A320',
          availableSeats: 28,
          isDirect: true,
          createdAt: '',
          updatedAt: ''
        },
        {
          id: 102,
          flightNumber: 'MU5112',
          airlineId: 2,
          airlineName: '中国东方航空',
          departureAirportId: 1,
          departureAirportCode: 'PEK',
          departureAirportName: '北京首都国际机场',
          arrivalAirportId: 2,
          arrivalAirportCode: 'SHA',
          arrivalAirportName: '上海虹桥国际机场',
          departureTime: '2026-04-20T14:00:00',
          arrivalTime: '2026-04-20T16:10:00',
          duration: 130,
          price: 880,
          currency: 'CNY',
          seatClass: 'ECONOMY',
          stops: 0,
          aircraftType: 'B737',
          availableSeats: 35,
          isDirect: true,
          createdAt: '',
          updatedAt: ''
        }
      ],
      valueForMoney: [
        {
          id: 103,
          flightNumber: 'HU7618',
          airlineId: 4,
          airlineName: '海南航空',
          departureAirportId: 1,
          departureAirportCode: 'PEK',
          departureAirportName: '北京首都国际机场',
          arrivalAirportId: 2,
          arrivalAirportCode: 'PVG',
          arrivalAirportName: '上海浦东国际机场',
          departureTime: '2026-04-20T19:30:00',
          arrivalTime: '2026-04-20T22:00:00',
          duration: 150,
          price: 720,
          currency: 'CNY',
          seatClass: 'ECONOMY',
          stops: 0,
          aircraftType: 'A330',
          availableSeats: 42,
          isDirect: true,
          createdAt: '',
          updatedAt: ''
        }
      ]
    }

    aiAnalysis.value = '根据您的偏好（经济舱、直飞、早晨出发），AI为您筛选了这些航班。国航CA1501时间最优，东航MU5112价格适中，海航HU7618性价比最高。'

    ElMessage.success('AI推荐生成成功')
  } catch (error) {
    ElMessage.error('AI推荐失败：' + (error as Error).message)
  } finally {
    aiLoading.value = false
  }
}

const resetAiForm = () => {
  if (aiRecommendFormRef.value) {
    aiRecommendFormRef.value.resetFields()
  }
  aiRecommendations.value = {}
  aiAnalysis.value = ''
}

const loadPopularRecommendations = async () => {
  // 加载热门推荐
  ElMessage.info('加载热门推荐')
}

const getCategoryTitle = (categoryName: string) => {
  const titles: Record<string, string> = {
    personalized: '个性化推荐',
    valueForMoney: '高性价比',
    premium: '尊享体验',
    lastMinute: '最后机会'
  }
  return titles[categoryName] || categoryName
}

const getCategoryDescription = (categoryName: string) => {
  const descriptions: Record<string, string> = {
    personalized: '最符合您个人偏好的航班',
    valueForMoney: '价格优惠且体验良好的选择',
    premium: '提供额外服务和舒适体验',
    lastMinute: '近期出发的超值航班'
  }
  return descriptions[categoryName] || ''
}

const getSeatClassTagType = (seatClass: string) => {
  switch (seatClass) {
    case 'FIRST': return 'warning'
    case 'BUSINESS': return 'success'
    default: return 'info'
  }
}

const formatSeatClass = (seatClass: string) => {
  switch (seatClass) {
    case 'FIRST': return '头等舱'
    case 'BUSINESS': return '商务舱'
    default: return '经济舱'
  }
}

const formatTime = (timeStr: string) => {
  return new Date(timeStr).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDuration = (minutes: number) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}h ${mins}m`
}

const viewFlightDetails = (flight: Flight) => {
  ElMessage.info(`查看航班 ${flight.flightNumber} 的详情`)
}

const bookFlight = (flight: Flight) => {
  ElMessage.success(`开始预订 ${flight.flightNumber}`)
}

const giveFeedback = (flight: Flight, feedbackType: 'LIKE' | 'DISLIKE') => {
  router.push(`/feedback?flightId=${flight.id}&feedbackType=${feedbackType}`)
}
</script>

<style scoped>
.recommendations-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.recommendations-header {
  text-align: center;
  margin-bottom: 30px;
}

.recommendations-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.recommendations-header p {
  color: #666;
}

.recommendation-tabs {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.tab-content {
  margin-top: 20px;
}

.ai-recommendation-form {
  margin-bottom: 30px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.preferences-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.ai-recommendations-results {
  margin-top: 30px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h2 {
  color: #333;
}

.ai-analysis {
  max-width: 60%;
}

.recommendation-categories {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.category-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.category-section h3 {
  color: #333;
  margin-bottom: 10px;
}

.category-description {
  color: #666;
  margin-bottom: 20px;
  font-size: 0.95rem;
}

.flight-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.flight-card {
  background: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.1);
}

.flight-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.flight-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.flight-info h4 {
  margin: 0;
  color: #333;
}

.flight-price {
  text-align: right;
}

.price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #f56c6c;
}

.currency {
  font-size: 0.9rem;
  color: #999;
  margin-left: 5px;
}

.flight-details {
  margin-bottom: 10px;
}

.route-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.route-summary .departure,
.route-summary .arrival {
  font-weight: bold;
  font-size: 1.2rem;
}

.route-summary .duration {
  color: #666;
  font-size: 0.9rem;
  margin-left: 10px;
}

.time-info {
  color: #666;
  font-size: 0.9rem;
}

.flight-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
}
</style>