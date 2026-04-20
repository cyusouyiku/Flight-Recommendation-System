<template>
  <div class="price-trend-view">
    <div class="trend-header">
      <h1>📈 价格趋势分析</h1>
      <p>实时监控航班价格变化，把握最佳购买时机</p>
    </div>

    <div class="trend-container">
      <div class="trend-controls">
        <el-card class="controls-card">
          <div class="controls-form">
            <el-form
              ref="trendFormRef"
              :model="trendForm"
              label-width="120px"
              @submit.prevent="loadPriceTrend"
            >
              <div class="form-row">
                <el-form-item label="出发地" prop="departure">
                  <el-input
                    v-model="trendForm.departure"
                    placeholder="例如：PEK（北京首都）"
                    :prefix-icon="Location"
                  />
                </el-form-item>

                <el-form-item label="目的地" prop="arrival">
                  <el-input
                    v-model="trendForm.arrival"
                    placeholder="例如：PVG（上海浦东）"
                    :prefix-icon="Location"
                  />
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="出发日期" prop="departureDate">
                  <el-date-picker
                    v-model="trendForm.departureDate"
                    type="date"
                    placeholder="选择出发日期"
                    :prefix-icon="Calendar"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>

                <el-form-item label="分析周期" prop="period">
                  <el-select v-model="trendForm.period" placeholder="选择分析周期">
                    <el-option label="最近7天" value="7d" />
                    <el-option label="最近30天" value="30d" />
                    <el-option label="最近90天" value="90d" />
                    <el-option label="自定义" value="custom" />
                  </el-select>
                </el-form-item>
              </div>

              <div class="form-actions">
                <el-button type="primary" :loading="loading" @click="loadPriceTrend">
                  <el-icon><Search /></el-icon>
                  分析价格趋势
                </el-button>
                <el-button @click="resetForm">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
                <el-button type="success" @click="showPriceAlertDialog">
                  <el-icon><Bell /></el-icon>
                  设置价格提醒
                </el-button>
              </div>
            </el-form>
          </div>
        </el-card>
      </div>

      <div v-if="priceTrendData" class="trend-results">
        <div class="results-summary">
          <el-card class="summary-card">
            <div class="summary-grid">
              <div class="summary-item">
                <div class="summary-label">当前最低价</div>
                <div class="summary-value">¥{{ currentMinPrice }}</div>
                <div class="summary-change" :class="getChangeClass(priceChange)">
                  <el-icon v-if="priceChange > 0"><Top /></el-icon>
                  <el-icon v-else-if="priceChange < 0"><Bottom /></el-icon>
                  <span>{{ Math.abs(priceChange) }}%</span>
                </div>
              </div>
              <div class="summary-item">
                <div class="summary-label">平均价格</div>
                <div class="summary-value">¥{{ averagePrice }}</div>
                <div class="summary-sub">历史平均</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">价格波动</div>
                <div class="summary-value">{{ priceVolatility }}%</div>
                <div class="summary-sub">波动率</div>
              </div>
              <div class="summary-item">
                <div class="summary-label">建议购买</div>
                <div class="summary-value" :class="getBuyRecommendationClass(buyRecommendation)">
                  {{ buyRecommendation }}
                </div>
                <div class="summary-sub">AI建议</div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="trend-chart-section">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <h3>价格趋势图</h3>
                <div class="chart-controls">
                  <el-button-group>
                    <el-button :type="chartType === 'line' ? 'primary' : ''" @click="chartType = 'line'">
                      折线图
                    </el-button>
                    <el-button :type="chartType === 'area' ? 'primary' : ''" @click="chartType = 'area'">
                      面积图
                    </el-button>
                    <el-button :type="chartType === 'bar' ? 'primary' : ''" @click="chartType = 'bar'">
                      柱状图
                    </el-button>
                  </el-button-group>
                </div>
              </div>
            </template>

            <div class="chart-container">
              <div class="mock-chart">
                <div class="chart-placeholder">
                  <div class="chart-title">价格趋势可视化图表</div>
                  <div class="chart-desc">
                    实际项目中可接入ECharts或Chart.js展示详细价格走势
                  </div>
                  <div class="chart-simulation">
                    <div class="chart-grid">
                      <div class="grid-line" v-for="i in 5" :key="i" :style="{ bottom: `${i * 20}%` }"></div>
                      <div
                        v-for="(point, index) in simulatedChartData"
                        :key="index"
                        class="chart-point"
                        :style="{
                          left: `${index * 15}%`,
                          bottom: `${(point.price - minSimulatedPrice) / (maxSimulatedPrice - minSimulatedPrice) * 80}%`
                        }"
                      >
                        <div class="point-value">¥{{ point.price }}</div>
                      </div>
                      <div class="chart-line"></div>
                    </div>
                    <div class="chart-xaxis">
                      <div
                        v-for="(point, index) in simulatedChartData"
                        :key="index"
                        class="xaxis-label"
                        :style="{ left: `${index * 15}%` }"
                      >
                        {{ point.date }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="chart-legend">
              <div class="legend-item">
                <div class="legend-color" style="background: #409eff;"></div>
                <span>最低价格</span>
              </div>
              <div class="legend-item">
                <div class="legend-color" style="background: #67c23a;"></div>
                <span>平均价格</span>
              </div>
              <div class="legend-item">
                <div class="legend-color" style="background: #e6a23c;"></div>
                <span>最高价格</span>
              </div>
              <div class="legend-item">
                <div class="legend-color" style="background: #f56c6c; border: 2px dashed #f56c6c;"></div>
                <span>您的目标价格</span>
              </div>
            </div>
          </el-card>
        </div>

        <div class="ai-analysis-section">
          <el-card class="analysis-card">
            <template #header>
              <div class="card-header">
                <h3>🤖 AI价格分析</h3>
                <el-tag type="success">
                  <el-icon><MagicStick /></el-icon>
                  智能分析
                </el-tag>
              </div>
            </template>

            <div class="analysis-content">
              <div class="analysis-text">
                <p>{{ aiAnalysis }}</p>
              </div>

              <div class="analysis-insights">
                <h4>关键洞察</h4>
                <div class="insights-list">
                  <div class="insight-item">
                    <el-icon><TrendCharts /></el-icon>
                    <div class="insight-content">
                      <div class="insight-title">价格走势</div>
                      <div class="insight-desc">未来一周价格可能{{ futureTrend }}</div>
                    </div>
                  </div>
                  <div class="insight-item">
                    <el-icon><Timer /></el-icon>
                    <div class="insight-content">
                      <div class="insight-title">最佳购买时间</div>
                      <div class="insight-desc">建议{{ bestTimeToBuy }}</div>
                    </div>
                  </div>
                  <div class="insight-item">
                    <el-icon><Money /></el-icon>
                    <div class="insight-content">
                      <div class="insight-title">价格预测</div>
                      <div class="insight-desc">预计最低可达¥{{ predictedMinPrice }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="analysis-actions">
                <el-button type="primary" @click="showPriceAlertDialog">
                  <el-icon><Bell /></el-icon>
                  设置¥{{ targetPrice }}提醒
                </el-button>
                <el-button @click="askAiAssistant">
                  <el-icon><ChatLineRound /></el-icon>
                  咨询AI助手
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <div class="price-alerts-section">
          <el-card class="alerts-card">
            <template #header>
              <div class="card-header">
                <h3>🔔 我的价格提醒</h3>
                <el-button type="primary" text @click="showPriceAlertDialog">
                  <el-icon><Plus /></el-icon>
                  新增提醒
                </el-button>
              </div>
            </template>

            <div v-if="priceAlerts.length === 0" class="empty-alerts">
              <el-empty description="暂无价格提醒" />
            </div>

            <div v-else class="alerts-list">
              <div
                v-for="alert in priceAlerts"
                :key="alert.id"
                class="alert-item"
                :class="{ active: alert.isActive }"
              >
                <div class="alert-info">
                  <div class="alert-route">
                    <span class="route-codes">{{ alert.departureAirportCode }} → {{ alert.arrivalAirportCode }}</span>
                    <el-tag size="small" :type="alert.isActive ? 'success' : 'info'">
                      {{ alert.isActive ? '监控中' : '已暂停' }}
                    </el-tag>
                  </div>
                  <div class="alert-price">
                    <div class="target-price">
                      <span class="label">目标价格:</span>
                      <span class="value">¥{{ alert.targetPrice }}</span>
                    </div>
                    <div class="current-price">
                      <span class="label">当前价格:</span>
                      <span class="value">¥{{ alert.currentPrice }}</span>
                    </div>
                  </div>
                  <div class="alert-meta">
                    <span class="meta-item">
                      <el-icon><Calendar /></el-icon>
                      {{ formatDate(alert.createdAt) }}
                    </span>
                    <!-- <span class="meta-item" v-if="alert.lastNotified">
                      <el-icon><Notification /></el-icon>
                      上次通知: {{ formatDate(alert.lastNotified) }}
                    </span> -->
                  </div>
                </div>

                <div class="alert-actions">
                  <el-button
                    size="small"
                    :type="alert.isActive ? 'warning' : 'success'"
                    @click="toggleAlertStatus(alert)"
                  >
                    {{ alert.isActive ? '暂停' : '启用' }}
                  </el-button>
                  <el-button size="small" @click="editAlert(alert)">
                    编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteAlert(alert.id)">
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-empty description="输入航线信息开始价格分析" />
        <div class="example-routes">
          <h4>示例航线</h4>
          <div class="route-buttons">
            <el-button @click="loadExample('PEK', 'PVG')">
              北京(PEK) → 上海(PVG)
            </el-button>
            <el-button @click="loadExample('CAN', 'SHA')">
              广州(CAN) → 上海(SHA)
            </el-button>
            <el-button @click="loadExample('CTU', 'PEK')">
              成都(CTU) → 北京(PEK)
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 价格提醒对话框 -->
    <el-dialog
      v-model="priceAlertDialogVisible"
      title="设置价格提醒"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="priceAlertFormRef"
        :model="priceAlertForm"
        :rules="priceAlertRules"
        label-width="100px"
      >
        <el-form-item label="出发地" prop="departure">
          <el-input
            v-model="priceAlertForm.departure"
            placeholder="例如：PEK"
          />
        </el-form-item>

        <el-form-item label="目的地" prop="arrival">
          <el-input
            v-model="priceAlertForm.arrival"
            placeholder="例如：PVG"
          />
        </el-form-item>

        <el-form-item label="目标价格" prop="targetPrice">
          <el-input-number
            v-model="priceAlertForm.targetPrice"
            :min="0"
            :max="10000"
            placeholder="期望价格"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="通知方式">
          <el-checkbox-group v-model="priceAlertForm.notificationMethods">
            <el-checkbox label="email">邮件</el-checkbox>
            <el-checkbox label="sms">短信</el-checkbox>
            <el-checkbox label="push">推送</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="有效期" prop="expiryDate">
          <el-date-picker
            v-model="priceAlertForm.expiryDate"
            type="date"
            placeholder="选择提醒有效期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="priceAlertDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createPriceAlert" :loading="creatingAlert">
            创建提醒
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Location,
  Calendar,
  Search,
  Refresh,
  Bell,
  Top,
  Bottom,
  MagicStick,
  TrendCharts,
  Timer,
  Money,
  ChatLineRound,
  Plus,
  Notification
} from '@element-plus/icons-vue'
import type { PriceTrend, PriceAlert } from '@/types/flight'

const router = useRouter()

const trendFormRef = ref()
const priceAlertFormRef = ref()
const loading = ref(false)
const creatingAlert = ref(false)
const priceAlertDialogVisible = ref(false)
const chartType = ref('line')

const priceTrendData = ref<PriceTrend[] | null>(null)
const priceAlerts = ref<PriceAlert[]>([
  {
    id: 1,
    userId: 1,
    departureAirportCode: 'PEK',
    arrivalAirportCode: 'PVG',
    targetPrice: 800,
    currentPrice: 1200,
    isActive: true,
    createdAt: '2026-04-10T09:30:00',
    updatedAt: '2026-04-15T14:20:00'
  },
  {
    id: 2,
    userId: 1,
    departureAirportCode: 'CAN',
    arrivalAirportCode: 'SHA',
    targetPrice: 600,
    currentPrice: 750,
    isActive: false,
    createdAt: '2026-04-05T11:15:00',
    updatedAt: '2026-04-12T16:45:00'
  }
])

const trendForm = reactive({
  departure: '',
  arrival: '',
  departureDate: '',
  period: '30d'
})

const priceAlertForm = reactive({
  departure: '',
  arrival: '',
  targetPrice: 0,
  notificationMethods: ['email'],
  expiryDate: ''
})

const priceAlertRules = {
  departure: [
    { required: true, message: '请输入出发地', trigger: 'blur' }
  ],
  arrival: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  targetPrice: [
    { required: true, message: '请输入目标价格', trigger: 'blur' }
  ]
}

// 模拟数据
const currentMinPrice = ref(950)
const averagePrice = ref(1200)
const priceChange = ref(-12.5)
const priceVolatility = ref(18.2)
const buyRecommendation = ref('立即购买')
const aiAnalysis = ref('根据历史数据，当前价格处于近30天的低位区间，相比30天前下降了12.5%。价格波动率适中，建议在价格回升前购买。未来一周可能有小幅上涨趋势。')
const futureTrend = ref('上涨5-10%')
const bestTimeToBuy = ref('未来3天内购买')
const predictedMinPrice = ref(920)
const targetPrice = ref(800)

const simulatedChartData = [
  { date: '4/1', price: 1500 },
  { date: '4/5', price: 1350 },
  { date: '4/10', price: 1250 },
  { date: '4/15', price: 1200 },
  { date: '4/20', price: 1150 },
  { date: '4/25', price: 1050 },
  { date: '4/30', price: 950 }
]

const minSimulatedPrice = Math.min(...simulatedChartData.map(d => d.price))
const maxSimulatedPrice = Math.max(...simulatedChartData.map(d => d.price))

const loadPriceTrend = async () => {
  if (!trendForm.departure || !trendForm.arrival) {
    ElMessage.warning('请输入出发地和目的地')
    return
  }

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 这里应该调用实际的API
    // const response = await api.get('/price/trend', { params: trendForm })
    // priceTrendData.value = response.data

    // 模拟数据
    priceTrendData.value = simulatedChartData.map((d, i) => ({
      date: d.date,
      minPrice: d.price * 0.9,
      avgPrice: d.price,
      maxPrice: d.price * 1.2
    }))

    ElMessage.success('价格趋势分析完成')
  } catch (error) {
    ElMessage.error('分析失败：' + (error as Error).message)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  if (trendFormRef.value) {
    trendFormRef.value.resetFields()
  }
  priceTrendData.value = null
}

const showPriceAlertDialog = () => {
  if (trendForm.departure && trendForm.arrival) {
    priceAlertForm.departure = trendForm.departure
    priceAlertForm.arrival = trendForm.arrival
  }
  priceAlertDialogVisible.value = true
}

const createPriceAlert = async () => {
  if (!priceAlertFormRef.value) return

  await priceAlertFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    creatingAlert.value = true
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))

      // 这里应该调用实际的API
      // await api.post('/price/alert', priceAlertForm)

      // 添加到本地列表
      const newAlert: PriceAlert = {
        id: Date.now(),
        userId: 1,
        departureAirportCode: priceAlertForm.departure.toUpperCase(),
        arrivalAirportCode: priceAlertForm.arrival.toUpperCase(),
        targetPrice: priceAlertForm.targetPrice,
        currentPrice: currentMinPrice.value,
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }

      priceAlerts.value.unshift(newAlert)

      ElMessage.success('价格提醒设置成功')
      priceAlertDialogVisible.value = false
      resetPriceAlertForm()
    } catch (error) {
      ElMessage.error('设置失败：' + (error as Error).message)
    } finally {
      creatingAlert.value = false
    }
  })
}

