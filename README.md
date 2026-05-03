# 航班推荐系统 AI 增强版

基于 Spring Boot 3.4 和 Vue 3 的现代化航班推荐系统，集成 DeepSeek AI接口提供智能航班推荐、用户反馈分析、满意度优化和个性化推荐等功能。系统包含完整的前后端分离架构，提供全面的航班搜索、比价、个性化推荐、价格趋势分析和智能AI助手服务。

## 项目概述

航班推荐系统 AI 增强版是一个现代化的智能航班推荐平台，旨在帮助用户快速找到最合适的航班。系统不仅支持传统的航班搜索、比价、个性化推荐和价格趋势分析，还深度集成 AI 技术，提供智能航班重新排序、用户反馈情感分析、推荐效果评估和个性化 AI 助手对话等功能。通过持续学习用户偏好和反馈，系统能够不断优化推荐策略，提升用户满意度和对不同画像用户推荐精准度。

## 技术栈

### 后端技术栈
- **Java 21** - 现代Java版本
- **Spring Boot 3.4.1** - 现代化Java框架
- **Spring Web、Security、AOP** - Web安全与切面编程
- **MyBatis + H2 / MySQL / PostgreSQL** - 数据访问层
- **PageHelper** - 分页插件
- **Spring Data Redis**（可选缓存）
- **SpringDoc OpenAPI**（Swagger） - API文档生成
- **Flyway** - 数据库迁移管理
- **Spring Mail** - 邮件通知服务
- **JWT** - 安全认证机制

### 前端技术栈
- **Vue 3** + **TypeScript** - 现代化前端框架
- **Vite** - 快速构建工具
- **Element Plus** - UI组件库
- **Pinia** - 状态管理
- **Vue Router 4** - 路由管理
- **Axios** - HTTP客户端
- **ESLint + Prettier** - 代码规范

### AI集成技术
- **DeepSeek AI API** - 智能推荐与反馈分析
- **OpenAI Java SDK** - AI服务客户端服务
- **Server-Sent Events (SSE)** - 实时流式对话
- **Prompt工程** - 智能提示构建

## 已实现功能

### 核心航班功能
- [x] **航班搜索** - 多条件搜索（出发地、目的地、日期、价格区间等）
- [x] **航班比价** - 多维度排序（价格、时长、性价比综合评分）
- [x] **个性化推荐** - 基于用户历史行为的热门航线、常搜航线推荐
- [x] **价格趋势分析** - 历史价格查询与趋势预测
- [x] **低价提醒** - 价格监控与邮件/通知提醒
- [x] **热门航线排行** - 实时热门航线统计
- [x] **用户偏好记录** - AOP切面自动记录用户搜索行为

### AI增强功能
- [x] **AI智能推荐** - 基于DeepSeek AI的航班重新排序与个性化推荐
- [x] **用户反馈系统** - 喜欢/不喜欢/中立反馈收集与管理
- [x] **AI反馈分析** - 自动分析用户反馈情感、关键因素和优化建议
- [x] **推荐效果评估** - AI驱动的SWOT分析、性能评估与优化建议
- [x] **AI满意度助手** - 智能对话助手，提供航班咨询和满意度优化
- [x] **实时流式对话** - Server-Sent Events实现的AI助手流式响应
- [x] **反馈驱动优化** - 基于用户反馈的推荐策略动态优化
- [x] **AI服务监控** - AI连接状态检查与健康监控

### 系统功能
- [x] **用户认证授权** - JWT令牌认证与权限管理
- [x] **API文档** - Swagger UI自动生成的交互式API文档
- [x] **健康检查** - Spring Boot Actuator健康端点
- [x] **数据库迁移** - Flyway管理的版本化数据库迁移
- [x] **缓存支持** - Redis缓存集成（可选）
- [x] **邮件通知** - 低价提醒邮件发送

## API 接口总览

