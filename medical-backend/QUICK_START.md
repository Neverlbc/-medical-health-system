# 快速启动指南

## 📋 前置条件

### 软件要求
- ✅ JDK 1.8 或更高版本
- ✅ Maven 3.6+  
- ✅ MySQL 8.0+
- ✅ Redis 6.0+
- ✅ IntelliJ IDEA 或 Eclipse (推荐IDEA)

### DeepSeek API
- 注册账号: https://platform.deepseek.com
- 获取API Key

## 🚀 启动步骤

### 第一步：导入项目

#### 使用IntelliJ IDEA
1. 打开IDEA
2. File → Open
3. 选择 `medical-backend` 文件夹
4. 等待Maven依赖下载完成

#### 使用Eclipse
1. 打开Eclipse
2. File → Import → Existing Maven Projects
3. 选择 `medical-backend` 文件夹
4. 等待Maven依赖下载完成

### 第二步：准备数据库

```bash
# 1. 启动MySQL
# Windows: 打开MySQL服务
# Linux/Mac: sudo service mysql start

# 2. 创建数据库并导入数据
mysql -u root -p

# 在MySQL命令行执行:
CREATE DATABASE IF NOT EXISTS medical_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;

# 3. 导入SQL脚本
mysql -u root -p medical_health < ../docs/sql/medical_health.sql
mysql -u root -p medical_health < ../docs/sql/insert_test_data.sql
```

### 第三步：启动Redis

```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server

# 或者使用Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### 第四步：修改配置文件

编辑 `medical-admin/src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root           # 修改为你的MySQL用户名
    password: root           # 修改为你的MySQL密码
  
  redis:
    host: localhost
    port: 6379
    password:                # 如果Redis有密码，填写密码；没有则留空

# DeepSeek AI配置
deepseek:
  api-key: your-deepseek-api-key-here    # 修改为你的DeepSeek API Key
```

### 第五步：运行项目

#### 方式一：使用IDEA运行
1. 找到 `medical-admin/src/main/java/com/medical/MedicalApplication.java`
2. 右键 → Run 'MedicalApplication'
3. 等待启动完成

#### 方式二：使用Maven命令
```bash
# 在 medical-backend 目录下执行
mvn clean install

# 进入 medical-admin 目录
cd medical-admin

# 运行
mvn spring-boot:run
```

#### 方式三：打包后运行
```bash
# 打包
mvn clean package

# 运行jar包
cd medical-admin/target
java -jar medical-admin.jar
```

### 第六步：验证启动

启动成功后，控制台会显示：

```
========================================
  智慧医疗健康管理系统启动成功！
  API文档地址: http://localhost:8080/doc.html
========================================
```

访问以下地址验证：
- ✅ API文档: http://localhost:8080/doc.html
- ✅ Druid监控: http://localhost:8080/druid (用户名/密码: admin/admin)

## 🧪 测试接口

### 1. 测试用户注册

使用Postman或API文档测试：

**请求:**
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "phone": "13900000000",
  "role": "PATIENT"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "注册成功",
  "timestamp": 1699276800000
}
```

### 2. 测试用户登录

**请求:**
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "role": "ADMIN",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "expiresIn": 7200
  },
  "timestamp": 1699276800000
}
```

### 3. 测试AI症状分析

**请求:**
```http
POST http://localhost:8080/api/v1/ai/symptom-analysis
Content-Type: application/json
Authorization: Bearer {token}

{
  "symptoms": "我最近经常头痛，还伴有恶心的症状"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "根据您描述的症状，可能的原因包括：...",
  "timestamp": 1699276800000
}
```

## 🔍 常见问题

### 问题1: 启动时报错 "Connection refused"

**原因:** MySQL或Redis未启动

**解决:**
```bash
# 检查MySQL
mysql -u root -p

# 检查Redis  
redis-cli ping
# 应该返回 PONG
```

### 问题2: 数据库连接失败

**原因:** 数据库配置错误

**解决:**
1. 检查 `application-dev.yml` 中的数据库配置
2. 确认数据库名称、用户名、密码正确
3. 确认MySQL服务正在运行

### 问题3: Maven依赖下载失败

**原因:** 网络问题或Maven仓库配置

**解决:**
1. 检查网络连接
2. 使用阿里云Maven镜像（项目已配置）
3. 删除 `.m2/repository` 重新下载

### 问题4: 端口8080被占用

**解决:**

修改 `application.yml`:
```yaml
server:
  port: 8081  # 改为其他端口
```

### 问题5: AI接口调用失败

**原因:** DeepSeek API Key未配置或无效

**解决:**
1. 注册DeepSeek账号: https://platform.deepseek.com
2. 获取API Key
3. 在 `application-dev.yml` 中配置正确的API Key

## 📊 项目架构

```
medical-backend
├── medical-common          公共模块
│   ├── config/            配置类(Security, MyBatisPlus, Redis)
│   ├── exception/         异常处理
│   ├── result/            统一响应
│   └── utils/             工具类(JWT, Security)
├── medical-system         系统业务模块  
│   ├── entity/            实体类(User, Patient, Doctor, etc.)
│   ├── mapper/            Mapper接口
│   ├── service/           服务层
│   ├── controller/        控制器(Auth)
│   ├── dto/               请求DTO
│   └── vo/                响应VO
├── medical-ai             AI服务模块
│   ├── config/            DeepSeek配置
│   ├── model/             请求/响应模型
│   ├── service/           AI服务
│   └── controller/        AI接口
└── medical-admin          启动模块
    └── MedicalApplication 启动类
```

## 📚 下一步

项目成功启动后，你可以：

1. **查看API文档**: http://localhost:8080/doc.html
2. **开发新功能**: 参考现有代码结构添加新接口
3. **前端开发**: 基于API开发前端页面
4. **功能测试**: 测试所有接口功能
5. **性能优化**: 优化数据库查询、添加缓存

## 🎓 学习资源

- Spring Boot官方文档: https://spring.io/projects/spring-boot
- MyBatis-Plus文档: https://baomidou.com
- DeepSeek API文档: https://platform.deepseek.com/api-docs
- Knife4j文档: https://doc.xiaominfo.com

## 💬 技术支持

如果遇到问题:
1. 查看项目 `README.md` 
2. 查看日志文件 `logs/medical-health.log`
3. 检查数据库和Redis连接

---

**祝你项目开发顺利！** 🎉

