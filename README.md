# 智慧医疗健康管理系统 (Smart Medical Health System) ✨

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![DeepSeek](https://img.shields.io/badge/AI-DeepSeek-blue?logo=openai&logoColor=white)](https://www.deepseek.com/)

> **极简 · 临床 · 智能** —— 基于 Spring Boot 2.7 与 DeepSeek AI 生态构建的现代化智慧医疗管理解决方案。

---

## 🎨 视觉美学 (Digital Clinical Design)

本项目采用 **"数字化诊疗 (Digital Clinical)"** 视觉体系，追求 **"高端极简 (Premium Minimalist)"** 的交互体验：
- **高端医疗色调**：深邃医疗蓝、活力青与纯净白的和谐统一。
- **动态交互**：全屏 DNA 螺旋动效、AI 机器人微表情、骨架屏加载、流畅的页面过渡。
- **数字化看板**：立体化统计卡片、实时脉动系统状态、专业医学图表。

## ✨ 核心功能

### 1. 数字化健康档案 📋
- **全生命周期管理**：电子病历、体检报告、用药史、过敏史的一站式存储与回溯。
- **智能化检索**：支持关键词多维度搜索与附件在线预览。

### 2. AI 临床级智能问诊 🤖
- **深度症状分析**：利用 DeepSeek 大模型进行多轮对话，提供精准的健康咨询与科室引导。
- **医学验证保障**：AI 回复经过预设医学逻辑校验，提供专业化建议。
- **情感化交互**：定制化医疗机器人形象，具备实时录入反馈与打字机效果。

### 3. 健康预测与实时监测 📈
- **体征趋势可视化**：血压、血糖、心率、体温等关键指标的动态对比看板。
- **异常智能预警**：基于临床标准的 5 级状态自动判定（正常、注意、警告、严重、危险）。
- **AI 健康周报**：自动汇总监测数据，生成个性化健康建议。

### 4. 智慧挂号与导诊 📅
- **动态排班系统**：医生工作模式与号源实时同步。
- **冲突检测机制**：智能识别重叠预约，保障挂号流程丝滑无阻。

### 5. 闭环用药提醒 ⏰
- **智能化方案制定**：根据病历自动建议用药计划（需医生确认）。
- **实时穿透推送**：WebSocket 实时推送，确保不错过任何一次服药。

### 6. 医疗报告深度解读 🤖
- **OCR/结构化提取**：自动解析体检报告中的异常项，由 AI 提供通俗易懂的解读。
- **风险预测**：基于历史数据预测潜在健康风险。

## 🛠️ 技术栈

### 后端技术
- **核心框架**: Spring Boot 2.7.x
- **安全框架**: Spring Security + JWT
- **持久层**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **API文档**: Knife4j (Swagger3)
- **日志**: Logback + SLF4J
- **定时任务**: Quartz
- **实时通信**: WebSocket
- **AI集成**: DeepSeek API

### 前端技术
- **框架**: Vue 3.x
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4.x
- **HTTP客户端**: Axios
- **图表**: ECharts
- **构建工具**: Vite

### 开发工具
- **项目管理**: Maven 3.8+
- **版本控制**: Git
- **开发IDE**: IntelliJ IDEA / VSCode
- **接口测试**: Postman / Apifox

## 📦 项目结构

```
medical-health-system/
├── docs/                           # 项目文档
│   ├── sql/                       # 数据库脚本
│   ├── api/                       # API接口文档
│   └── design/                    # 设计文档
├── medical-backend/                # 后端项目
│   ├── medical-common/            # 公共模块
│   │   ├── common-core/          # 核心工具类
│   │   ├── common-security/      # 安全配置
│   │   └── common-redis/         # Redis配置
│   ├── medical-system/            # 系统服务模块
│   │   ├── controller/           # 控制器
│   │   ├── service/              # 业务层
│   │   ├── mapper/               # 数据访问层
│   │   ├── entity/               # 实体类
│   │   └── dto/                  # 数据传输对象
│   ├── medical-ai/                # AI服务模块
│   │   ├── deepseek/             # DeepSeek集成
│   │   ├── analyzer/             # 数据分析
│   │   └── recommender/          # 推荐引擎
│   ├── medical-schedule/          # 定时任务模块
│   └── medical-admin/             # 系统管理模块
├── medical-frontend/               # 前端项目
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── components/           # 公共组件
│   │   ├── views/                # 页面
│   │   ├── router/               # 路由
│   │   ├── store/                # 状态管理
│   │   ├── utils/                # 工具类
│   │   └── assets/               # 静态资源
│   └── public/
├── .gitignore
├── README.md
└── pom.xml                         # Maven父项目配置
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+

### 配置敏感信息（环境变量）
后端 `application.yml` 通过环境变量读取数据库、Redis、JWT 与 DeepSeek 配置。启动前请设置以下变量（示例值可按需调整）：

```powershell
# Windows PowerShell
$Env:DB_URL="jdbc:mysql://localhost:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$Env:DB_USERNAME="root"
$Env:DB_PASSWORD="<your_db_password>"
$Env:REDIS_HOST="localhost"
$Env:REDIS_PORT="6379"
$Env:REDIS_PASSWORD=""
$Env:JWT_SECRET="<generate_a_strong_secret>"
$Env:DEEPSEEK_API_KEY="<your_deepseek_api_key>"
# 可选覆盖
$Env:DEEPSEEK_API_URL="https://api.deepseek.com/v1/chat/completions"
$Env:DEEPSEEK_MODEL="deepseek-chat"
```

```bash
# macOS / Linux
export DB_URL="jdbc:mysql://localhost:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="<your_db_password>"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""
export JWT_SECRET="<generate_a_strong_secret>"
export DEEPSEEK_API_KEY="<your_deepseek_api_key>"
# 可选覆盖
export DEEPSEEK_API_URL="https://api.deepseek.com/v1/chat/completions"
export DEEPSEEK_MODEL="deepseek-chat"
```

如果未设置某个变量，应用会使用 `application.yml` 中定义的安全默认值；生产环境请务必覆盖默认值。

### 后端启动

1. **克隆项目**
```bash
git clone [项目地址]
cd medical-health-system
```

2. **创建数据库**
```bash
# 执行数据库脚本
mysql -u root -p < docs/sql/medical_health.sql
```

3. **配置敏感信息**
确保已按上文设置环境变量，或在本地新增 `application-local.yml` 并通过 `spring.profiles.active=local` 方式覆盖默认值。

4. **编译运行**
```bash
# 编译项目
mvn clean install

# 运行主程序
cd medical-backend/medical-admin
mvn spring-boot:run
```

后端服务默认运行在: http://localhost:8080

### 前端启动

1. **安装依赖**
```bash
cd medical-frontend
npm install
```

2. **修改配置**
```javascript
// .env.development
VITE_APP_BASE_API=http://localhost:8080
```

3. **运行项目**
```bash
npm run dev
```


前端服务默认运行在: http://localhost:3000

## 📚 文档说明

- [数据库设计文档](docs/DATABASE.md)
- [API接口文档](docs/API.md)
- [DeepSeek集成指南](docs/DEEPSEEK_INTEGRATION.md)
- [部署文档](docs/DEPLOYMENT.md)
- [开发规范](docs/DEVELOPMENT.md)
- [文档索引（建议从这里开始）](docs/README.md)
- [迭代计划（进行中）](docs/plans/README.md)

## 🔑 默认账号

### 管理员
- 账号: admin
- 密码: admin123

### 医生
- 账号: doctor
- 密码: doctor123

### 患者
- 账号: patient
- 密码: patient123

## 📝 开发进度 (截至 2026-01-24)

- [x] **项目架构设计** - 后端多模块 Spring Boot + 前端 Vue 3 生态
- [x] **数据库设计** - 12张核心业务表 + 视图 + 触发器
- [x] **用户认证模块** - JWT 全局认证 + 角色动态路由
- [x] **Premium UI/UX 重塑** ⭐ - "Digital Clinical" 视觉体系，全系统高端化优化
- [x] **DeepSeek AI 集成** - 症状分析、健康问答、用药指导
- [x] **健康档案模块** - 电子病历存储、附件管理、历史回溯
- [x] **健康监测模块** - ECharts 趋势图、5级临床状态自动判定、异常智能预警
- [x] **智能问诊功能完善** - 定制化 AI 机器人、Markdown 渲染、对话历史保存
- [x] **前端页面开发** - 患者/医生/管理员三端控制台全面美化
- [x] **系统集成测试** - 核心业务流程前后端联通性测试
- [/] **预约挂号模块** (进行中 - 55%) - UI 框架已就绪，医生排班逻辑开发中
- [/] **用药提醒模块** (进行中 - 25%) - 消息推送机制设计中
- [ ] **报告解读功能** (计划中) - AI OCR 结构化提取与深度解析
- [x] **Git 仓库管理** - 代码已同步至远程仓库 (GitHub)
- [ ] **多端适配优化** - 移动端响应式布局精修

## ⚠️ 重要说明

1. **医疗免责声明**: 本系统提供的AI诊断建议仅供参考，不能替代专业医生的诊断。如有不适，请及时就医。

2. **数据安全**: 系统涉及个人健康隐私数据，已采取加密存储、访问控制等安全措施。

3. **API限制**: DeepSeek API调用有频率限制，建议合理使用并做好缓存策略。

## 🤝 贡献指南

欢迎提交Issue和Pull Request

## 📄 开源协议

本项目采用 MIT 协议

## 👨‍💻 作者信息

- 作者: 刘柏城
- 项目类型: 毕业设计
- 完成时间: 2025年

## 📞 联系方式

如有问题，欢迎通过以下方式联系：
- Email: [您的邮箱]
- GitHub: [您的GitHub]

---

⭐ 如果这个项目对你有帮助，欢迎Star支持！

