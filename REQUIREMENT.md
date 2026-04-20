# 机票推荐系统 - 需求规格说明书

## 1. 引言

### 1.1 项目概述
机票推荐系统是一个基于Spring Boot 3.4的后端服务，旨在为用户提供智能化的航班搜索、比价、个性化推荐和价格监控功能。系统集成了AI技术，通过分析用户行为和反馈，提供更加精准的航班推荐。

### 1.2 项目目标
- 为用户提供快速、准确的航班搜索和比价服务
- 基于用户历史行为和偏好提供个性化推荐
- 实时监控航班价格变化，提供低价提醒
- 通过AI技术持续优化推荐策略，提升用户体验
- 提供完整的用户反馈机制，实现推荐系统的闭环优化

### 1.3 范围
本系统主要包含以下核心功能：
- 用户认证与授权
- 航班搜索与过滤
- 航班比价与排序
- 个性化推荐（热门、常搜、AI推荐）
- 价格趋势分析与低价提醒
- 用户反馈收集与AI分析
- AI服务状态监控与优化

## 2. 功能需求

### 2.1 用户管理
- **注册功能**：新用户可通过用户名、密码、邮箱注册账户
- **登录功能**：已注册用户可通过用户名/密码登录系统
- **用户信息管理**：用户可查看和更新个人信息

### 2.2 航班搜索
- **基本搜索**：支持按出发地、目的地、出发日期进行航班搜索
- **高级搜索**：支持多维度过滤（价格范围、航空公司、舱位等级、中转次数、直飞选项）
- **排序功能**：支持按价格、时长、出发时间等维度排序
- **分页功能**：搜索结果支持分页显示

### 2.3 航班推荐
- **热门推荐**：基于全局搜索历史的热门航线推荐
- **常搜航线**：基于用户个人搜索历史的个性化推荐
- **你可能喜欢**：基于用户常搜航线的相似航线推荐
- **AI增强推荐**：使用AI技术对推荐结果进行重新排序和优化
- **个性化推荐**：基于用户偏好（时间偏好、价格区间、舱位等级等）的AI个性化推荐

### 2.4 价格监控
- **价格趋势**：提供特定航线的历史价格趋势分析
- **低价提醒**：用户可设置价格阈值，当价格低于阈值时接收提醒
- **邮件通知**：通过邮件发送低价提醒通知

### 2.5 用户反馈
- **反馈提交**：用户可对推荐的航班提交反馈（喜欢/不喜欢/中立）
- **反馈历史**：用户可查看自己的反馈历史记录
- **反馈统计**：提供用户反馈的数据统计
- **AI反馈分析**：使用AI技术分析用户反馈，提取关键洞察

### 2.6 AI服务
- **AI重新排序**：使用AI对推荐结果进行智能重新排序
- **反馈分析**：使用AI分析用户反馈的情感和原因
- **推荐效果分析**：使用AI分析推荐系统的整体性能
- **策略优化**：基于用户反馈优化推荐策略
- **服务状态监控**：监控AI服务的可用性和连接状态

### 2.7 系统管理
- **健康检查**：提供系统健康状态监控接口
- **API文档**：提供完整的Swagger UI API文档
- **数据库控制台**：提供H2数据库Web控制台（开发环境）

## 3. 非功能需求

### 3.1 性能需求
- **响应时间**：航班搜索接口响应时间应小于500ms
- **并发用户**：系统应支持至少100个并发用户
- **吞吐量**：核心接口应支持每秒至少50个请求

### 3.2 可用性需求
- **系统可用性**：系统应保证99.9%的可用性
- **故障恢复**：系统应在故障发生后5分钟内恢复
- **数据备份**：重要数据应每日备份

### 3.3 安全性需求
- **身份认证**：使用Spring Security实现用户认证
- **密码加密**：用户密码应使用BCrypt加密存储
- **API保护**：敏感API应需要认证后才能访问
- **SQL注入防护**：使用MyBatis参数化查询防止SQL注入
- **XSS防护**：对用户输入进行适当的过滤和转义

### 3.4 可扩展性需求
- **模块化设计**：系统应采用模块化设计，便于功能扩展
- **数据库扩展**：支持从H2迁移到MySQL/PostgreSQL
- **缓存支持**：支持Redis缓存，提高系统性能
- **AI服务扩展**：支持多种AI服务提供商（OpenAI、DeepSeek等）

### 3.5 兼容性需求
- **Java版本**：支持Java 21及以上版本
- **Spring Boot**：基于Spring Boot 3.4.1
- **数据库**：支持H2、MySQL、PostgreSQL
- **浏览器**：API文档支持主流现代浏览器

## 4. 系统架构

### 4.1 技术架构
- **后端框架**：Spring Boot 3.4.1
- **数据持久层**：MyBatis + H2/MySQL/PostgreSQL
- **缓存层**：Spring Data Redis（可选）
- **安全框架**：Spring Security
- **API文档**：SpringDoc OpenAPI (Swagger UI)
- **数据库迁移**：Flyway
- **邮件服务**：Spring Mail
- **AI集成**：OpenAI Java SDK（兼容DeepSeek API）