### 用户认证接口
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/login` | POST | 用户登录 |

### 航班核心接口
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/flights` | GET | 航班列表 |
| `/api/flights/{id}` | GET | 航班详情 |
| `/api/flights/search` | GET | 高级搜索（分页、排序、过滤） |
| `/api/recommendations` | GET | 个性化推荐 |
| `/api/routes/popular` | GET | 热门航线排行 |
| `/api/price/trend` | GET | 价格趋势分析 |
| `/api/price/alert` | POST/GET | 低价提醒（创建/列表） |

### AI增强接口
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/feedback` | POST/GET | 提交/获取用户反馈 |
| `/api/ai/feedback/stats` | GET | 获取反馈统计数据 |
| `/api/ai/feedback/rerank` | POST | 不满意反馈并立即重新优化推荐 |
| `/api/ai/analyze` | POST | AI分析推荐系统效果（SWOT分析） |
| `/api/ai/status` | GET | 获取AI服务连接状态 |
| `/api/ai/optimize` | POST | 基于用户反馈优化推荐策略 |
| `/api/ai/recommend` | POST | AI个性化推荐（高级推荐接口） |
| `/api/ai/assistant` | POST | AI助手对话接口（满意度助手） |
| `/api/ai/assistant/stream` | GET | AI助手流式对话（Server-Sent Events） |

### 系统接口
| 接口 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 系统主页（API概览） |
| `/api/health` | GET | 应用健康检查 |
| `/swagger-ui.html` | GET | Swagger UI API文档 |
| `/v3/api-docs` | GET | OpenAPI规范文档 |
| `/h2-console` | GET | H2数据库控制台（开发环境） |
| `/actuator/health` | GET | Spring Boot Actuator健康端点 |

## 快速开始

### 启动应用

```bash
mvn spring-boot:run
```

或指定环境：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 验证运行

访问 http://localhost:8080/api/health 应返回：

```json
{"status":"UP","application":"springboot-app"}
```

### API 文档

启动后访问 http://localhost:8080/swagger-ui.html 可在线调试所有接口。

### 测试账号

- 用户名：`testuser`
- 密码：`123456`

### AI配置

系统深度集成DeepSeek AI提供智能推荐和反馈分析。默认配置包含演示API密钥，如需使用自己的DeepSeek API密钥：

1. **环境变量配置**（推荐）：
   ```bash
   export DEEPSEEK_API_KEY=your-actual-deepseek-api-key
   mvn spring-boot:run
   ```

2. **配置文件配置**：
   在`src/main/resources/application.yml`中修改：
   ```yaml
   ai:
     openai:
       api-key: ${DEEPSEEK_API_KEY:sk-demo-key}  # 替换为你的API密钥
       base-url: https://api.deepseek.com/v1/
       model: deepseek-chat
       temperature: 0.3
       max-tokens: 1000
       timeout: 30s
   ```

3. **验证AI服务**：
   ```bash
   curl http://localhost:8080/api/ai/status
   ```
   预期响应：
   ```json
   {
     "code": 200,
     "msg": "success",
     "data": {
       "enabled": true,
       "connected": true,
       "status": "可用",
       "timestamp": 1710000000000
     }
   }
   ```

4. **测试AI助手**：
   ```bash
   curl -X POST http://localhost:8080/api/ai/assistant \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer {your-jwt-token}" \
     -d '{"message": "帮我推荐从北京到上海的航班"}'
   ```

### 示例：航班搜索

```
GET /api/flights/search?departure=PEK&arrival=PVG&departureDate=2025-03-18&page=0&size=10&sortBy=PRICE_ASC
```

### 打包

```bash
mvn clean package
java -jar target/springboot-app-1.0.0-SNAPSHOT.jar
```

### 测试

系统包含完整的单元测试和集成测试，确保代码质量和功能稳定性。

#### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=FlightControllerTest

# 运行集成测试
mvn test -Dtest=AiIntegrationTest

# 生成测试覆盖率报告
mvn jacoco:report
```

