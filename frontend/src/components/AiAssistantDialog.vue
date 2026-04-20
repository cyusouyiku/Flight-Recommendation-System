<template>
  <div
    v-if="isOpen"
    class="ai-assistant-dialog"
    :class="{
      'minimized': isMinimized,
      'mobile': isMobile,
      'position-right': dialogPosition === 'right',
      'position-bottom': dialogPosition === 'bottom'
    }"
    :style="{
      width: isMinimized ? '60px' : dialogWidth,
      height: isMinimized ? '60px' : (dialogPosition === 'bottom' ? '400px' : 'calc(100vh - 40px)'),
      right: dialogPosition === 'right' ? '20px' : 'auto',
      bottom: dialogPosition === 'bottom' ? '20px' : 'auto',
      top: dialogPosition === 'right' ? '20px' : 'auto',
      left: dialogPosition === 'bottom' ? '20px' : 'auto'
    }"
  >
    <!-- 最小化状态 -->
    <div v-if="isMinimized" class="minimized-view" @click="toggleMinimized">
      <el-avatar :size="40" :src="assistantAvatar" />
      <el-badge :value="unreadCount" :max="99" v-if="unreadCount > 0">
        <el-icon><ChatDotRound /></el-icon>
      </el-badge>
    </div>

    <!-- 完整对话框 -->
    <div v-else class="dialog-container">
      <!-- 对话框头部 -->
      <div class="dialog-header">
        <div class="header-left">
          <el-avatar :size="32" :src="assistantAvatar" />
          <div class="header-info">
            <h3>旅行小助手</h3>
            <el-tag size="small" type="success">
              <el-icon><Connection /></el-icon>
              在线
            </el-tag>
          </div>
        </div>
        <div class="header-actions">
          <el-tooltip content="最小化" placement="bottom">
            <el-button text :icon="Minus" @click="toggleMinimized" />
          </el-tooltip>
          <el-tooltip content="关闭" placement="bottom">
            <el-button text :icon="Close" @click="close" />
          </el-tooltip>
        </div>
      </div>

      <!-- 对话框内容 -->
      <div class="dialog-content">
        <!-- 聊天消息区域 -->
        <div class="chat-messages" ref="messagesContainer">
          <div
            v-for="message in messages"
            :key="message.id"
            :class="['message', message.role]"
          >
            <div class="message-avatar">
              <el-avatar
                v-if="message.role === 'assistant'"
                :size="28"
                :src="assistantAvatar"
              />
              <el-avatar
                v-else
                :size="28"
                :src="userAvatar"
              >
                {{ userInitials }}
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">
                {{ formatTime(message.timestamp) }}
              </div>
            </div>
          </div>

          <div v-if="loading" class="message assistant typing">
            <div class="message-avatar">
              <el-avatar :size="28" :src="assistantAvatar" />
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快速建议 -->
        <div class="quick-suggestions" v-if="suggestions.length > 0 && !isMobile">
          <div class="suggestions-header">
            <el-icon><Lightning /></el-icon>
            <span>快速提问</span>
          </div>
          <div class="suggestion-buttons">
            <el-button
              v-for="suggestion in suggestions"
              :key="suggestion"
              size="small"
              @click="sendQuickQuestion(suggestion)"
            >
              {{ suggestion }}
            </el-button>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="isMobile ? 1 : 2"
            placeholder="输入您的问题... (Enter发送，Shift+Enter换行)"
            :disabled="loading"
            @keydown.enter="handleEnterKey"
          />
          <div class="input-actions">
            <div class="input-tools" v-if="!isMobile">
              <el-tooltip content="上传文件" placement="top">
                <el-button text :icon="Upload" />
              </el-tooltip>
              <el-tooltip content="语音输入" placement="top">
                <el-button text :icon="Microphone" />
              </el-tooltip>
            </div>
            <div class="send-button">
              <el-button
                type="primary"
                :loading="loading"
                :disabled="!userInput.trim()"
                @click="sendMessage"
                size="small"
              >
                <template #icon>
                  <el-icon><Promotion /></el-icon>
                </template>
                {{ isMobile ? '发送' : '' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 全局浮动按钮（当对话框关闭时） -->
  <div v-else class="global-fab" :class="{ 'mobile': isMobile }" @click="open">
    <el-avatar :size="isMobile ? 48 : 36" :src="assistantAvatar" />
    <el-badge :value="unreadCount" :max="99" v-if="unreadCount > 0" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import {
  Connection,
  Lightning,
  Upload,
  Microphone,
  Promotion,
  Minus,
  Close,
  ChatDotRound
} from '@element-plus/icons-vue'
import type { AiAssistantMessage } from '@/types/ai'
import { aiService } from '@/services/ai'
import { useAuthStore } from '@/stores/auth'
import { useAiAssistantStore } from '@/stores/aiAssistant'

// Store
const authStore = useAuthStore()
const aiAssistantStore = useAiAssistantStore()

// 响应式状态
const messagesContainer = ref<HTMLElement>()
const userInput = ref('')

// 使用storeToRefs获取响应式状态
const {
  isOpen,
  isMinimized,
  messages,
  loading,
  suggestions,
  helpCount,
  overallSatisfaction,
  recommendationAccuracy,
  responseSpeed,
  isMobile,
  dialogWidth,
  dialogPosition
} = storeToRefs(aiAssistantStore)

// 直接使用store的方法
const {
  toggleMinimized,
  close: closeDialog,
  open: openDialog,
  addMessage,
  updateSuggestions,
  updateSatisfactionStats,
  incrementHelpCount
} = aiAssistantStore

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.userInfo?.username || '用户')
const userInitials = computed(() => {
  if (!authStore.userInfo?.username) return '用户'
  const name = authStore.userInfo.username
  if (name.length >= 2) {
    return name.substring(0, 2).toUpperCase()
  }
  return name.charAt(0).toUpperCase()
})
const assistantAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const userAvatar = ref('')
const unreadCount = computed(() => {
  // 简单实现：返回最近未读消息数（这里暂时返回0）
  return 0
})

// 方法
const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return


  const userMessage: AiAssistantMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: userInput.value,
    timestamp: new Date().toISOString()
  }

  addMessage(userMessage)
  const userMessageText = userInput.value
  userInput.value = ''
  loading.value = true

  await nextTick()
  scrollToBottom()

  try {
    // 调用实际的AI助手API
    const context = messages.value.map(msg => ({
      id: msg.id,
      role: msg.role,
      content: msg.content,
      timestamp: msg.timestamp
    }))

    const response = await aiService.chatWithAssistant(userMessageText, context)

    // 检查响应是否有效
    if (!response || !response.message) {
      throw new Error('AI助手响应异常')
    }

    const assistantMessage: AiAssistantMessage = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: response.message,
      timestamp: new Date().toISOString()
    }

    addMessage(assistantMessage)
    incrementHelpCount()

    // 如果API返回了建议，更新快速提问建议
    if (response.suggestions && Array.isArray(response.suggestions)) {
      updateSuggestions(response.suggestions.slice(0, 5))
    }

    // 更新满意度统计数据
    if (response.analysis && response.satisfaction !== undefined) {
      updateSatisfactionStats({
        satisfaction: response.satisfaction,
        accuracy: response.accuracy,
        speed: response.speed
      })
    }

    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('AI助手对话失败:', error)

    // 提供更详细的错误提示
    const errorMessage = getErrorMessage(error)
    ElMessage.error(`发送消息失败：${errorMessage}`)

    // 如果API调用失败，提供友好的错误提示
    const assistantErrorMessage: AiAssistantMessage = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: getAssistantErrorMessage(error),
      timestamp: new Date().toISOString()
    }

    addMessage(assistantErrorMessage)
    await nextTick()
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