const resetPriceAlertForm = () => {
  if (priceAlertFormRef.value) {
    priceAlertFormRef.value.resetFields()
  }
  priceAlertForm.notificationMethods = ['email']
}

const toggleAlertStatus = async (alert: PriceAlert) => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    alert.isActive = !alert.isActive
    alert.updatedAt = new Date().toISOString()

    ElMessage.success(`价格提醒已${alert.isActive ? '启用' : '暂停'}`)
  } catch (error) {
    ElMessage.error('操作失败：' + (error as Error).message)
  }
}

const editAlert = (alert: PriceAlert) => {
  priceAlertForm.departure = alert.departureAirportCode
  priceAlertForm.arrival = alert.arrivalAirportCode
  priceAlertForm.targetPrice = alert.targetPrice
  priceAlertDialogVisible.value = true
}

const deleteAlert = async (alertId: number) => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    priceAlerts.value = priceAlerts.value.filter(a => a.id !== alertId)
    ElMessage.success('价格提醒已删除')
  } catch (error) {
    ElMessage.error('删除失败：' + (error as Error).message)
  }
}

const loadExample = (dep: string, arr: string) => {
  trendForm.departure = dep
  trendForm.arrival = arr
  trendForm.departureDate = '2026-04-20'
  loadPriceTrend()
}

