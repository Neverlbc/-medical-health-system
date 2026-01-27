# API 接口文档

本文件描述后端 HTTP 与 WebSocket 接口的使用方式与统一规范。文档随版本演进更新，未上线的接口会标注“计划中”或“暂未实现”。

最后更新时间：2025-11-14

## 基础信息
- 基础路径：`http://localhost:8080/api/v1`
- 请求格式：`application/json; charset=UTF-8`
- 响应格式：`application/json`
- 字符编码：`UTF-8`

## 认证方式
采用 JWT 认证，除公开接口外，其余请求 Header 需携带：
```
Authorization: Bearer <token>
```

## 统一响应格式

成功
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1699276800000
}
```

失败
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "timestamp": 1699276800000
}
```

状态码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 参数错误或业务校验失败 |
| 401 | 未授权，需要登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 认证模块（Auth）

说明：已实现。控制器路径：`/api/v1/auth`

### 1.1 用户注册（公开）
- URL：`POST /auth/register`
- 是否需要认证：否
- 请求
```json
{
  "username": "string",       // 必填，4-20 位
  "password": "string",       // 必填，6-20 位
  "phone": "string",          // 必填，中国大陆手机号
  "email": "string",          // 选填
  "role": "PATIENT|DOCTOR"    // 必填
}
```
- 响应（仅返回结果，不携带 token）
```json
{
  "code": 200,
  "message": "register ok",
  "data": null,
  "timestamp": 1699276800000
}
```

### 1.2 用户登录（公开）
- URL：`POST /auth/login`
- 是否需要认证：否
- 请求
```json
{
  "username": "string",
  "password": "string"
}
```
- 响应
```json
{
  "code": 200,
  "message": "login ok",
  "data": {
    "userId": 1001,
    "username": "zhangsan",
    "nickname": "张三",
    "role": "PATIENT",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400
  },
  "timestamp": 1699276800000
}
```

### 1.3 退出登录
- URL：`POST /auth/logout`
- 是否需要认证：是
- 说明：JWT 为无状态实现，此接口为显式登出（日志/黑名单可选）。
- 响应
```json
{ "code": 200, "message": "logout ok", "data": null, "timestamp": 1699276800000 }
```

### 1.4 刷新 Token（预留，暂未实现）
- URL：`POST /auth/refresh`
- 是否需要认证：是
- 说明：后续可按需要提供滑动续期能力。

---

## 2. 健康档案（Record）【已实现｜Iteration 01 ✅】

说明：已实现。控制器路径：`/api/v1/record`

### 2.1 创建档案
- URL：`POST /api/v1/record`
- 是否需要认证：是
- 权限要求：
  - 患者：自动绑定到当前用户，`userId` 字段会被覆盖
  - 医生/管理员：必须指定 `userId` 字段
- 请求体
```json
{
  "userId": 1001,                    // 患者ID（医生/管理员必填，患者自动填充）
  "allergies": "青霉素过敏",         // 过敏史（可选）
  "familyHistory": "父亲有高血压",   // 家族病史（可选）
  "medicalHistory": "无重大疾病史",  // 既往病史（可选）
  "medicationHistory": "长期服用降压药", // 用药史（可选）
  "remark": "定期体检"               // 备注（可选）
}
```
- 响应
```json
{
  "code": 200,
  "message": "ok",
  "data": null,
  "timestamp": 1699276800000
}
```
- 错误情况
  - `400`：医生/管理员未指定 `userId`
  - `401`：未登录
  - `403`：权限不足

### 2.2 更新档案
- URL：`PUT /api/v1/record/{id}`
- 是否需要认证：是
- 权限要求：
  - 患者：只能更新自己的档案
  - 医生/管理员：可以更新任意档案
- 路径参数
  - `id`：档案ID（必填）
- 请求体（同创建档案）
```json
{
  "allergies": "青霉素、磺胺类过敏",
  "familyHistory": "父亲有高血压，母亲有糖尿病",
  "medicalHistory": "2023年阑尾炎手术",
  "medicationHistory": "长期服用降压药",
  "remark": "需要定期复查"
}
```
- 响应
```json
{
  "code": 200,
  "message": "ok",
  "data": null,
  "timestamp": 1699276800000
}
```
- 错误情况
  - `400`：档案不存在
  - `403`：患者尝试修改他人档案