#### 测试结构
```
src/test/java/com/example/app/
├── SpringbootAppApplicationTests.java    # 应用上下文测试
├── controller/
│   ├── FlightControllerTest.java         # 航班控制器测试
│   ├── RouteControllerTest.java          # 航线控制器测试
│   ├── HealthControllerTest.java         # 健康检查测试
│   └── AiIntegrationTest.java            # ⭐ AI集成测试
└── service/
    └── (各服务层测试)
```

#### 测试覆盖
- **单元测试**：控制器、服务层、工具类的独立测试
- **集成测试**：AI服务集成、数据库操作、API端点测试
- **Mock测试**：使用Mockito模拟外部依赖（如AI API调用）
- **数据库测试**：使用H2内存数据库进行数据层测试
- **安全测试**：JWT认证和授权测试

## 项目结构

### 后端项目结构
```
src/main/java/com/example/app/
├── SpringbootAppApplication.java          # 应用启动类
├── aspect/
│   └── UserBehaviorAspect.java            # AOP用户行为记录
├── common/
│   ├── GlobalExceptionHandler.java        # 全局异常处理
│   └── Result.java                        # 统一响应封装
├── config/
│   ├── AiConfig.java                      # ⭐ AI服务配置（DeepSeek API）
│   ├── CacheConfig.java                   # 缓存配置
│   ├── OpenApiConfig.java                 # Swagger/OpenAPI配置
│   ├── RedisCacheConfig.java              # Redis缓存配置
│   ├── RedisConfig.java                   # Redis连接配置
│   ├── SecurityConfig.java                # 安全配置（JWT认证）
│   └── WebConfig.java                     # Web MVC配置
├── controller/
│   ├── AiController.java                  # ⭐ AI接口（反馈/分析/助手）
│   ├── AuthController.java                # 认证接口
│   ├── FlightController.java              # 航班接口
│   ├── HealthController.java              # 健康检查
│   ├── HomeController.java                # 主页接口
│   ├── PriceController.java               # 价格接口
│   ├── RecommendationController.java      # 推荐接口
│   └── RouteController.java               # 航线接口
├── dto/
│   ├── ai/                                # ⭐ AI相关数据传输对象
│   │   ├── AiFeedbackRequest.java         # 反馈请求
│   │   ├── AiFeedbackResponse.java        # 反馈响应
│   │   ├── AiFeedbackRerankRequest.java   # 重新优化请求
│   │   ├── AiFeedbackRerankResponse.java  # 重新优化响应
│   │   ├── AiAnalysisResult.java          # AI分析结果
│   │   └── AiRecommendRequest.java        # AI推荐请求
│   ├── FlightSearchParams.java            # 航班搜索参数
│   ├── FlightSearchRequest.java           # 航班搜索请求
│   ├── FlightSearchResponse.java          # 航班搜索响应
│   ├── FlightVO.java                      # 航班视图对象
│   ├── LoginRequest.java                  # 登录请求
│   ├── PageResult.java                    # 分页结果
│   ├── PriceAlertRequest.java             # 价格提醒请求
│   └── RegisterRequest.java               # 注册请求
├── entity/
│   ├── Airline.java                       # 航空公司实体
│   ├── Airport.java                       # 机场实体
│   ├── Booking.java                       # 预订实体
│   ├── Flight.java                        # 航班实体
│   ├── FlightPrice.java                   # 航班价格实体
│   ├── PriceAlert.java                    # 价格提醒实体
│   ├── Route.java                         # 航线实体
│   ├── User.java                          # 用户实体
│   ├── UserFlightHistory.java             # 用户搜索历史实体
│   └── UserFeedback.java                  # ⭐ 用户反馈实体
├── mapper/                                # MyBatis映射器
│   ├── AirlineMapper.java
│   ├── AirportMapper.java
│   ├── BookingMapper.java
│   ├── FlightMapper.java
│   ├── FlightPriceMapper.java
│   ├── PriceAlertMapper.java
│   ├── RouteMapper.java
│   ├── UserFlightHistoryMapper.java
│   ├── UserMapper.java
│   └── UserFeedbackMapper.java           # ⭐ 用户反馈映射器
├── scheduler/                             # 定时任务
│   ├── PriceScheduler.java                # 价格监控定时任务
│   └── AiOptimizationScheduler.java       # ⭐ AI优化定时任务
├── security/                              # 安全模块
│   ├── AuthUser.java                      # 认证用户封装
│   ├── JwtUtil.java                       # JWT工具类
│   └── JwtAuthFilter.java                 # JWT认证过滤器
└── service/                               # 业务服务层
    ├── ai/                                # ⭐ AI核心服务模块
    │   ├── AiService.java                 # AI服务接口
    │   ├── AiServiceImpl.java             # AI服务实现
    │   ├── AiClient.java                  # AI API客户端
    │   ├── PromptBuilder.java             # Prompt工程构建器
    │   └── AiResponseParser.java          # AI响应解析器
    ├── AirportService.java                # 机场服务
    ├── AuthService.java                   # 认证服务
    ├── FlightService.java                 # 航班服务
    ├── NotificationService.java           # 通知服务
    ├── PriceAlertService.java             # 价格提醒服务
    ├── PriceTrendService.java             # 价格趋势服务
    ├── RecommendationService.java         # ⭐ AI增强的推荐服务
    ├── RouteService.java                  # 航线服务
    ├── UserBehaviorService.java           # 用户行为服务
    ├── UserDetailsServiceImpl.java        # 用户详情服务
    ├── UserService.java                   # 用户服务
    └── UserFeedbackService.java           # ⭐ 用户反馈服务

src/main/resources/
├── application.yml                        # 主配置文件
├── application-dev.yml                    # 开发环境配置
├── application-prod.yml                   # 生产环境配置
├── application-redis.yml                  # Redis配置
├── db/migration/                          # Flyway数据库迁移脚本
│   ├── V1__init_schema.sql                # 初始化表结构
│   ├── V2__seed_data.sql                  # 基础数据
│   ├── V3__price_alert.sql                # 价格提醒表
│   ├── V4__seed_user_history_and_price.sql # 用户历史数据
│   ├── V5__add_user_feedback.sql          # ⭐ 用户反馈表
│   └── V6__add_more_flight_data.sql      # ⭐ 扩展航班数据
└── mapper/                                # MyBatis XML映射文件
    ├── UserMapper.xml
    ├── FlightMapper.xml
    └── UserFeedbackMapper.xml            # ⭐ 用户反馈映射
```

