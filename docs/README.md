# 项目文档索引

本目录汇总并规范项目文档，便于快速查阅与协作。

## 索引
- 快速了解
  - 项目概览与快速开始：根目录 `README.md`
  - 环境/部署：`DEPLOYMENT.md`
- 开发与规范
  - 开发规范：`DEVELOPMENT.md`
  - 接口文档：`API.md`
  - 数据库设计与脚本：`DATABASE.md`、`sql/`
  - AI 集成说明（DeepSeek）：`DEEPSEEK_INTEGRATION.md`
  - 代理/智能体约定：`AGENTS.md`
- 计划与管理
  - 迭代计划总览：`plans/README.md`
  - 具体迭代计划：`plans/iteration-01.md`、`plans/iteration-02.md`、`plans/iteration-03.md`
  - 长期改进方案：`plans/health-record-improvement-plan.md`
  - 项目状态总览：`STATUS.md`
- 历史与归档
  - 功能验证与测试报告：`archive/`

## 文档约定
- 文件命名：使用英文短横线命名（如 `iteration-01.md`），目录与文件名尽量语义化
- 结构模板（建议）
  - 标题 + 摘要
  - 背景/目标（可选）
  - 操作步骤/接口/方案
  - 验收标准（若为任务/计划）
  - 更新记录（末尾追加“最后更新：YYYY-MM-DD”）
- 术语：与后端/前端代码中的领域术语保持一致（如“健康档案/监测/预约”等）
- 代码片段：使用围栏代码块并标注语言
- 图片与附件：放入 `docs/assets/` 或相邻子目录，引用相对路径

最后更新：2026-01-24