### 2.3 获取档案详情
- URL：`GET /api/v1/record/{id}`
- 是否需要认证：是
- 权限要求：
  - 患者：只能查看自己的档案
  - 医生/管理员：可以查看任意档案
- 路径参数
  - `id`：档案ID（必填）
- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1001,
    "allergies": "青霉素过敏",
    "familyHistory": "父亲有高血压",
    "medicalHistory": "无重大疾病史",
    "medicationHistory": "长期服用降压药",
    "remark": "定期体检",
    "createTime": "2025-11-16 22:54:37",
    "updateTime": "2025-11-16 22:54:37",
    "deleted": 0
  },
  "timestamp": 1699276800000
}
```
- 错误情况
  - `404`：档案不存在
  - `403`：患者尝试查看他人档案

### 2.4 分页查询档案列表
- URL：`GET /api/v1/record/list`
- 是否需要认证：是
- 权限要求：
  - 患者：只能查询自己的档案
  - 医生/管理员：可以查询所有档案，支持按患者过滤
- 查询参数
  - `pageNum`：页码（默认 1）
  - `pageSize`：每页数量（默认 10）
  - `keyword`：搜索关键词（可选，搜索备注字段）
  - `userId`：患者ID（可选，医生/管理员可用于过滤）
- 请求示例
```
GET /api/v1/record/list?pageNum=1&pageSize=10&keyword=高血压&userId=1001
```
- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1001,
        "allergies": "青霉素过敏",
        "familyHistory": "父亲有高血压",
        "medicalHistory": "无重大疾病史",
        "medicationHistory": "长期服用降压药",
        "remark": "定期体检",
        "createTime": "2025-11-16 22:54:37",
        "updateTime": "2025-11-16 22:54:37"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1699276800000
}
```

### 2.5 获取档案附件列表
- URL：`GET /api/v1/record/{id}/attachments`
- 是否需要认证：是
- 路径参数
  - `id`：档案ID（必填）
- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "recordId": 1,
      "fileName": "体检报告.pdf",
      "fileUrl": "http://localhost:8080/uploads/report_1699276800.pdf",
      "fileType": "application/pdf",
      "fileSize": 102400,
      "createTime": "2025-11-16 22:54:37"
    }
  ],
  "timestamp": 1699276800000
}
```

### 2.6 添加档案附件
- URL：`POST /api/v1/record/{id}/attachments`
- 是否需要认证：是
- 路径参数
  - `id`：档案ID（必填）
- 请求体
```json
{
  "fileName": "体检报告.pdf",
  "fileUrl": "http://localhost:8080/uploads/report_1699276800.pdf",
  "fileType": "application/pdf",
  "fileSize": 102400
}
```
- 响应
```json
{
  "code": 200,
  "message": "ok",
  "data": null,
  "timestamp": 1699276800000
}
```
- 说明：文件需要先通过 `/api/v1/file/upload` 上传，获取 URL 后再调用此接口关联到档案

### 2.7 删除档案附件
- URL：`DELETE /api/v1/record/attachments/{attId}`
- 是否需要认证：是
- 路径参数
  - `attId`：附件ID（必填）
- 响应
```json
{
  "code": 200,
  "message": "ok",
  "data": null,
  "timestamp": 1699276800000
}
```

---

## 3. 健康监测（Health Data）【已实现✅｜Iteration 02】

说明：已实现。控制器路径：`/api/v1/health-data`

### 3.1 创建健康数据记录
- URL：`POST /api/v1/health-data`
- 是否需要认证：是
- 权限要求：患者角色（自动绑定 patientId）
- 请求体
```json
{
  "dataType": "BLOOD_PRESSURE",           // 数据类型（必填）
  "systolicPressure": 120,                // 收缩压（血压类型必填）
  "diastolicPressure": 80,                // 舒张压（血压类型必填）
  "bloodSugar": 5.5,                      // 血糖值（血糖类型必填）
  "heartRate": 72,                        // 心率（心率类型必填）
  "temperature": 36.5,                    // 体温（体温类型必填）
  "weight": 70.5,                         // 体重（体重类型必填）
  "measureTime": "2025-12-07 08:00:00",  // 测量时间（必填）
  "remark": "晨起测量"                    // 备注（可选）
}
```
- 数据类型枚举
  - `BLOOD_PRESSURE`：血压
  - `BLOOD_SUGAR`：血糖
  - `HEART_RATE`：心率
  - `TEMPERATURE`：体温
  - `WEIGHT`：体重

- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "patientId": 1001,
    "dataType": "BLOOD_PRESSURE",
    "systolicPressure": 120,
    "diastolicPressure": 80,
    "measureTime": "2025-12-07 08:00:00",
    "status": 0,
    "createTime": "2025-12-07 08:05:00"
  },
  "timestamp": 1699276800000
}
```

