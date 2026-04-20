<template>
  <div class="search-view">
    <div class="search-header">
      <h1>🔍 航班搜索</h1>
      <p>查找最适合您的航班</p>
    </div>

    <el-card class="search-form-card">
      <el-form
        ref="searchFormRef"
        :model="searchForm"
        label-width="120px"
        @submit.prevent="handleSearch"
      >
        <div class="form-row">
          <el-form-item label="出发地" prop="departure">
            <el-input
              v-model="searchForm.departure"
              placeholder="例如：PEK（北京首都）"
              :prefix-icon="Location"
            />
          </el-form-item>

          <el-form-item label="目的地" prop="arrival">
            <el-input
              v-model="searchForm.arrival"
              placeholder="例如：PVG（上海浦东）"
              :prefix-icon="Location"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="出发日期" prop="departureDate">
            <el-date-picker
              v-model="searchForm.departureDate"
              type="date"
              placeholder="选择出发日期"
              :prefix-icon="Calendar"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <el-form-item label="返程日期" prop="returnDate">
            <el-date-picker
              v-model="searchForm.returnDate"
              type="date"
              placeholder="选择返程日期（可选）"
              :prefix-icon="Calendar"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </div>

        <el-collapse v-model="activeFilters">
          <el-collapse-item title="高级筛选" name="filters">
            <div class="advanced-filters">
              <el-form-item label="价格范围">
                <el-input-number
                  v-model="searchForm.minPrice"
                  :min="0"
                  :max="10000"
                  placeholder="最低价格"
                />
                <span class="filter-separator">至</span>
                <el-input-number
                  v-model="searchForm.maxPrice"
                  :min="0"
                  :max="10000"
                  placeholder="最高价格"
                />
              </el-form-item>

              <el-form-item label="舱位等级">
                <el-select
                  v-model="searchForm.seatClass"
                  placeholder="选择舱位等级"
                  clearable
                >
                  <el-option label="经济舱" value="ECONOMY" />
                  <el-option label="商务舱" value="BUSINESS" />
                  <el-option label="头等舱" value="FIRST" />
                </el-select>
              </el-form-item>

              <el-form-item label="航空公司">
                <el-select
                  v-model="searchForm.airlines"
                  multiple
                  placeholder="选择航空公司"
                  clearable
                >
                  <el-option label="中国国际航空" value="CA" />
                  <el-option label="中国东方航空" value="MU" />
                  <el-option label="中国南方航空" value="CZ" />
                  <el-option label="海南航空" value="HU" />
                  <el-option label="厦门航空" value="MF" />
                </el-select>
              </el-form-item>

              <el-form-item label="中转次数">
                <el-select
                  v-model="searchForm.stops"
                  placeholder="选择中转次数"
                  clearable
                >
                  <el-option label="直飞" :value="0" />
                  <el-option label="1次中转" :value="1" />
                  <el-option label="2次中转" :value="2" />
                </el-select>
              </el-form-item>
            </div>
          </el-collapse-item>
        </el-collapse>

        <div class="form-actions">
          <el-button type="primary" :loading="loading" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索航班
          </el-button>
          <el-button @click="resetForm">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="router.push('/ai-assistant')">
            <el-icon><ChatLineRound /></el-icon>
            AI助手帮搜
          </el-button>
        </div>
      </el-form>
    </el-card>

    <div v-if="searchResults" class="search-results">
      <div class="results-header">
        <h2>搜索结果（共{{ searchResults.total }}条）</h2>
        <div class="sort-options">
          <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
            <el-option label="价格从低到高" value="PRICE_ASC" />
            <el-option label="价格从高到低" value="PRICE_DESC" />
            <el-option label="时间从早到晚" value="TIME_ASC" />
            <el-option label="时长从短到长" value="DURATION_ASC" />
          </el-select>
        </div>
      </div>

      <div v-if="searchResults.flights.length === 0" class="empty-results">
        <el-empty description="未找到符合条件的航班" />
      </div>

      <div v-else class="flight-list">
        <div
          v-for="flight in searchResults.flights"
          :key="flight.id"
          class="flight-card"
        >
          <div class="flight-header">
            <div class="flight-info">
              <h3>{{ flight.flightNumber }} - {{ flight.airlineName }}</h3>
              <el-tag :type="flight.seatClass === 'FIRST' ? 'warning' : 'primary'">
                {{ flight.seatClass === 'FIRST' ? '头等舱' : flight.seatClass === 'BUSINESS' ? '商务舱' : '经济舱' }}
              </el-tag>
            </div>
            <div class="flight-price">
              <span class="price">{{ flight.price.toLocaleString() }}</span>
              <span class="currency">{{ flight.currency }}</span>
            </div>
          </div>

          <div class="flight-details">
            <div class="route">
              <div class="airport">
                <div class="code">{{ flight.departureAirportCode }}</div>
                <div class="name">{{ flight.departureAirportName }}</div>
                <div class="time">{{ formatTime(flight.departureTime) }}</div>
              </div>
              <div class="flight-path">
                <div class="duration">{{ formatDuration(flight.duration) }}</div>
                <div class="stops">
                  <el-tag size="small" v-if="flight.stops > 0">
                    {{ flight.stops }}次中转
                  </el-tag>
                  <el-tag size="small" type="success" v-else>
                    直飞
                  </el-tag>
                </div>
              </div>
              <div class="airport">
                <div class="code">{{ flight.arrivalAirportCode }}</div>
                <div class="name">{{ flight.arrivalAirportName }}</div>
                <div class="time">{{ formatTime(flight.arrivalTime) }}</div>
              </div>
            </div>

            <div class="flight-actions">
              <el-button type="primary" size="small" @click="viewFlightDetails(flight)">
                查看详情
              </el-button>
              <el-button type="success" size="small" @click="addToFavorites(flight)">
                收藏
              </el-button>
              <el-button size="small" @click="giveFeedback(flight)">
                反馈
              </el-button>
            </div>
          </div>
        </div>

        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="searchResults.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
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
  Search,
  Refresh,
  ChatLineRound
} from '@element-plus/icons-vue'
import type { FlightSearchParams, FlightSearchResponse } from '@/types/flight'

