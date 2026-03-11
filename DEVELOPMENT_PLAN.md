# 机票推荐系统 — 开发周期规划

两周为一个 Sprint，支持迭代交付。

---

## Sprint 1（第 1–2 周）— 基础设施与数据模型

**目标**：搭建基础架构，建立核心数据模型与存储

| 任务 | 内容 | 产出 |
|------|------|------|
| 数据库接入 | 引入 MyBatis + H2/MySQL/PostgreSQL，完成数据源配置 | 可用的数据库连接 |
| 实体设计 | 定义 `Flight`（航班）、`Route`（航线）、`Airport`（机场）等实体 | 实体类、表结构、基础 CRUD |
| 种子数据 | 编写或导入测试航班数据（至少 100+ 条） | 可用测试数据集 |
| 用户模块（基础） | 用户表设计，注册/登录接口 | 用户实体与基本认证 |

**Sprint 1 验收**：能通过接口查询航班列表，完成用户注册/登录。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| 持久层 | MyBatis | `@Mapper`、Mapper XML、`resultMap`、动态 SQL |
| 数据库 | H2 / MySQL / PostgreSQL | 建表、索引、数据类型、基本 SQL |
| 连接池 | HikariCP | 默认已集成，了解配置项 |
| 实体映射 | MyBatis resultMap | 字段映射、驼峰命名、关联查询 |
| 认证 | Spring Security（基础）| 表单登录、BCrypt 密码加密、Session 管理 |
| 配置 | application.yml | 数据源、MyBatis 配置、多环境 profile |
| 工具 | Flyway / Liquibase | 数据库迁移（可选） |
| 工具 | Lombok | `@Data`、`@Builder`、`@NoArgsConstructor` |

---

## Sprint 2（第 3–4 周）— 航班搜索

**目标**：实现按条件搜索与筛选

| 任务 | 内容 | 产出 |
|------|------|------|
| 搜索接口 | 支持出发地、目的地、出发日期、返程日期 | `GET /api/flights/search` |
| 搜索 DTO | `FlightSearchRequest`、`FlightSearchResponse` | 统一的请求/响应结构 |
| 分页排序 | 分页、按价格/时长/出发时间排序 | 分页与排序参数支持 |
| 基础过滤 | 舱位、航司、经停条件 | 可选过滤条件 |

**Sprint 2 验收**：可输入条件搜索航班，并支持分页与多维度排序。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| 查询 | MyBatis 动态 SQL | `<if>`、`<where>`、`<foreach>` 多条件组合 |
| 分页 | Spring Data `Pageable` | `PageRequest`、`Page<T>`、`Sort` |
| 参数校验 | Bean Validation | `@Valid`、`@NotNull`、`@Size`、自定义校验 |
| DTO | 请求/响应对象 | 与 Entity 分离、`@RequestBody`、`ModelMapper` / MapStruct |
| 日期处理 | Java 8 Date/Time | `LocalDate`、`LocalDateTime`、时区 |
| REST | Spring MVC | `@GetMapping`、`@RequestParam`、`@PathVariable` |
| 查询优化 | MyBatis 关联查询 | `<association>`、`<collection>`、避免 N+1 |
| 文档 | 接口设计 | RESTful 规范、HTTP 状态码、统一响应格式 |

---

## Sprint 3（第 5–6 周）— 比价与排序增强

**目标**：完善比价逻辑，增强排序与展示

| 任务 | 内容 | 产出 |
|------|------|------|
| 多航司比价 | 同一航线多航司结果聚合与比较 | 比价结果展示 |
| 排序策略 | 价格升序、时长最短、综合评分 | 多种排序方式 |
| 缓存引入 | Redis 缓存热门搜索、热门航线 | 提升查询性能 |
| 接口优化 | 响应结构优化、字段精简 | 更清晰的前端数据结构 |

**Sprint 3 验收**：比价结果正确，热门搜索响应时间明显缩短。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| 缓存 | Spring Data Redis | `RedisTemplate`、`@Cacheable`、序列化配置 |
| 缓存 | Redis 数据结构 | String、Hash、Sorted Set（排行榜） |
| 注解 | `@Cacheable` / `@CacheEvict` | 缓存键设计、过期时间、缓存失效 |
| 业务逻辑 | 聚合与分组 | Stream API、`Collectors.groupingBy`、多航司合并 |
| 排序 | 多条件排序 | `Comparator`、`Comparator.thenComparing` |
| 并发 | 缓存穿透/击穿 | 空值缓存、分布式锁（可选） |
| 配置 | Redis 连接 | `application.yml`、`Lettuce` 连接池 |
| 工具 | Redis CLI | 查看 key、调试缓存内容 |

---

## Sprint 4（第 7–8 周）— 个性化推荐

**目标**：实现基于用户行为的推荐

| 任务 | 内容 | 产出 |
|------|------|------|
| 偏好记录 | 记录用户搜索、浏览、收藏的航线/航班 | 用户偏好表与埋点逻辑 |
| 推荐算法 | 基于历史行为的简单推荐（协同/规则） | `GET /api/recommendations` |
| 推荐策略 | 「热门」「你可能喜欢」「常搜航线」 | 至少 2–3 种推荐维度 |
| 用户画像 | 基础标签（如偏好低价/直飞） | 简单画像结构 |