- 状态自动计算（status 字段）
  - `0`：正常
  - `1`：轻度异常
  - `2`：中度异常
  - `3`：重度异常
  - `4`：危险

### 3.2 分页查询健康数据
- URL：`GET /api/v1/health-data/list`
- 是否需要认证：是
- 查询参数
  - `current`：页码（默认 1）
  - `size`：每页数量（默认 10）
  - `dataType`：数据类型（可选，筛选）
  - `patientId`：患者 ID（医生/管理员可用，可选）

- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "patientId": 1001,
        "dataType": "BLOOD_PRESSURE",
        "systolicPressure": 120,
        "diastolicPressure": 80,
        "measureTime": "2025-12-07 08:00:00",
        "status": 0,
        "remark": "晨起测量",
        "createTime": "2025-12-07 08:05:00"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  },
  "timestamp": 1699276800000
}
```

### 3.3 获取健康数据详情
- URL：`GET /api/v1/health-data/{id}`
- 是否需要认证：是
- 路径参数
  - `id`：数据 ID（必填）
- 权限要求：
  - 患者只能查看自己的数据
  - 医生/管理员可以查看任意数据

### 3.4 删除健康数据
- URL：`DELETE /api/v1/health-data/{id}`
- 是否需要认证：是
- 路径参数
  - `id`：数据 ID（必填）
- 权限要求：
  - 患者只能删除自己的数据
  - 医生/管理员可以删除任意数据

### 3.5 获取健康数据趋势（图表数据）
- URL：`GET /api/v1/health-data/trend`
- 是否需要认证：是
- 查询参数
  - `dataType`：数据类型（必填）
  - `days`：最近天数（默认 30，可选：7/30/90）
  - `patientId`：患者 ID（医生/管理员可用，可选）

- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "dataType": "BLOOD_PRESSURE",
      "systolicPressure": 120,
      "diastolicPressure": 80,
      "measureTime": "2025-12-01 08:00:00",
      "status": 0
    },
    {
      "id": 2,
      "dataType": "BLOOD_PRESSURE",
      "systolicPressure": 135,
      "diastolicPressure": 88,
      "measureTime": "2025-12-02 08:00:00",
      "status": 0
    }
  ],
  "timestamp": 1699276800000
}
```

### 3.6 获取健康数据统计信息
- URL：`GET /api/v1/health-data/statistics`
- 是否需要认证：是
- 查询参数
  - `patientId`：患者 ID（医生/管理员可用，可选）

