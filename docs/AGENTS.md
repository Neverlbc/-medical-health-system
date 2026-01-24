# Repository Guidelines

本指南面向参与该仓库的开发者与自动化代理（Agents），规范项目结构、构建与协作流程。

## 项目结构与模块组成
- medical-frontend：Vue 3 + TypeScript（Vite）
  - 目录：`src/api`、`src/router`、`src/store`、`src/views`、`src/styles`
- medical-backend：Maven 多模块 Spring Boot（Java 17）
  - medical-common：通用配置、异常、工具、统一返回
  - medical-system：核心业务（entity/mapper/service/controller）
  - medical-ai：AI 集成（DeepSeek）
  - medical-admin：启动模块（`MedicalApplication`）
- docs/sql：数据库初始化脚本；logs：后端日志输出

## 构建、测试与本地运行
- 前端（在 `medical-frontend`）
  - `npm install` / `npm ci`：安装依赖
  - `npm run dev`：启动 Vite（http://localhost:5173，`/api` 代理至后端）
  - `npm run build`：生产构建
  - `npm run preview`：本地预览构建产物
  - `npm run lint`：ESLint 检查
- 后端（在 `medical-backend`）
  - `mvn clean install`：构建所有模块
  - `mvn -pl medical-admin -am spring-boot:run`：运行（默认 `dev` 配置）
  - `java -jar medical-admin/target/medical-admin.jar`：运行打包产物
  - `mvn test` 或 `mvn -pl medical-ai test`：运行测试

## 代码风格与命名约定
- 前端：遵循 `eslint.config.js`
  - 2 空格缩进；TS + Vue SFC；组件文件 `PascalCase.vue`（如 `DashboardView.vue`）
  - API 放在 `src/api/modules/*.ts`；Pinia Store 放在 `src/store/modules/*`
  - 已关闭规则：`@typescript-eslint/no-explicit-any`、`vue/multi-word-component-names`
- 后端：Java 17，4 空格缩进，包名前缀 `com.medical...`
  - 类名 `UpperCamelCase`；方法/字段 `lowerCamelCase`；常量 `UPPER_SNAKE_CASE`
  - 使用 Lombok；持久层使用 MyBatis-Plus

## 测试规范
- 后端：JUnit 5（`spring-boot-starter-test`）
  - 目录 `src/test/java`；类名以 `*Test.java`（如 `DeepSeekServiceTest`）
  - 覆盖服务与控制器，外部调用建议 Mock
- 前端：推荐引入 Vitest（见迭代计划）

## 提交与合并请求
- 建议采用 Conventional Commits
  - 示例：`feat(ai): 增加症状分析接口`、`fix(auth): 修复登录 token 续期`
- PR 要求：清晰描述、关联 Issue（如 `Closes #123`）、UI 变更附截图/GIF、测试说明、涉及 DB/配置的迁移说明

## 安全与配置建议
- 后端通过环境变量覆盖 `application.yml`：`DB_URL/DB_USERNAME/DB_PASSWORD`、`REDIS_*`、`DEEPSEEK_API_KEY`；切勿提交密钥
- 前端设置 `VITE_APP_BASE_API`（见 `vite.config.ts` 代理）
- 启动前先导入 `docs/sql/*.sql`，并确保 Redis 可用

最后更新：2025-11-14

