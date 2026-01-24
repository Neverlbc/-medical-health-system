# 医疗健康管理系统前端

基于 Vite + Vue 3 + TypeScript + Element Plus 的项目骨架，用于对接后端智慧医疗健康管理系统，提供登录、健康数据、预约挂号、AI 问诊等功能界面。

## 快速开始

```bash
cd medical-frontend
npm install

# 开发调试
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

默认代理 `/api` 到 `http://localhost:8080`，可通过 `.env.development` 中的 `VITE_APP_BASE_API` 覆盖。

## 目录结构

```
medical-frontend/
├── src/
│   ├── api/               # 接口封装
│   ├── layouts/           # 通用布局
│   ├── router/            # 路由配置
│   ├── store/             # Pinia 状态
│   ├── styles/            # 全局样式
│   ├── views/             # 业务页面
│   └── main.ts            # 应用入口
├── types/                 # 自动生成的类型声明
├── vite.config.ts         # Vite 配置
├── tsconfig*.json         # TypeScript 配置
└── package.json
```

## 后续开发建议

- 衔接后端接口时，将 `src/views` 中的示例数据替换为真实请求。
- 在 `src/api/http.ts` 里补充异常码处理、刷新 Token 等逻辑。
- 根据角色（患者/医生/管理员）扩展路由与菜单。
- 可引入 ECharts 等图表库进行更丰富的数据可视化。