const askAiAssistant = () => {
  router.push('/ai-assistant')
}

const getChangeClass = (change: number) => {
  if (change > 0) return 'change-up'
  if (change < 0) return 'change-down'
  return 'change-neutral'
}

const getBuyRecommendationClass = (recommendation: string) => {
  if (recommendation.includes('立即')) return 'recommend-buy'
  if (recommendation.includes('观望')) return 'recommend-wait'
  return 'recommend-neutral'
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}
</script>

<style scoped>
.price-trend-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.trend-header {
  text-align: center;
  margin-bottom: 30px;
}

.trend-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.trend-header p {
  color: #666;
}

.trend-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.controls-card {
  border-radius: 12px;
}

.controls-form {
  padding: 10px 0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.trend-results {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-card {
  border-radius: 12px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.summary-change {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 0.9rem;
}

.change-up {
  color: #f56c6c;
}

.change-down {
  color: #67c23a;
}

.change-neutral {
  color: #999;
}

.summary-sub {
  font-size: 0.85rem;
  color: #999;
}

.recommend-buy {
  color: #67c23a;
}

.recommend-wait {
  color: #e6a23c;
}

.recommend-neutral {
  color: #909399;
}

.chart-card {
  border-radius: 12px;
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

.chart-container {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mock-chart {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
}

.chart-title {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 10px;
}

.chart-desc {
  color: #666;
  margin-bottom: 30px;
  font-size: 0.9rem;
}

.chart-simulation {
  position: relative;
  width: 100%;
  height: 300px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.chart-grid {
  position: relative;
  width: 100%;
  height: 240px;
  margin-top: 40px;
}

.grid-line {
  position: absolute;
  left: 0;
  right: 0;
  border-top: 1px dashed #e4e7ed;
}

.chart-point {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #409eff;
  border-radius: 50%;
  transform: translate(-50%, 50%);
  z-index: 2;
}

.point-value {
  position: absolute;
  top: -25px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 0.8rem;
  color: #333;
  white-space: nowrap;
}

.chart-line {
  position: absolute;
  left: 7.5%;
  right: 7.5%;
  bottom: 0;
  height: 2px;
  background: #409eff;
  z-index: 1;
}

.chart-xaxis {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 40px;
  border-top: 1px solid #e4e7ed;
}

.xaxis-label {
  position: absolute;
  transform: translateX(-50%);
  font-size: 0.8rem;
  color: #666;
  text-align: center;
  width: 50px;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.analysis-card {
  border-radius: 12px;
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.analysis-text p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.analysis-insights h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.insights-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.insight-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.insight-item .el-icon {
  font-size: 1.2rem;
  color: #667eea;
  margin-top: 2px;
}

.insight-content {
  flex: 1;
}

.insight-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.insight-desc {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
}

.analysis-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 10px;
}

.alerts-card {
  border-radius: 12px;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.alert-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.alert-item.active {
  border-left-color: #67c23a;
}

.alert-info {
  flex: 1;
}

.alert-route {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.route-codes {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.alert-price {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.target-price,
.current-price {
  display: flex;
  align-items: center;
  gap: 5px;
}

.label {
  font-size: 0.9rem;
  color: #666;
}

.value {
  font-weight: bold;
  color: #333;
}

.target-price .value {
  color: #f56c6c;
}

.current-price .value {
  color: #333;
}

.alert-meta {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.alert-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
}

.example-routes {
  margin-top: 30px;
}

.example-routes h4 {
  margin-bottom: 15px;
  color: #666;
}

.route-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.empty-alerts {
  padding: 40px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 992px) {
  .form-row,
  .summary-grid,
  .insights-list {
    grid-template-columns: 1fr;
  }

  .alert-item {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .alert-actions {
    justify-content: flex-end;
  }
}
</style>