## 配置说明

### 环境配置
系统支持多环境配置，通过Spring Profiles管理：

1. **开发环境 (default)**：使用H2内存数据库，无需外部依赖
   - 默认排除Redis，无需本地安装
   - 启用H2控制台：http://localhost:8080/h2-console
   - 启用Swagger UI：http://localhost:8080/swagger-ui.html

2. **Redis缓存环境**：激活 `redis` profile 启用Redis缓存
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=redis
   ```

3. **生产环境**：激活 `prod` profile 使用生产配置
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

### 数据库配置
- **默认数据库**：H2内存数据库（开发环境）
- **生产数据库**：支持MySQL/PostgreSQL，在 `application-prod.yml` 中配置
- **数据库迁移**：Flyway管理，自动执行 `db/migration/` 下的SQL脚本
- **数据初始化**：系统启动时自动注入基础航班数据、机场数据和测试用户

### AI服务配置
- **默认配置**：使用DeepSeek AI演示API密钥（仅供测试）
- **自定义配置**：设置环境变量 `DEEPSEEK_API_KEY` 使用自己的API密钥
- **服务验证**：通过 `/api/ai/status` 端点验证AI服务连接状态
- **降级处理**：AI服务不可用时，系统自动降级到传统推荐算法

### 邮件通知配置
- **必需配置**：需在 `application.yml` 中配置 `spring.mail.*` 属性
- **可选功能**：如未配置邮件服务，低价提醒仅记录到数据库，不实际发送邮件
- **模板支持**：使用Thymeleaf模板引擎生成HTML邮件内容

### JWT安全配置
- **密钥配置**：在 `application.yml` 中配置 `jwt.secret` 属性
- **有效期**：默认24小时，可通过 `jwt.expiration` 调整
- **安全建议**：生产环境务必更换默认JWT密钥

## 前端项目 (Vue 3 + TypeScript)

前端项目位于 `frontend/` 目录，采用现代化的Vue 3 + TypeScript技术栈构建，提供响应式用户界面和流畅的用户体验。前端与后端完全分离，通过RESTful API进行通信，支持AI助手流式对话、实时反馈提交和个性化推荐展示。

### 前端技术栈
- **核心框架**: Vue 3 + TypeScript - 现代化的前端框架和类型安全
- **构建工具**: Vite - 快速的开发构建工具
- **UI组件库**: Element Plus - 丰富的UI组件库
- **状态管理**: Pinia - 轻量级状态管理库
- **路由管理**: Vue Router 4 - 前端路由系统
- **HTTP客户端**: Axios - 强大的HTTP请求库
- **代码规范**: ESLint + Prettier - 代码质量和格式化
- **图标库**: @element-plus/icons-vue - Element Plus图标组件

### 核心功能模块

#### 1. 用户认证模块
- 用户登录/注册界面
- JWT令牌自动管理
- 路由守卫和权限控制
- 用户状态持久化

#### 2. 航班搜索模块
- 多条件航班搜索（出发地、目的地、日期、价格区间）
- 实时筛选和排序功能
- 分页加载和无限滚动
- 航班详情查看

#### 3. AI推荐模块
- AI增强的个性化航班推荐
- 用户反馈收集（喜欢/不喜欢/中立）
- 推荐结果重新优化
- 推荐效果可视化展示

#### 4. 价格趋势模块
- 历史价格趋势图表
- 低价提醒设置与管理
- 价格预测分析
- 邮件通知配置

#### 5. AI助手模块 ⭐
- 智能对话助手界面
- 实时流式对话响应（Server-Sent Events）
- 满意度优化建议
- 航班咨询和推荐

#### 6. 反馈管理模块
- 用户反馈提交和管理
- 反馈统计和可视化
- AI反馈分析结果展示
- 反馈历史记录

### 前端项目结构
```
frontend/
├── public/                            # 静态资源
├── src/
│   ├── assets/                       # 静态资源（样式、图片）
│   ├── components/                   # 可复用组件
│   │   └── AiAssistantDialog.vue     # ⭐ AI助手对话框组件
│   ├── composables/                  # Vue组合式函数
│   │   └── useAiAssistant.ts         # ⭐ AI助手组合式函数
│   ├── layouts/                      # 布局组件
│   ├── pages/                        # 页面组件
│   ├── router/                       # 路由配置
│   │   └── index.ts                  # 路由定义
│   ├── services/                     # API服务层
│   │   ├── auth.ts                   # 认证服务
│   │   ├── flight.ts                 # 航班服务
│   │   └── ai.ts                     # ⭐ AI服务
│   ├── stores/                       # Pinia状态管理
│   │   ├── auth.ts                   # 认证状态
│   │   └── aiAssistant.ts            # ⭐ AI助手状态
│   ├── types/                        # TypeScript类型定义
│   │   ├── auth.ts                   # 认证类型
│   │   ├── flight.ts                 # 航班类型
│   │   └── ai.ts                     # ⭐ AI相关类型
│   ├── utils/                        # 工具函数
│   │   └── api.ts                    # API请求封装
│   ├── views/                        # 页面视图组件
│   │   ├── HomeView.vue              # 首页
│   │   ├── LoginView.vue             # 登录页
│   │   ├── RegisterView.vue          # 注册页
│   │   ├── SearchView.vue            # 航班搜索页
│   │   ├── RecommendationsView.vue   # 推荐页
│   │   ├── PriceTrendView.vue        # 价格趋势页
│   │   ├── FeedbackView.vue          # ⭐ 反馈页
│   │   └── AiAssistantView.vue       # ⭐ AI助手页
│   ├── App.vue                       # 根组件
│   └── main.ts                       # 应用入口
├── index.html                        # HTML模板
├── package.json                      # 依赖配置
├── tsconfig.json                     # TypeScript配置
├── vite.config.ts                    # Vite配置
└── README.md                         # 前端说明文档
```

### 前端启动步骤

1. **安装依赖**：
   ```bash
   cd frontend
   npm install
   ```

2. **启动开发服务器**：
   ```bash
   npm run dev
   ```

3. **访问应用**：
   - 开发服务器：http://localhost:3000
   - 后端API：http://localhost:8080

4. **构建生产版本**：
   ```bash
   npm run build
   ```

### 代理配置
前端开发服务器配置了API代理，将所有 `/api` 请求转发到后端服务器：
```javascript
// vite.config.ts 代理配置
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### 开发说明
- **热重载**：支持代码修改实时刷新
- **TypeScript支持**：完整的类型检查和智能提示
- **响应式设计**：适配桌面和移动设备
- **组件化开发**：模块化、可复用的组件架构
- **状态管理**：集中式的状态管理和数据流控制