const router = useRouter()

const searchFormRef = ref()
const loading = ref(false)
const activeFilters = ref(['filters'])
const searchResults = ref<FlightSearchResponse | null>(null)
const sortBy = ref('PRICE_ASC')
const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  departure: '',
  arrival: '',
  departureDate: '',
  returnDate: '',
  minPrice: undefined as number | undefined,
  maxPrice: undefined as number | undefined,
  seatClass: '',
  airlines: [] as string[],
  stops: undefined as number | undefined
})

const handleSearch = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // const response = await api.get('/flights/search', { params: { ...searchForm, page: currentPage.value, size: pageSize.value, sortBy: sortBy.value } })
    // searchResults.value = response.data

    // 模拟数据
    searchResults.value = {
      flights: [
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
      ],
      total: 2,
      page: currentPage.value,
      size: pageSize.value,
      totalPages: 1
    }

    ElMessage.success(`找到${searchResults.value.total}个航班`)
  } catch (error) {
    ElMessage.error('搜索失败：' + (error as Error).message)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  if (searchFormRef.value) {
    searchFormRef.value.resetFields()
  }
  searchResults.value = null
}

const handleSortChange = (value: string) => {
  sortBy.value = value
  if (searchResults.value) {
    handleSearch()
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  if (searchResults.value) {
    handleSearch()
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  if (searchResults.value) {
    handleSearch()
  }
}

const viewFlightDetails = (flight: any) => {
  ElMessage.info(`查看航班 ${flight.flightNumber} 的详情`)
}

const addToFavorites = (flight: any) => {
  ElMessage.success(`已收藏航班 ${flight.flightNumber}`)
}

const giveFeedback = (flight: any) => {
  router.push(`/feedback?flightId=${flight.id}`)
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
</script>

<style scoped>
.search-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  text-align: center;
  margin-bottom: 30px;
}

.search-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.search-header p {
  color: #666;
}

.search-form-card {
  margin-bottom: 30px;
  border-radius: 12px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.advanced-filters {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.filter-separator {
  margin: 0 10px;
  color: #999;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.search-results {
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

.flight-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.flight-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.flight-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.flight-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.flight-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.flight-price {
  text-align: right;
}

.price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #f56c6c;
}

.currency {
  font-size: 1rem;
  color: #999;
  margin-left: 5px;
}

.flight-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.route {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.airport {
  text-align: center;
}

.airport .code {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.airport .name {
  font-size: 0.9rem;
  color: #666;
  margin: 5px 0;
}

.airport .time {
  font-size: 1.2rem;
  font-weight: 500;
  color: #333;
}

.flight-path {
  text-align: center;
}

.duration {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
}

.flight-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-results {
  padding: 50px 0;
}
</style>