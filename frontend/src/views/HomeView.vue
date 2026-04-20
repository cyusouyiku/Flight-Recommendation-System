<template>
  <div class="home">
    <header class="header">
      <div class="header-content">
        <div class="header-left">
          <h1>✈️ 智能机票推荐系统</h1>
          <p class="subtitle">基于AI的个性化航班搜索与推荐平台</p>
        </div>
        <div class="header-right">
          <el-button type="text" @click="router.push('/login')">登录</el-button>
          <el-button type="text" @click="router.push('/register')">注册</el-button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="features">
        <div class="feature-card" @click="router.push('/search')">
          <el-icon size="48"><Search /></el-icon>
          <h3>航班搜索</h3>
          <p>按出发地、目的地、日期等条件搜索航班</p>
        </div>

        <div class="feature-card" @click="router.push('/recommendations')">
          <el-icon size="48"><Star /></el-icon>
          <h3>个性化推荐</h3>
          <p>基于您的偏好和AI分析的智能推荐</p>
        </div>

        <div class="feature-card" @click="router.push('/price-trend')">
          <el-icon size="48"><TrendCharts /></el-icon>
          <h3>价格趋势</h3>
          <p>查看历史价格趋势，设置低价提醒</p>
        </div>

      </div>

      <div class="quick-actions">
        <el-button type="primary" size="large" @click="router.push('/search')">
          开始搜索航班
        </el-button>
      </div>

      <div class="recent-updates">
        <h2>📊 系统状态</h2>
        <el-card class="status-card">
          <div class="status-item">
            <span>AI服务状态</span>
            <el-tag type="success">正常运行</el-tag>
          </div>
          <div class="status-item">
            <span>今日推荐航班</span>
            <el-tag>128条</el-tag>
          </div>
          <div class="status-item">
            <span>用户满意度</span>
            <el-tag type="warning">92%</el-tag>
          </div>
        </el-card>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { Search, Star, TrendCharts } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 40px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  gap: 10px;
}

.header-right .el-button {
  color: white;
  font-weight: 500;
}

.header-right .el-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.feature-card {
  background: white;
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.feature-card h3 {
  margin: 15px 0 10px;
  color: #333;
}

.feature-card p {
  color: #666;
  font-size: 0.9rem;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.recent-updates {
  margin-top: 40px;
}

.status-card {
  margin-top: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.status-item:last-child {
  border-bottom: none;
}

.status-item span {
  font-weight: 500;
}
</style>