## 完整系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户界面层 (UI Layer)                           │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                        Vue 3前端应用 (frontend/)                     │  │
│  │  • HomeView.vue       • SearchView.vue      • RecommendationsView.vue│  │
│  │  • AiAssistantView.vue • FeedbackView.vue   • PriceTrendView.vue     │  │
│  │  • LoginView.vue      • RegisterView.vue                             │  │
│  └───────────────────────────────┬──────────────────────────────────────┘  │
│                                  │                                         │
│                         HTTP/REST API (代理: /api → :8080)                 │
│                                  │                                         │
└──────────────────────────────────┼─────────────────────────────────────────┘
                                   │
┌──────────────────────────────────▼─────────────────────────────────────────┐
│                          Spring Boot后端服务 (src/)                         │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                   控制器层 (Controller)                               │  │
│  │  • AuthController      • FlightController     • AiController         │  │
│  │  • RecommendationController • PriceController • RouteController      │  │
│  └──────────────┬────────────────────────┬─────────────────────────────┘  │
│                 │                        │                                │
│  ┌──────────────▼────────┐   ┌──────────▼──────────┐                     │
│  │     服务层 (Service)    │   │    AI服务层         │                     │
│  │  • FlightService      │   │  • AiService       │                     │
│  │  • RecommendationService│   │  • AiClient       │                     │
│  │  • PriceAlertService  │   │  • PromptBuilder   │                     │
│  │  • UserFeedbackService│   │  • AiResponseParser│                     │
│  └──────────────┬────────┘   └────────────────────┘                     │
│                 │                                                     │
│  ┌──────────────▼──────────────────────────────────────────────────┐  │
│  │               数据访问层 (Mapper)                                │  │
│  │  • FlightMapper     • UserMapper     • UserFeedbackMapper      │  │
│  │  • RouteMapper      • PriceAlertMapper                         │  │
│  └──────────────┬──────────────────────────────────────────────────┘  │
│                 │                                                     │
│  ┌──────────────▼────────┐   ┌──────────────────────┐               │
│  │       数据库层          │   │       缓存层          │               │
│  │  • H2/MySQL/PostgreSQL│   │  • Redis (可选)      │               │
│  └───────────────────────┘   └──────────────────────┘               │
└─────────────────────────────────────────────────────────────────────┘

                                AI服务集成