- 响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 50,        // 总数据量
    "recentCount": 10,       // 最近 7 天数据量
    "abnormalCount": 5       // 异常数据量（status > 0）
  },
  "timestamp": 1699276800000
}
```

### 健康状态判断标准

#### 血压（BLOOD_PRESSURE）
| 收缩压/舒张压 | 状态 | 说明 |
|---------------|------|------|
| < 90/60 | 1 | 低血压 |
| 90-139/60-89 | 0 | 正常 |
| 140-159/90-99 | 2 | 1级高血压（轻度） |
| 160-179/100-109 | 3 | 2级高血压（中度） |
| ≥ 180/110 | 4 | 3级高血压（重度/危险） |

#### 血糖（BLOOD_SUGAR，空腹）
| 血糖值 (mmol/L) | 状态 | 说明 |
|-----------------|------|------|
| < 2.8 | 4 | 严重低血糖（危险） |
| 2.8-3.8 | 1 | 低血糖 |
| 3.9-6.1 | 0 | 正常 |
| 6.2-7.0 | 2 | 糖尿病前期 |
| 7.1-11.1 | 3 | 糖尿病 |
| > 11.1 | 4 | 严重高血糖（危险） |

#### 心率（HEART_RATE）
| 心率 (bpm) | 状态 | 说明 |
|------------|------|------|
| < 40 | 4 | 严重心动过缓（危险） |
| 40-49 | 3 | 中度心动过缓 |
| 50-59 | 1 | 轻度心动过缓 |
| 60-100 | 0 | 正常 |
| 101-120 | 2 | 轻度心动过速 |
| 121-150 | 3 | 中度心动过速 |
| > 150 | 4 | 严重心动过速（危险） |

#### 体温（TEMPERATURE）
| 体温 (℃) | 状态 | 说明 |
|----------|------|------|
| < 35.0 | 4 | 严重低体温（危险） |
| 35.0-36.0 | 1 | 体温偏低 |
| 36.1-37.3 | 0 | 正常 |
| 37.4-38.0 | 1 | 低热 |
| 38.1-39.0 | 2 | 中度发热 |
| 39.1-41.0 | 3 | 高热 |
| > 41.0 | 4 | 超高热（危险） |

#### 体重（WEIGHT，基于 BMI）
| BMI | 状态 | 说明 |
|-----|------|------|
| < 16 | 4 | 严重偏瘦（危险） |
| 16-18.4 | 1 | 偏瘦 |
| 18.5-23.9 | 0 | 正常 |
| 24-27.9 | 2 | 超重 |
| 28-34.9 | 3 | 肥胖 |
| ≥ 35 | 4 | 重度肥胖（危险） |

**注**：BMI = 体重(kg) / 身高²(m²)

---

## 4. AI 智能问诊（AI）【部分已实现｜持续增强】
- URL：`POST /ai/analyze`（建议）
- 请求
```json
{ "symptoms": "连续三天发热 38.3℃，伴随咽痛和咳嗽" }
```
- 响应（文本或结构化建议，具体以实现为准）
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "analysis": "可能为上呼吸道感染，建议多饮水...",
    "riskLevel": "LOW",
    "suggestedDept": "呼吸内科"
  },
  "timestamp": 1699276800000
}
```
- 说明：后端会进行限频、超时与降级处理；Prompt 模板见 `application.yml` 中 DeepSeek 配置。

---


---

## 5. 预约挂号（Appointment）【已实现✅】
- `GET /schedule/list` 排班查询
- `POST /appointment` 创建预约（含冲突校验）
- `POST /appointment/{id}/cancel` 取消预约
- `GET /appointment/my` 我的预约

---


## 6. 通知（Notification）【计划中】
- `GET /notification/list?type=&isRead=&pageNum=&pageSize=`
- `POST /notification/{id}/read`
- `POST /notification/read-all`
- `GET /notification/unread-count`

---

## 7. 健康知识库（Knowledge）【计划中】
- `GET /knowledge/list?category=&keyword=&pageNum=&pageSize=`
- `GET /knowledge/{id}`

---

## 8. 文件上传（File）【基础已存在，按需扩展】
- URL：`POST /file/upload`
- Content-Type：`multipart/form-data`
- 参数
| 名称 | 说明 |
|------|------|
| file | 文件 |
| type | 文件类型（avatar/report/other） |

响应（示例）
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileName": "report_1699276800.pdf",
    "fileUrl": "http://example.com/files/report_1699276800.pdf",
    "fileSize": 102400
  },
  "timestamp": 1699276800000
}
```

---

## WebSocket 接口

连接地址
```
ws://localhost:8080/ws/{userId}
```

消息格式（示例）
```json
{
  "type": "SYSTEM",
  "title": "系统维护",
  "content": "系统将于今晚22:00进行维护",
  "timestamp": 1699276800000
}
```

