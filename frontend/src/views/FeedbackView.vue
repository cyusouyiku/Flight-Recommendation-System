<template>
  <div class="feedback-view">
    <div class="feedback-header">
      <h1>💬 用户反馈</h1>
      <p>您的反馈帮助我们优化推荐系统</p>
    </div>

    <div class="feedback-container">
      <div class="feedback-stats">
        <el-card class="stats-card">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ feedbackStats.total || 0 }}</div>
              <div class="stat-label">总反馈数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ feedbackStats.like || 0 }}</div>
              <div class="stat-label">喜欢</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ feedbackStats.dislike || 0 }}</div>
              <div class="stat-label">不喜欢</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ feedbackStats.neutral || 0 }}</div>
              <div class="stat-label">中立</div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="feedback-content">
        <div class="feedback-form-section">
          <el-card class="feedback-form-card">
            <template #header>
              <div class="card-header">
                <h3>提交新反馈</h3>
                <p>请选择航班并告诉我们您的感受</p>
              </div>
            </template>

            <el-form
              ref="feedbackFormRef"
              :model="feedbackForm"
              :rules="feedbackRules"
              label-width="100px"
              @submit.prevent="submitFeedback"
            >
              <el-form-item label="航班" prop="flightId">
                <el-select
                  v-model="feedbackForm.flightId"
                  placeholder="选择航班"
                  filterable
                  remote
                  :remote-method="searchFlights"
                  :loading="flightSearchLoading"
                  @change="handleFlightSelect"
                >
                  <el-option
                    v-for="flight in flightOptions"
                    :key="flight.id"
                    :label="`${flight.flightNumber} - ${flight.airlineName} (${flight.departureAirportCode}→${flight.arrivalAirportCode})`"
                    :value="flight.id"
                  />
                </el-select>
              </el-form-item>

              <div v-if="selectedFlight" class="selected-flight-info">
                <div class="flight-summary">
                  <h4>{{ selectedFlight.flightNumber }} - {{ selectedFlight.airlineName }}</h4>
                  <div class="flight-details">
                    <span>{{ selectedFlight.departureAirportCode }} → {{ selectedFlight.arrivalAirportCode }}</span>
                    <span>{{ formatTime(selectedFlight.departureTime) }} - {{ formatTime(selectedFlight.arrivalTime) }}</span>
                    <span>{{ formatDuration(selectedFlight.duration) }}</span>
                    <span class="price">¥{{ selectedFlight.price }}</span>
                  </div>
                </div>
              </div>

              <el-form-item label="反馈类型" prop="feedbackType">
                <el-radio-group v-model="feedbackForm.feedbackType">
                  <el-radio-button value="LIKE">
                    <el-icon><CircleCheck /></el-icon>
                    喜欢
                  </el-radio-button>
                  <el-radio-button value="DISLIKE">
                    <el-icon><CircleClose /></el-icon>
                    不喜欢
                  </el-radio-button>
                  <el-radio-button value="NEUTRAL">
                    <el-icon><InfoFilled /></el-icon>
                    中立
                  </el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="详细原因" prop="feedbackText">
                <el-input
                  v-model="feedbackForm.feedbackText"
                  type="textarea"
                  :rows="4"
                  placeholder="请详细说明您喜欢或不喜欢的原因（可选）"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="偏好标签">
                <el-select
                  v-model="feedbackForm.tags"
                  multiple
                  placeholder="选择相关偏好标签"
                  style="width: 100%"
                >
                  <el-option label="价格合理" value="price_reasonable" />
                  <el-option label="时间合适" value="time_convenient" />
                  <el-option label="航司偏好" value="airline_preferred" />
                  <el-option label="直飞优先" value="direct_flight" />
                  <el-option label="服务满意" value="service_good" />
                  <el-option label="座位舒适" value="seat_comfortable" />
                  <el-option label="餐食满意" value="food_good" />
                  <el-option label="准点率高" value="punctual" />
                </el-select>
              </el-form-item>

              <el-form-item>
                <div class="form-actions">
                  <el-button
                    type="primary"
                    :loading="submitting"
                    @click="submitFeedback"
                  >
                    <el-icon><Upload /></el-icon>
                    提交反馈
                  </el-button>
                  <el-button @click="resetForm">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                  <el-button type="success" @click="goToAiOptimization">
                    <el-icon><MagicStick /></el-icon>
                    AI优化推荐
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <div class="feedback-history-section">
          <el-card class="history-card">
            <template #header>
              <div class="card-header">
                <h3>反馈历史</h3>
                <el-button
                  type="text"
                  @click="refreshFeedbackHistory"
                  :loading="historyLoading"
                >
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </template>

            <div v-if="feedbackHistory.length === 0" class="empty-history">
              <el-empty description="暂无反馈记录" />
            </div>

            <div v-else class="feedback-list">
              <div
                v-for="feedback in feedbackHistory"
                :key="feedback.id"
                class="feedback-item"
              >
                <div class="feedback-header">
                  <div class="flight-info">
                    <span class="flight-number">航班 {{ feedback.flightId }}</span>
                    <el-tag
                      size="small"
                      :type="getFeedbackTagType(feedback.feedbackType)"
                    >
                      {{ getFeedbackTypeText(feedback.feedbackType) }}
                    </el-tag>
                  </div>
                  <div class="feedback-time">
                    {{ formatDate(feedback.createdAt) }}
                  </div>
                </div>

                <div class="feedback-content">
                  <p v-if="feedback.feedbackText" class="feedback-text">
                    {{ feedback.feedbackText }}
                  </p>
                  <div v-if="feedback.aiAnalysis" class="ai-analysis">
                    <el-tag type="info" size="small">AI分析</el-tag>
                    <p class="analysis-text">{{ feedback.aiAnalysis }}</p>
                  </div>
                  <div v-if="feedback.confidenceScore" class="confidence-score">
                    <el-progress
                      :percentage="Math.round(feedback.confidenceScore * 100)"
                      :color="getConfidenceColor(feedback.confidenceScore)"
                      :show-text="false"
                      :width="100"
                    />
                    <span class="score-label">
                      置信度: {{ Math.round(feedback.confidenceScore * 100) }}%
                    </span>
                  </div>
                </div>

                <div class="feedback-actions">
                  <el-button size="small" text @click="editFeedback(feedback)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    text
                    type="danger"
                    @click="deleteFeedback(feedback.id)"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <div class="ai-optimization-section" v-if="showOptimizationResults">
        <el-card class="optimization-card">
          <template #header>
            <div class="card-header">
              <h3>🎯 AI优化结果</h3>
              <el-tag type="success">
                <el-icon><SuccessFilled /></el-icon>
                优化完成
              </el-tag>
            </div>
          </template>

          <div class="optimization-content">
            <div class="optimization-analysis">
              <h4>AI分析</h4>
              <p>{{ optimizationResults.aiAnalysis }}</p>
            </div>

            <div class="confidence-score">
              <h4>优化置信度</h4>
              <div class="score-display">
                <el-progress
                  type="circle"
                  :percentage="Math.round(optimizationResults.confidenceScore * 100)"
                  :color="getConfidenceColor(optimizationResults.confidenceScore)"
                  :width="100"
                />
                <div class="score-details">
                  <div class="score-value">
                    {{ Math.round(optimizationResults.confidenceScore * 100) }}%
                  </div>
                  <div class="score-label">优化质量</div>
                </div>
              </div>
            </div>

            <div class="optimized-recommendations" v-if="optimizationResults.rerankedRecommendations">
              <h4>重新优化的推荐</h4>
              <div class="recommendation-list">
                <!-- 优化后的推荐列表 -->
              </div>
            </div>

            <div class="optimization-actions">
              <el-button type="primary" @click="applyOptimization">
                应用优化
              </el-button>
              <el-button @click="hideOptimizationResults">
                关闭
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  CircleCheck,
  CircleClose,
  InfoFilled,
  Upload,
  Refresh,
  MagicStick,
  Edit,
  Delete,
  SuccessFilled
} from '@element-plus/icons-vue'
import type { AiFeedbackRequest, AiFeedbackResponse, AiFeedbackRerankResponse } from '@/types/ai'
import type { Flight } from '@/types/flight'