┌─────────────────────────────────────────────────────────────────────┐
│                      DeepSeek AI / OpenAI API                       │
│  • 智能航班重新排序 (Reranking)                                       │
│  • 用户反馈情感分析                                                   │
│  • 推荐效果SWOT分析                                                  │
│  • 个性化推荐生成                                                    │
│  • 满意度助手对话                                                    │
└─────────────────────────────────────────────────────────────────────┘
```

## 数据流说明
1. **用户请求流程**: 前端UI → Vue Router → 页面组件 → API服务 → 后端REST API
2. **后端处理流程**: Controller → Service → AI服务 → Mapper → 数据库
3. **AI集成流程**: Service层调用AiClient → 发送Prompt到AI API → 解析响应 → 返回结果
4. **反馈优化闭环**: 用户提交反馈 → AI分析 → 策略优化 → 改进推荐 → 提升满意度

## 部署架构
```
                   ┌─────────────┐
                   │   用户浏览器   │
                   └──────┬──────┘
                          │ (HTTPS)
                   ┌──────▼──────┐
                   │  Nginx反向代理 │
                   └──────┬──────┘
            ┌─────────────┼─────────────┐
    ┌───────▼──────┐      │      ┌──────▼──────┐
    │   Vue前端     │      │      │ Spring Boot  │
    │   (静态文件)   │      │      │   (后端API)  │
    └──────────────┘      │      └──────┬──────┘
                          │             │
                    ┌─────▼─────────────▼─────┐
                    │       数据库层           │
                    │  • MySQL/PostgreSQL     │
                    │  • Redis (缓存)         │
                    └─────────────────────────┘
