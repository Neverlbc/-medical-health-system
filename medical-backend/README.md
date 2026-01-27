# 智慧医疗健康管理系统 - 后端项目

## 项目简介

基于Spring Boot 2.7.18 + MyBatis-Plus + Redis的智慧医疗健康管理系统后端服务，集成DeepSeek AI实现智能问诊、报告解读等功能。

## 技术栈

- **核心框架**: Spring Boot 2.7.18
- **安全框架**: Spring Security + JWT
- **持久层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **API文档**: Knife4j 4.3.0
- **AI集成**: DeepSeek API
- **HTTP客户端**: OkHttp 4.12.0

## 项目结构

```
medical-backend/
├── medical-common/          # 公共模块
│   ├── config/             # 配置类
│   ├── exception/          # 异常处理
│   ├── result/             # 统一响应
│   └── utils/              # 工具类
├── medical-system/          # 系统业务模块
│   ├── entity/             # 实体类
│   ├── mapper/             # 数据访问层
│   ├── service/            # 业务层
│   ├── controller/         # 控制层
│   ├── dto/                # 数据传输对象
│   └── vo/                 # 视图对象
├── medical-ai/              # AI服务模块
│   ├── config/             # AI配置
│   ├── model/              # AI模型
│   ├── service/            # AI服务
│   └── controller/         # AI接口
└── medical-admin/           # 启动模块
    ├── resources/          # 配置文件
    └── MedicalApplication  # 启动类
```

## 快速开始

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化

```bash
# 执行SQL脚本
mysql -u root -p medical_health < ../docs/sql/medical_health.sql
```

### 3. 配置文件

修改 `medical-admin/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medical_health
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password

# DeepSeek API配置
deepseek:
  api-key: your-deepseek-api-key
```

### 4. 编译运行

```bash
# 编译项目
mvn clean install

# 运行项目
cd medical-admin
mvn spring-boot:run
```

或者使用IDE (IntelliJ IDEA)：
- 打开项目
- 找到 `MedicalApplication.java`
- 右键运行

### 5. 访问系统

- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html
- Druid监控: http://localhost:8080/druid (admin/admin)

## 核心功能

### 1. 用户认证
- 用户注册
- 用户登录
- JWT认证

### 2. AI智能问诊
- 症状分析
- 用药指导
- 健康知识问答

### 3. 健康管理
- 健康数据记录
- 医疗报告管理
- 预约挂号

## API接口

### 认证接口

```
POST /api/v1/auth/register     # 用户注册
POST /api/v1/auth/login        # 用户登录
POST /api/v1/auth/logout       # 用户退出
```

### AI问诊接口

```
POST /api/v1/ai/symptom-analysis      # 症状分析
POST /api/v1/ai/medication-guide      # 用药指导
POST /api/v1/ai/health-question       # 健康问答
```

## 默认账号

### 管理员
- 用户名: `admin`
- 密码: `admin123`

### 医生
- 用户名: `doctor1`
- 密码: `doctor123`

### 患者
- 用户名: `patient1`
- 密码: `patient123`

## 开发指南

### 添加新接口

1. 在 `entity` 包创建实体类
2. 在 `mapper` 包创建Mapper接口
3. 在 `service` 包创建Service接口和实现类
4. 在 `controller` 包创建Controller
5. 访问 http://localhost:8080/doc.html 查看API文档

### 集成DeepSeek AI

参考 `medical-ai` 模块的实现：
- `DeepSeekConfig`: 配置类
- `DeepSeekService`: 服务类
- `AIConsultationController`: 接口类

## 注意事项

1. **API Key配置**: 必须配置有效的DeepSeek API Key才能使用AI功能
2. **数据库配置**: 确保数据库连接信息正确
3. **Redis配置**: 如果Redis没有密码，配置项留空即可
4. **JWT Secret**: 生产环境请修改JWT密钥

## 常见问题

### 1. 启动失败

- 检查MySQL和Redis是否启动
- 检查配置文件中的数据库连接信息
- 查看日志文件 `logs/medical-health.log`

### 2. AI接口调用失败

- 检查DeepSeek API Key是否正确
- 检查网络连接
- 查看日志中的错误信息

### 3. 接口401未授权

- 检查Token是否过期
- 确认请求Header中包含正确的Authorization

## 项目作者

- 作者: lbc
- 类型: 毕业设计
- 时间: 2025年

## 许可证

MIT License