const sendQuickQuestion = (question: string) => {
  userInput.value = question
  sendMessage()
}

const handleEnterKey = (event: Event) => {
  const keyboardEvent = event as KeyboardEvent
  if (keyboardEvent.shiftKey) {
    // Shift+Enter: 插入换行
    return
  }

  // Enter: 发送消息
  event.preventDefault()
  sendMessage()
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatMessage = (content: string) => {
  // 简单的Markdown转换
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
    .replace(/•/g, '•')
}

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getErrorMessage = (error: any): string => {
  if (error?.response?.status === 401) {
    return '认证失败，请重新登录'
  } else if (error?.response?.status === 403) {
    return '没有权限访问此功能'
  } else if (error?.response?.status === 404) {
    return '服务暂时不可用'
  } else if (error?.response?.status === 500) {
    return '服务器内部错误'
  } else if (error?.request) {
    return '网络连接失败，请检查网络设置'
  } else if (error?.message) {
    return error.message
  }
  return '未知错误，请稍后重试'
}

const getAssistantErrorMessage = (error: any): string => {
  const errorMsg = getErrorMessage(error)
  return `抱歉，AI助手服务遇到问题：${errorMsg}\n\n建议：\n1. 检查网络连接\n2. 刷新页面重试\n3. 稍后再使用AI助手`
}

const open = () => {
  openDialog()
}

const close = () => {
  closeDialog()
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-assistant-dialog {
  position: fixed;
  z-index: 9999;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-assistant-dialog.position-right {
  top: 20px;
  right: 20px;
}

.ai-assistant-dialog.position-bottom {
  bottom: 20px;
  left: 20px;
  right: 20px;
}

.ai-assistant-dialog.minimized {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.minimized-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  height: 100%;
}

.minimized-view .el-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

.dialog-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: white;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-info h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.header-actions .el-button {
  color: white;
}

.header-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dialog-content {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f8f9fa;
}

.message {
  display: flex;
  margin-bottom: 12px;
  animation: fadeIn 0.3s ease;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 8px;
}

.message-content {
  max-width: 80%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 8px 12px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  line-height: 1.4;
  font-size: 0.9rem;
}

.message.assistant .message-text {
  background: white;
  border: 1px solid #e4e7ed;
}

.message.user .message-text {
  background: #667eea;
  color: white;
}

.message-time {
  font-size: 0.75rem;
  color: #999;
  margin-top: 4px;
}

.message.user .message-time {
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 12px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

.quick-suggestions {
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
  background: white;
  flex-shrink: 0;
}

.suggestions-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #666;
  font-size: 0.85rem;
}

.suggestion-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-buttons .el-button {
  font-size: 0.8rem;
  padding: 4px 8px;
}

.chat-input-area {
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
  background: white;
  flex-shrink: 0;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.input-tools {
  display: flex;
  gap: 8px;
}

.send-button {
  min-width: 80px;
}

.send-button .el-button {
  width: 100%;
}

/* 移动端样式 */
.ai-assistant-dialog.mobile {
  border-radius: 0;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important;
}

.global-fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  z-index: 9998;
  transition: all 0.3s ease;
}

.global-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
}

.global-fab.mobile {
  width: 60px;
  height: 60px;
}

.global-fab .el-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .ai-assistant-dialog:not(.mobile) {
    max-height: 80vh;
  }

  .chat-messages {
    padding: 12px;
  }

  .message-text {
    font-size: 0.85rem;
    padding: 6px 10px;
  }

  .quick-suggestions {
    display: none;
  }

  .input-tools {
    display: none;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  30% {
    transform: translateY(-3px);
    opacity: 1;
  }
}
</style>