```

## 后续扩展建议

### 1. 前端功能增强
- **移动端优化**：完善响应式设计，提供更好的移动端体验
- **PWA支持**：实现渐进式Web应用，支持离线访问和桌面安装
- **实时通知**：集成WebSocket实现实时价格变动通知和航班状态更新
- **多语言支持**：添加国际化(i18n)支持，提供多语言界面
- **主题定制**：支持深色/浅色主题切换，提供个性化外观
- **性能优化**：实现虚拟滚动、图片懒加载、代码分割等性能优化

### 2. 后端架构扩展
- **微服务拆分**：将单体应用拆分为用户服务、航班服务、推荐服务、AI服务等微服务
- **消息队列集成**：引入RabbitMQ或Kafka处理异步任务和事件驱动架构
- **分布式缓存**：扩展Redis缓存集群，支持分布式会话和缓存
- **API网关**：引入Spring Cloud Gateway作为API网关，统一入口和路由
- **服务注册发现**：集成Consul或Nacos实现服务注册与发现

### 3. AI能力增强
- **多AI提供商支持**：扩展支持OpenAI、通义千问、文心一言、智谱AI等多平台
- **本地模型部署**：集成本地LLM模型（如Qwen、ChatGLM）降低API成本
- **高级预测分析**：基于历史数据的航班价格预测和需求预测
- **个性化模型训练**：基于用户行为数据训练个性化推荐模型
- **多模态AI**：支持图像识别（航班信息截图解析）、语音交互等

### 4. 数据与算法优化
- **实时数据处理**：引入Flink或Spark Streaming处理实时航班数据流
- **机器学习集成**：集成协同过滤、矩阵分解等传统推荐算法作为AI补充
- **图神经网络**：使用图神经网络分析用户-航班-航线复杂关系
- **A/B测试框架**：建立A/B测试平台，评估不同推荐策略效果

### 5. 运维与监控
- **容器化部署**：Docker容器化，支持一键部署和水平扩展
- **Kubernetes编排**：使用K8s管理微服务集群，实现自动扩缩容
- **可观测性**：集成Prometheus监控、Grafana仪表板、ELK日志分析
- **CI/CD流水线**：建立完整的持续集成和持续部署流水线
- **安全加固**：实现API限流、熔断降级、安全审计等生产级安全特性

### 6. 业务功能扩展
- **第三方数据集成**：对接真实航司API、OTA平台数据，获取实时航班信息
- **社交功能**：添加航班点评、旅行分享、好友推荐等社交元素
- **行程规划**：扩展为完整的旅行规划平台，包含酒店、租车、景点推荐
- **企业版功能**：开发企业差旅管理版本，支持审批流、预算控制、报表分析
- **移动端应用**：开发原生iOS/Android应用，提供更好的移动体验
