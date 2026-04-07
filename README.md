# 机票推荐系统

基于 Spring Boot 3.4 的机票推荐系统后端服务，为用户提供航班搜索、比价、个性化推荐、价格趋势、低价提醒等功能。已按 `DEVELOPMENT_PLAN.md` 完成全部 6 个 Sprint 开发。

## 项目概述

机票推荐系统旨在帮助用户快速找到合适的航班，支持按出发地、目的地、日期、价格等条件筛选，并结合用户偏好进行智能推荐。

## 技术栈

- Java 21
- Spring Boot 3.4.1
- Spring Web、Security、AOP
- MyBatis + H2 / MySQL / PostgreSQL
- PageHelper（分页）
- Spring Data Redis（可选缓存）
- SpringDoc OpenAPI（Swagger）
- Flyway（数据库迁移）
- Spring Mail（邮件通知）

## 已实现功能

- [x] 航班搜索（出发地、目的地、日期、分页、排序、过滤）
- [x] 航班比价与多维度排序（价格、时长、综合）
- [x] 个性化推荐（热门、常搜航线、你可能喜欢）
- [x] 用户偏好记录（AOP 埋点）
- [x] 价格趋势查询
- [x] 低价提醒 + 邮件通知
- [x] 热门航线排行
- [x] API 文档（Swagger UI）
- [x] 健康检查（Actuator）

## API 接口总览

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/health` | GET | 健康检查 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/login` | POST | 用户登录 |
| `/api/flights` | GET | 航班列表 |
| `/api/flights/{id}` | GET | 航班详情 |
| `/api/flights/search` | GET | 高级搜索（分页、排序、过滤） |
| `/api/recommendations` | GET | 个性化推荐 |
| `/api/routes/popular` | GET | 热门航线排行 |
| `/api/price/trend` | GET | 价格趋势 |
| `/api/price/alert` | POST/GET | 低价提醒（创建/列表） |
| `/swagger-ui.html` | GET | API 文档 |
| `/actuator/health` | GET | 应用健康 |

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

### 示例：航班搜索

```
GET /api/flights/search?departure=PEK&arrival=PVG&departureDate=2025-03-18&page=0&size=10&sortBy=PRICE_ASC
```

### 打包

```bash
mvn clean package
java -jar target/springboot-app-1.0.0-SNAPSHOT.jar
```

## 项目结构

```
src/main/java/com/example/app/
├── SpringbootAppApplication.java   # 启动类（已完成）
├── aspect/                         # AOP 切面（用户行为埋点）（已完成）
├── common/                          # 通用组件（Result、异常处理）（已完成）
├── config/                          # 配置（Security、Redis、缓存、OpenAPI）（已完成）
├── controller/                      # 控制器（已完成）
├── dto/                             # 数据传输对象（已完成）
├── entity/                          # 实体类（已完成）
├── mapper/                          # MyBatis Mapper（已完成）
├── scheduler/                       # 定时任务（价格采集、提醒检查）（已完成）
├── security/                        # 认证（AuthUser）（已完成）
├── service/                         # 业务逻辑层（已完成）
└── ...
```

## 配置说明

- **开发环境**：默认排除 Redis，无需本地安装
- **Redis 缓存**：激活 `redis` profile 可启用 Redis 缓存
- **邮件通知**：需配置 `spring.mail.*`，否则低价提醒仅记录不发送

## 后续扩展建议

1. 对接真实航司或第三方航班数据 API
2. 接入 MySQL/PostgreSQL 生产数据库
3. 增强推荐算法（协同过滤、机器学习）
4. 前端 Web 或移动端