### 4.2 系统架构图
```
┌─────────────────────────────────────────────────────────────┐
│                     客户端（Web/移动端）                       │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    Spring Boot应用服务器                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   控制器层 (Controller)                │  │
│  │  • AuthController      • FlightController           │  │
│  │  • RecommendationController • PriceController       │  │
│  │  • AiController        • RouteController            │  │
│  └──────────────┬────────────────────────┬─────────────┘  │
│                 │                        │                │
│  ┌──────────────▼────────┐   ┌──────────▼──────────┐     │
│  │     服务层 (Service)    │   │    AI服务层         │     │
│  │  • FlightService      │   │  • AiService       │     │
│  │  • RecommendationService│   │  • AiClient       │     │
│  │  • PriceAlertService  │   │  • PromptBuilder   │     │
│  │  • UserFeedbackService│   │  • AiResponseParser│     │
│  └──────────────┬────────┘   └────────────────────┘     │
│                 │                                        │
│  ┌──────────────▼──────────────────────────────────────┐│
│  │               数据访问层 (Mapper)                     ││
│  │  • FlightMapper     • UserMapper                   ││
│  │  • UserFeedbackMapper • RouteMapper                ││
│  └──────────────┬──────────────────────────────────────┘│
│                 │                                       │
│  ┌──────────────▼────────┐   ┌──────────────────────┐  │
│  │       数据库层          │   │       缓存层          │  │
│  │  • H2/MySQL/PostgreSQL│   │  • Redis (可选)      │  │
│  └───────────────────────┘   └──────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 4.3 数据流
1. 用户请求通过HTTP到达控制器
2. 控制器调用相应的服务层处理业务逻辑
3. 服务层可能调用AI服务进行智能处理
4. 服务层通过数据访问层与数据库交互
5. 结果通过控制器返回给客户端

## 5. 接口需求

### 5.1 REST API接口
系统提供以下主要API接口：

| 接口类别 | 接口路径 | 方法 | 描述 |
|---------|---------|------|------|
| 健康检查 | `/api/health` | GET | 系统健康状态检查 |
| 用户认证 | `/api/auth/register` | POST | 用户注册 |
| 用户认证 | `/api/auth/login` | POST | 用户登录 |
| 航班搜索 | `/api/flights` | GET | 航班列表 |
| 航班搜索 | `/api/flights/{id}` | GET | 航班详情 |
| 航班搜索 | `/api/flights/search` | GET | 高级搜索 |
| 推荐服务 | `/api/recommendations` | GET | 个性化推荐 |
| 热门航线 | `/api/routes/popular` | GET | 热门航线排行 |
| 价格趋势 | `/api/price/trend` | GET | 价格趋势分析 |
| 低价提醒 | `/api/price/alert` | POST/GET | 创建/查看低价提醒 |
| AI反馈 | `/api/ai/feedback` | POST/GET | 提交/获取用户反馈 |
| AI分析 | `/api/ai/feedback/stats` | GET | 获取反馈统计 |
| AI分析 | `/api/ai/analyze` | POST | AI分析推荐效果 |
| AI状态 | `/api/ai/status` | GET | 获取AI服务状态 |
| AI优化 | `/api/ai/optimize` | POST | 优化推荐策略 |
| AI推荐 | `/api/ai/recommend` | POST | AI个性化推荐 |

### 5.2 数据格式
- **请求/响应格式**：JSON
- **日期格式**：ISO 8601（yyyy-MM-dd）
- **分页参数**：page（页码）、size（每页大小）
- **排序参数**：sortBy（排序字段）、direction（排序方向）

## 6. 数据需求

### 6.1 数据库设计
系统主要包含以下数据表：

#### 核心表
- **app_user**：用户信息表
- **airport**：机场信息表
- **airline**：航空公司表
- **route**：航线信息表
- **flight**：航班信息表
- **flight_price**：航班价格历史表
- **booking**：预订记录表

#### 用户行为表
- **user_flight_history**：用户航班搜索历史
- **user_feedback**：用户反馈记录（新增）
- **price_alert**：价格提醒设置

### 6.2 数据量估计
- **用户数据**：预计最多10,000个注册用户
- **航班数据**：预计最多50,000条航班记录
- **搜索历史**：预计每天最多10,000条搜索记录
- **用户反馈**：预计每天最多1,000条反馈记录

### 6.3 数据安全
- **敏感数据加密**：用户密码使用BCrypt加密
- **数据备份**：每日自动备份关键数据
- **数据清理**：定期清理过期的历史数据

## 7. 约束条件

### 7.1 技术约束
- 必须使用Java 21或更高版本
- 必须使用Spring Boot 3.4.1框架
- 数据库必须支持SQL标准
- AI服务必须兼容OpenAI API标准

### 7.2 业务约束
- 航班数据需要定期更新，确保信息的准确性
- 价格提醒功能需要用户明确授权
- AI服务的使用需要遵守相关法律法规

### 7.3 资源约束
- 开发团队：3-5名开发人员
- 开发周期：2-3个月
- 服务器资源：至少2核CPU、4GB内存、50GB存储

## 8. 假设和依赖

### 8.1 假设
1. 用户具有基本的互联网使用能力
2. 航班数据来源可靠且定期更新
3. AI服务提供商稳定可靠
4. 用户反馈真实有效，能够反映真实偏好

### 8.2 启动所需依赖与配置

#### 运行环境要求
| 依赖 | 版本要求 | 说明 |
|------|---------|------|
| Java JDK | 21+ | 后端运行环境，推荐使用 OpenJDK 21 |
| Maven | 3.9+ | 后端构建工具（或使用项目自带 `./mvnw`） |
| Node.js | 18+ | 前端运行环境 |
| npm | 9+ | 前端包管理器（随 Node.js 一起安装） |

#### 后端依赖（Maven 自动管理，无需手动安装）
| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.4.1 | 核心框架 |
| MyBatis Spring Boot Starter | 3.0.3 | 数据持久层 |
| H2 Database | 内嵌 | 开发环境内存数据库，无需额外安装 |
| Flyway Core | 内嵌 | 数据库迁移，自动执行 SQL 脚本 |
| Spring Security | 内嵌 | 用户认证与授权 |
| Spring Data Redis | 内嵌 | 缓存层（默认已禁用，可选开启） |
| Spring Mail | 内嵌 | 邮件通知（价格提醒，可选配置） |
| PageHelper | 2.1.0 | 分页插件 |
| SpringDoc OpenAPI | 2.3.0 | Swagger UI 接口文档 |
| OpenAI Java SDK | 0.18.2 | 兼容 DeepSeek API 的 AI 调用客户端 |
| Lombok | 内嵌 | 编译期代码生成 |

#### 前端依赖（npm 自动管理，无需手动安装）
| 依赖 | 版本 | 用途 |
|------|------|------|
| Vue 3 | ^3.5.0 | 前端框架 |
| Vue Router | ^4.3.0 | 前端路由 |
| Pinia | ^2.1.7 | 状态管理 |
| Axios | ^1.7.0 | HTTP 请求 |
| Element Plus | ^2.8.0 | UI 组件库 |
| Vite | ^5.4.0 | 前端构建工具 |
| TypeScript | ^5.4.5 | 类型系统 |

#### 必须配置项（启动前需手动设置）

**1. DeepSeek API Key（AI 功能必需）**

在 `src/main/resources/application.yml` 中配置，或通过环境变量注入：
```bash
export DEEPSEEK_API_KEY=your_api_key_here
```
或直接修改 `application.yml`：
```yaml
ai:
  openai:
    api-key: sk-xxxxxxxxxxxxxxxxxxxxxxxx