const router = useRouter()

const feedbackFormRef = ref()
const submitting = ref(false)
const flightSearchLoading = ref(false)
const historyLoading = ref(false)
const showOptimizationResults = ref(false)

const flightOptions = ref<Flight[]>([])
const selectedFlight = ref<Flight | null>(null)
const feedbackHistory = ref<AiFeedbackResponse[]>([])

const feedbackStats = reactive({
  total: 0,
  like: 0,
  dislike: 0,
  neutral: 0
})

const feedbackForm = reactive({
  flightId: undefined as number | undefined,
  feedbackType: 'LIKE' as 'LIKE' | 'DISLIKE' | 'NEUTRAL',
  feedbackText: '',
  tags: [] as string[],
  metadata: {}
})

const optimizationResults = reactive({
  aiAnalysis: '',
  confidenceScore: 0,
  rerankedRecommendations: {} as Record<string, any[]>
})

const feedbackRules = {
  flightId: [
    { required: true, message: '请选择航班', trigger: 'blur' }
  ],
  feedbackType: [
    { required: true, message: '请选择反馈类型', trigger: 'change' }
  ]
}

const searchFlights = async (query: string) => {
  if (!query.trim()) {
    flightOptions.value = []
    return
  }

  flightSearchLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    // 这里应该调用实际的航班搜索API
    // const response = await api.get('/flights/search', { params: { q: query } })
    // flightOptions.value = response.data.flights

    // 模拟数据
    flightOptions.value = [
      {
        id: 1,
        flightNumber: 'CA1234',
        airlineId: 1,
        airlineName: '中国国际航空',
        departureAirportId: 1,
        departureAirportCode: 'PEK',
        departureAirportName: '北京首都国际机场',
        arrivalAirportId: 2,
        arrivalAirportCode: 'PVG',
        arrivalAirportName: '上海浦东国际机场',
        departureTime: '2026-04-20T08:00:00',
        arrivalTime: '2026-04-20T10:30:00',
        duration: 150,
        price: 1200,
        currency: 'CNY',
        seatClass: 'ECONOMY',
        stops: 0,
        aircraftType: 'A330',
        availableSeats: 45,
        isDirect: true,
        createdAt: '',
        updatedAt: ''
      },
      {
        id: 2,
        flightNumber: 'MU5678',
        airlineId: 2,
        airlineName: '中国东方航空',
        departureAirportId: 1,
        departureAirportCode: 'PEK',
        departureAirportName: '北京首都国际机场',
        arrivalAirportId: 2,
        arrivalAirportCode: 'PVG',
        arrivalAirportName: '上海浦东国际机场',
        departureTime: '2026-04-20T14:30:00',
        arrivalTime: '2026-04-20T17:15:00',
        duration: 165,
        price: 980,
        currency: 'CNY',
        seatClass: 'ECONOMY',
        stops: 0,
        aircraftType: 'B737',
        availableSeats: 32,
        isDirect: true,
        createdAt: '',
        updatedAt: ''
      }
    ]
  } catch (error) {
    ElMessage.error('搜索航班失败：' + (error as Error).message)
  } finally {
    flightSearchLoading.value = false
  }
}

