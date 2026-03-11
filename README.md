# 机票推荐系统

基于 Spring Boot 3.4 的机票推荐系统后端服务，为用户提供航班搜索、比价、个性化推荐等功能。

## 项目概述

机票推荐系统旨在帮助用户快速找到合适的航班，支持按出发地、目的地、日期、价格等条件筛选，并结合用户偏好进行智能推荐。

## 技术栈

- Java 21
- Spring Boot 3.4.1
- Spring Web
- Spring Validation
- Lombok

## 计划功能

- [ ] 航班搜索（出发地、目的地、日期）
- [ ] 航班比价与排序
- [ ] 个性化推荐（基于用户偏好、历史订单）
- [ ] 价格趋势与低价提醒
- [ ] 热门航线排行

## 项目结构

```
src/main/java/com/example/app/
├── SpringbootAppApplication.java   # 启动类
├── common/                          # 通用组件（Result、异常处理）
├── config/                          # 配置类（CORS 等）
├── controller/                      # 控制器（航班、推荐等接口）
├── dto/                             # 数据传输对象（搜索条件、推荐结果等）
├── entity/                          # 实体类（航班、航线、用户偏好等）
├── repository/                      # 数据访问层
└── service/                         # 业务逻辑层（推荐算法、搜索等）
```

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

### 打包

```bash
mvn clean package
java -jar target/springboot-app-1.0.0-SNAPSHOT.jar
```

## 后续扩展建议

1. **数据库**：添加 `spring-boot-starter-data-jpa` 存储航班数据、用户偏好、历史记录
2. **缓存**：添加 Redis 缓存热点航线、价格信息
3. **外部 API**：对接航司或第三方航班数据接口
4. **安全**：添加 `spring-boot-starter-security` 支持用户登录与鉴权