```
> API Key 申请地址：https://platform.deepseek.com/

**2. 邮件服务（价格提醒功能，可选）**

在 `application.yml` 中配置 SMTP 信息：
```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: your_email@example.com
    password: your_smtp_password
```

**3. Redis（缓存功能，可选）**

默认已禁用 Redis。如需启用，移除 `application.yml` 中的 `autoconfigure.exclude` 配置，并添加：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

#### 启动步骤

**后端：**
```bash
cd /path/to/Flight-Recommendation-System-AI-Version
mvn spring-boot:run
# 启动后访问 http://localhost:8080
# Swagger 文档：http://localhost:8080/swagger-ui.html
# H2 控制台：http://localhost:8080/h2-console
```

**前端：**
```bash
cd frontend
npm install   # 首次启动需安装依赖
npm run dev
# 启动后访问 http://localhost:3000
```

## 9. 验收标准

### 9.1 功能验收标准
- [x] 用户能够成功注册和登录系统
- [x] 用户能够搜索和筛选航班
- [x] 系统能够提供个性化推荐
- [x] 用户能够设置和接收价格提醒
- [x] 用户能够提交和查看反馈
- [x] AI服务能够正常分析和优化推荐

### 9.2 性能验收标准
- [ ] 航班搜索响应时间小于500ms（95%百分位）
- [ ] 系统支持100个并发用户
- [ ] API接口可用性达到99.9%

### 9.3 安全验收标准
- [ ] 用户密码加密存储
- [ ] 敏感API需要认证
- [ ] 无SQL注入漏洞
- [ ] 无XSS安全漏洞

## 10. 附录

### 10.1 术语表
- **AI**：人工智能，本系统中指基于大语言模型的智能服务
- **DeepSeek**：深度求索公司提供的AI服务
- **Flyway**：数据库迁移工具
- **MyBatis**：Java持久层框架
- **Spring Boot**：Java企业级开发框架

### 10.2 参考文档
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis官方文档](https://mybatis.org/mybatis-3/)
- [DeepSeek API文档](https://platform.deepseek.com/api-docs/)
- [OpenAI API文档](https://platform.openai.com/docs/api-reference)

---

**文档版本**：1.0  
**最后更新**：2026年4月15日  
**编写人**：Claude Code  
**审核状态**：待审核