const handleFlightSelect = (flightId: number) => {
  selectedFlight.value = flightOptions.value.find(f => f.id === flightId) || null
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return

  await feedbackFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    submitting.value = true
    try {
      // 构建请求数据
      const feedbackData: AiFeedbackRequest = {
        flightId: feedbackForm.flightId!,
        feedbackType: feedbackForm.feedbackType,
        feedbackText: feedbackForm.feedbackText || undefined,
        metadata: {
          tags: feedbackForm.tags,
          ...feedbackForm.metadata
        }
      }

      // 这里应该调用实际的API
      // await api.post('/ai/feedback', feedbackData)

      // 模拟成功
      await new Promise(resolve => setTimeout(resolve, 1000))

      ElMessage.success('反馈提交成功！AI正在分析您的反馈...')

      // 更新统计
      updateFeedbackStats(feedbackForm.feedbackType, 'add')

      // 刷新历史记录
      loadFeedbackHistory()

      // 重置表单
      resetForm()
    } catch (error) {
      ElMessage.error('提交反馈失败：' + (error as Error).message)
    } finally {
      submitting.value = false
    }
  })
}

const updateFeedbackStats = (type: string, action: 'add' | 'remove') => {
  const delta = action === 'add' ? 1 : -1
  feedbackStats.total += delta

  switch (type) {
    case 'LIKE':
      feedbackStats.like += delta
      break
    case 'DISLIKE':
      feedbackStats.dislike += delta
      break
    case 'NEUTRAL':
      feedbackStats.neutral += delta
      break
  }
}

const resetForm = () => {
  if (feedbackFormRef.value) {
    feedbackFormRef.value.resetFields()
  }
  selectedFlight.value = null
  feedbackForm.tags = []
}

const goToAiOptimization = () => {
  router.push('/ai-assistant')
}

const loadFeedbackHistory = async () => {
  historyLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 800))

    // 这里应该调用实际的API
    // const response = await api.get('/ai/feedback')
    // feedbackHistory.value = response.data

    // 模拟数据
    feedbackHistory.value = [
      {
        id: 1,
        userId: 1,
        flightId: 1,
        feedbackType: 'LIKE',
        feedbackText: '价格合理，时间合适',
        aiAnalysis: '用户对价格和时间表示满意，这是影响选择的关键因素',
        confidenceScore: 0.85,
        metadata: { tags: ['price_reasonable', 'time_convenient'] },
        createdAt: '2026-04-14T10:30:00',
        updatedAt: '2026-04-14T10:30:00'
      },
      {
        id: 2,
        userId: 1,
        flightId: 2,
        feedbackType: 'DISLIKE',
        feedbackText: '航空公司服务不满意',
        aiAnalysis: '用户对航空公司服务不满意，建议优化航司推荐策略',
        confidenceScore: 0.72,
        metadata: { tags: ['service_poor'] },
        createdAt: '2026-04-13T15:45:00',
        updatedAt: '2026-04-13T15:45:00'
      }
    ]

    // 更新统计
    calculateFeedbackStats()
  } catch (error) {
    ElMessage.error('加载反馈历史失败：' + (error as Error).message)
  } finally {
    historyLoading.value = false
  }
}