**Sprint 4 验收**：登录用户可获得个性化航班推荐。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| 埋点 | 用户行为记录 | 拦截器 / AOP 记录搜索、浏览、收藏 |
| AOP | Spring AOP | `@Around`、`@Before`、切点表达式 |
| 推荐 | 协同过滤（基础）| 基于用户/基于物品的相似度计算 |
| 推荐 | 规则推荐 | IF-THEN 规则、权重 scoring |
| 算法 | 相似度 | 余弦相似度、Jaccard、欧氏距离 |
| 用户画像 | 标签系统 | 偏好标签存储、标签权重更新 |
| 安全 | 用户上下文 | `SecurityContextHolder`、`@AuthenticationPrincipal` |
| SQL | 统计查询 | `COUNT`、`GROUP BY`、子查询、窗口函数 |
| 数据模型 | 偏好表设计 | 用户-航线-行为 多对多、时间衰减 |

---

## Sprint 5（第 9–10 周）— 价格趋势与提醒

**目标**：提供价格趋势和低价提醒

| 任务 | 内容 | 产出 |
|------|------|------|
| 价格历史 | 记录与存储航班价格变化 | 价格历史表与采集任务 |
| 趋势接口 | 查询某航线近期价格走势 | `GET /api/price/trend` |
| 低价提醒 | 用户设置目标航线 + 期望价格，达标时通知 | 提醒表、定时任务、通知接口 |
| 通知通道 | 邮件或站内消息（可选对接短信） | 至少一种通知方式 |

**Sprint 5 验收**：可查看价格趋势，成功创建低价提醒并收到通知。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| 定时任务 | `@Scheduled` | 固定周期、Cron 表达式 |
| 定时任务 | Spring Task | `@EnableScheduling`、线程池配置 |
| 异步 | `@Async` | 异步执行、`CompletableFuture`（可选） |
| 价格采集 | 模拟/对接 API | HTTP 客户端、`RestTemplate` / `WebClient` |
| 通知 | JavaMailSender | 发送邮件、HTML 模板 |
| 通知 | Thymeleaf / FreeMarker | 邮件模板渲染 |
| 数据模型 | 价格历史表 | 时间序列数据、分区表（可选） |
| 提醒逻辑 | 条件匹配 | 定时扫描、价格对比、去重 |
| HTTP 客户端 | WebClient / RestTemplate | 调用外部 API、超时、重试 |

---

## Sprint 6（第 11–12 周）— 热门航线与收尾

**目标**：完善排行与体验，准备上线

| 任务 | 内容 | 产出 |
|------|------|------|
| 热门航线 | 基于搜索量/订单量的排行 | `GET /api/routes/popular` |
| 性能优化 | 慢查询、缓存命中、索引优化 | 性能监控与优化记录 |
| 文档与测试 | API 文档（如 Swagger）、核心用例测试 | 可维护的文档与测试集 |
| Bug 修复 | 全流程回归，修复已知问题 | 可对外演示的版本 |

**Sprint 6 验收**：热门航线排行可用，主要接口有文档和测试，无阻塞性 Bug。

**技术栈与学习清单**：

| 类别 | 技术/知识点 | 学习要点 |
|------|-------------|----------|
| API 文档 | SpringDoc / Swagger | `@Operation`、`@Schema`、分组、在线调试 |
| API 文档 | OpenAPI 3.0 | 规范、`/v3/api-docs`、`/swagger-ui.html` |
| 测试 | JUnit 5 | `@Test`、`@ParameterizedTest`、断言 |
| 测试 | MockMvc | 接口测试、`MockBean`、JSON 断言 |
| 测试 | Testcontainers（可选）| 集成测试用真实 MySQL/Redis |
| 性能 | 慢查询分析 | EXPLAIN、索引优化、执行计划 |
| 性能 | 监控 | Actuator、`/actuator/health`、metrics |
| Redis | Sorted Set | `ZADD`、`ZREVRANGE` 实现排行榜 |
| 工程 | 代码规范 | 统一异常、日志、参数校验 |

---

## 迭代说明

- **Sprint 1–2**：核心搜索闭环，可优先交付最小可用版本（MVP）。
- **Sprint 3–4**：在搜索基础上增强体验与推荐能力。
- **Sprint 5–6**：价格与运营类功能，可按业务优先级调整或拆入后续 Sprint。
- 每个 Sprint 结束进行一次 Demo 和回顾，根据反馈调整下一 Sprint 计划。

---

## 时间线总览

| Sprint | 周次 | 核心产出 |
|--------|------|----------|
| Sprint 1 | 1–2 | 数据库、实体、用户基础、种子数据 |
| Sprint 2 | 3–4 | 航班搜索、分页、排序、过滤 |
| Sprint 3 | 5–6 | 比价、多排序策略、Redis 缓存 |
| Sprint 4 | 7–8 | 偏好记录、推荐接口、用户画像 |
| Sprint 5 | 9–10 | 价格趋势、低价提醒、通知 |
| Sprint 6 | 11–12 | 热门航线、优化、文档、测试 |

**总周期**：约 12 周（6 个 Sprint）。

---

## 技术栈汇总（按 Sprint）

| Sprint | 核心技术 | 扩展技术 |
|--------|----------|----------|
| 1 | MyBatis、H2/MySQL/PostgreSQL、Spring Security、HikariCP、Lombok | Flyway、BCrypt |
| 2 | MyBatis 动态 SQL、PageHelper、Bean Validation、LocalDate | ModelMapper、关联查询 |
| 3 | Spring Data Redis、Redis 数据结构、@Cacheable | Lettuce、Comparator、Stream API |
| 4 | Spring AOP、协同过滤、标签系统、SecurityContext | 相似度算法、窗口函数 |
| 5 | @Scheduled、JavaMailSender、WebClient/RestTemplate | Thymeleaf、@Async、Cron |
| 6 | SpringDoc/Swagger、JUnit 5、MockMvc、Actuator | OpenAPI、Testcontainers、Redis Sorted Set |
