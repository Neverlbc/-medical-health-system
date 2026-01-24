# 开发规范文档

本文件约定代码风格、目录结构、测试与日志、安全与性能优化等规则，保证多人协作的一致性与可维护性。

最后更新时间：2025-11-14

## 1. 代码规范

### 1.1 命名
- 包名：全小写，反域名规则，如 `com.medical.system.controller`
- 类名：PascalCase，如 `UserController`, `PatientService`
- 方法/变量：camelCase，如 `getUserInfo()`, `userName`
- 常量：UPPER_SNAKE_CASE，如 `MAX_RETRY_COUNT`
- 数据库：表与字段使用小写下划线，如 `patient_info`, `create_time`

### 1.2 注释
```java
/**
 * 患者信息服务
 * @author ...
 * @date 2025-11-14
 */
public class PatientService {
}
```
```java
/**
 * 获取患者详细信息
 * @param patientId 患者ID
 * @return 患者信息
 * @throws BusinessException 患者不存在
 */
public PatientInfo getPatientInfo(Long patientId) { ... }
```
// 复杂逻辑建议写行内注释，简明说明意图

### 1.3 格式
- 缩进：4 空格（Java），2 空格（前端）
- 每行尽量不超过 120 列；参数过多时换行对齐
- 方法/逻辑块之间保留空行；`import` 后空一行

### 1.4 依赖与工具
- 后端：Spring Boot 2.7.x、MyBatis-Plus、Lombok、Knife4j/Swagger3、Redis、JWT
- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、ECharts

## 2. 目录结构（后端）
```
medical-backend/
├─ medical-common/           # 公共模块（工具、异常、统一返回等）
├─ medical-system/           # 核心业务（entity/mapper/service/controller/dto/vo）
├─ medical-ai/               # AI 集成
└─ medical-admin/            # 启动模块（配置、主应用）
```

## 3. 异常与返回

### 3.1 业务异常
```java
public class BusinessException extends RuntimeException {
    private final Integer code;
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    public BusinessException(String message) { this(400, message); }
    public Integer getCode() { return code; }
}
```

### 3.2 统一返回
控制器统一返回 `Result<T>`，包含 `code/message/data/timestamp` 字段；异常通过全局异常处理器转换。

## 4. 安全规范
- 认证：JWT，除公开接口外均需 `Authorization: Bearer <token>`
- 鉴权：按角色/数据归属校验（患者只可访问自身数据）
- XSS：对用户输入进行转义；前端组件使用安全插值
- SQL 注入：使用 MyBatis 预编译参数，禁止字符串拼接 SQL
- 敏感信息：密码加密存储（BCrypt），日志不打印敏感字段

## 5. 日志规范
- 级别：ERROR（故障）、WARN（潜在问题）、INFO（业务关键）、DEBUG（调试）
```java
@Service
@Slf4j
public class PatientService {
    public void save(PatientInfo info) {
        log.info("save patient start, id={}", info.getId());
        try {
            // ...
            log.debug("payload={}", info);
        } catch (Exception e) {
            log.error("save patient failed, id={}", info.getId(), e);
            throw new BusinessException("保存失败");
        }
    }
}
```

## 6. 测试规范

### 6.1 单元测试与集成测试（后端）
```java
@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {
    @Autowired private MockMvc mockMvc;
    @Test
    void testGetPatientInfo() throws Exception {
        mockMvc.perform(get("/api/v1/patient/info")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 6.2 前端测试（建议）
- 引入 Vitest 做组件与工具函数测试
- 关键流程可用 Cypress/Playwright 做 E2E（见部署阶段）

## 7. 性能与可维护性
- 数据库：合理索引，避免 N+1 查询，分页查询优先
- 缓存：热点数据使用 Redis（`@Cacheable/@CacheEvict`）
- 异步：通知、统计等非关键路径可使用 `@Async`
- 配置：使用环境变量覆盖敏感信息（DB、Redis、DeepSeek、JWT）

## 8. 本地开发速查

### 8.1 数据库初始化
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS medical_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p medical_health < docs/sql/medical_health.sql
```

### 8.2 后端启动
```bash
cd medical-backend/medical-admin
mvn spring-boot:run
# 或指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 8.3 前端启动
```bash
cd medical-frontend
npm install
npm run dev
```

### 8.4 常用环境变量（示例）
```
DB_URL=jdbc:mysql://localhost:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=root
REDIS_HOST=localhost
REDIS_PORT=6379
DEEPSEEK_API_KEY=xxx
JWT_SECRET=please-change-in-dev
```

## 9. UI/UX 设计规范 (Digital Clinical)

为了保持系统专业且高端的医疗感，请遵循以下 UI 准则：

### 9.1 色彩体系
- **主色 (Clinical Blue)**: `#2a64ff` - 代表专业与科技。
- **辅助蓝 (Cyan Vital)**: `#64dcff` - 用于渐变，增加活力感。
- **警示色 (Alert Red)**: `#ff4d4f` - 用于健康预警与危险操作。
- **背景色 (Pure Gray)**: `#f8fafc` - 保持界面清爽。

### 9.2 组件风格
- **圆角 (Border Radius)**: 容器统一使用 `12px` - `24px` 的大圆角，营造柔和感。
- **阴影 (Box Shadow)**: 悬浮态使用 `0 8px 30px rgba(0,0,0,0.08)`，模拟立体悬浮效果。
- **玻璃拟态 (Glassmorphism)**: 侧边栏与弹窗可结合 `backdrop-filter: blur(10px)` 使用。

### 9.3 交互动效
- **页面过渡**: 使用 `page-fade` 效果（`translateY(20px)`）。
- **微动效**: 
  - 核心状态点增加 `pulse` 呼吸效。
  - 按钮悬浮时增加 `translateY(-2px)`。
  - 数据加载使用骨架屏 (Skeleton) 而非简单的 Loading。

## 10. 文档与变更
- 新增或修改接口后，更新 `docs/API.md`
- 有配置或部署变化，更新 `docs/DEPLOYMENT.md`
- 重要里程碑记录在 `docs/plans/` 中
- 每日/每周同步更新 `docs/STATUS.md`

最后更新时间：2026-01-24