const calculateFeedbackStats = () => {
  feedbackStats.total = feedbackHistory.value.length
  feedbackStats.like = feedbackHistory.value.filter(f => f.feedbackType === 'LIKE').length
  feedbackStats.dislike = feedbackHistory.value.filter(f => f.feedbackType === 'DISLIKE').length
  feedbackStats.neutral = feedbackHistory.value.filter(f => f.feedbackType === 'NEUTRAL').length
}

const refreshFeedbackHistory = () => {
  loadFeedbackHistory()
}

const editFeedback = (feedback: AiFeedbackResponse) => {
  ElMessage.info('编辑功能开发中')
}

const deleteFeedback = async (feedbackId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条反馈吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 这里应该调用实际的API
    // await api.delete(`/ai/feedback/${feedbackId}`)

    // 从本地列表中移除
    const index = feedbackHistory.value.findIndex(f => f.id === feedbackId)
    if (index !== -1) {
      const feedback = feedbackHistory.value[index]
      updateFeedbackStats(feedback.feedbackType, 'remove')
      feedbackHistory.value.splice(index, 1)
    }

    ElMessage.success('反馈已删除')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error as Error).message)
    }
  }
}

const formatTime = (timeStr: string) => {
  return new Date(timeStr).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDuration = (minutes: number) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}h ${mins}m`
}

const getFeedbackTagType = (feedbackType: string) => {
  switch (feedbackType) {
    case 'LIKE': return 'success'
    case 'DISLIKE': return 'danger'
    default: return 'info'
  }
}

const getFeedbackTypeText = (feedbackType: string) => {
  switch (feedbackType) {
    case 'LIKE': return '喜欢'
    case 'DISLIKE': return '不喜欢'
    default: return '中立'
  }
}

const getConfidenceColor = (score: number) => {
  if (score >= 0.8) return '#67c23a'
  if (score >= 0.6) return '#e6a23c'
  return '#f56c6c'
}

const applyOptimization = () => {
  ElMessage.success('优化策略已应用')
  showOptimizationResults.value = false
}

const hideOptimizationResults = () => {
  showOptimizationResults.value = false
}

onMounted(() => {
  loadFeedbackHistory()
})
</script>

<style scoped>
.feedback-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.feedback-header {
  text-align: center;
  margin-bottom: 30px;
}

.feedback-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.feedback-header p {
  color: #666;
}

.feedback-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feedback-stats {
  margin-bottom: 10px;
}

.stats-card {
  border-radius: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
}

.feedback-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.feedback-form-card,
.history-card {
  border-radius: 12px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
}

.card-header p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 0.9rem;
}

.selected-flight-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.flight-summary h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.flight-details {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 0.9rem;
  color: #666;
}

.flight-details .price {
  font-weight: bold;
  color: #f56c6c;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 600px;
  overflow-y: auto;
}

.feedback-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
}

.feedback-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.feedback-header {
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

.flight-number {
  font-weight: bold;
  color: #333;
}

.feedback-time {
  font-size: 0.85rem;
  color: #999;
}

.feedback-content {
  margin-bottom: 10px;
}

.feedback-text {
  margin: 10px 0;
  color: #666;
  line-height: 1.5;
}

.ai-analysis {
  margin: 10px 0;
  padding: 10px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.analysis-text {
  margin: 5px 0 0 0;
  color: #333;
  font-size: 0.9rem;
  line-height: 1.4;
}

.confidence-score {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.score-label {
  font-size: 0.85rem;
  color: #666;
  min-width: 80px;
}

.feedback-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.ai-optimization-section {
  margin-top: 20px;
}

.optimization-card {
  border-radius: 12px;
}

.optimization-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.optimization-analysis h4,
.confidence-score h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.optimization-analysis p {
  color: #666;
  line-height: 1.6;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 20px;
}

.score-details {
  text-align: center;
}

.score-value {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.score-label {
  font-size: 0.9rem;
  color: #666;
}

.optimization-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.empty-history {
  padding: 40px 0;
}

@media (max-width: 992px) {
  .feedback-content {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .optimization-content {
    grid-template-columns: 1fr;
  }
}
</style>