# 数据库设计文档

## 数据库概述

- 数据库名称: `medical_health`
- 数据库引擎: InnoDB
- 字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci

## 数据表设计

### 1. 系统用户表 (sys_user)

用户基础信息表，存储所有用户的登录信息。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 用户ID |
| username | VARCHAR | 50 | NO | UK | 用户名 |
| password | VARCHAR | 100 | NO | - | 密码(加密) |
| nickname | VARCHAR | 50 | YES | - | 昵称 |
| phone | VARCHAR | 20 | YES | UK | 手机号 |
| email | VARCHAR | 100 | YES | - | 邮箱 |
| avatar | VARCHAR | 255 | YES | - | 头像URL |
| role | VARCHAR | 20 | NO | - | 角色(ADMIN/DOCTOR/PATIENT) |
| status | TINYINT | - | NO | - | 状态(0:禁用 1:正常) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |
| last_login_time | DATETIME | - | YES | - | 最后登录时间 |
| deleted | TINYINT | - | NO | - | 逻辑删除(0:否 1:是) |

**索引:**
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_username` (`username`)
- UNIQUE KEY `uk_phone` (`phone`)
- KEY `idx_role` (`role`)

---

### 2. 患者信息表 (patient_info)

存储患者详细信息。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 患者ID |
| user_id | BIGINT | - | NO | UK | 用户ID(外键) |
| real_name | VARCHAR | 50 | NO | - | 真实姓名 |
| gender | TINYINT | - | NO | - | 性别(0:女 1:男) |
| birthday | DATE | - | YES | - | 出生日期 |
| age | INT | - | YES | - | 年龄 |
| id_card | VARCHAR | 18 | YES | UK | 身份证号 |
| address | VARCHAR | 200 | YES | - | 详细地址 |
| emergency_contact | VARCHAR | 50 | YES | - | 紧急联系人 |
| emergency_phone | VARCHAR | 20 | YES | - | 紧急联系电话 |
| height | DECIMAL | 5,2 | YES | - | 身高(cm) |
| weight | DECIMAL | 5,2 | YES | - | 体重(kg) |
| blood_type | VARCHAR | 10 | YES | - | 血型(A/B/AB/O) |
| allergies | TEXT | - | YES | - | 过敏史 |
| family_history | TEXT | - | YES | - | 家族病史 |
| medical_history | TEXT | - | YES | - | 既往病史 |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |

**索引:**
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_user_id` (`user_id`)
- UNIQUE KEY `uk_id_card` (`id_card`)

---

### 3. 医生信息表 (doctor_info)

存储医生详细信息。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 医生ID |
| user_id | BIGINT | - | NO | UK | 用户ID(外键) |
| real_name | VARCHAR | 50 | NO | - | 真实姓名 |
| gender | TINYINT | - | NO | - | 性别(0:女 1:男) |
| department | VARCHAR | 50 | NO | - | 科室 |
| title | VARCHAR | 50 | YES | - | 职称 |
| specialty | VARCHAR | 200 | YES | - | 专长 |
| introduction | TEXT | - | YES | - | 个人简介 |
| certificate_no | VARCHAR | 50 | YES | - | 执业证书号 |
| work_years | INT | - | YES | - | 从业年限 |
| consultation_fee | DECIMAL | 10,2 | YES | - | 挂号费(元) |
| rating | DECIMAL | 3,2 | YES | - | 评分(0-5) |
| patient_count | INT | - | YES | - | 接诊患者数 |
| status | TINYINT | - | NO | - | 状态(0:停诊 1:正常) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |

**索引:**
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_user_id` (`user_id`)
- KEY `idx_department` (`department`)
- KEY `idx_title` (`title`)

---

### 4. 健康数据表 (health_data)

存储患者日常健康监测数据。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 数据ID |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| data_type | VARCHAR | 20 | NO | - | 数据类型(BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE/TEMPERATURE/WEIGHT) |
| systolic_pressure | INT | - | YES | - | 收缩压(mmHg) |
| diastolic_pressure | INT | - | YES | - | 舒张压(mmHg) |
| blood_sugar | DECIMAL | 5,2 | YES | - | 血糖值(mmol/L) |
| heart_rate | INT | - | YES | - | 心率(次/分) |
| temperature | DECIMAL | 4,2 | YES | - | 体温(℃) |
| weight | DECIMAL | 5,2 | YES | - | 体重(kg) |
| remark | VARCHAR | 200 | YES | - | 备注 |
| measure_time | DATETIME | - | NO | - | 测量时间 |
| status | TINYINT | - | NO | - | 状态(0:正常 1:偏低 2:偏高) |
| create_time | DATETIME | - | NO | - | 创建时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_data_type` (`data_type`)
- KEY `idx_measure_time` (`measure_time`)

---

### 5. 医疗报告表 (medical_report)

存储患者的各类医疗报告。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 报告ID |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| report_type | VARCHAR | 50 | NO | - | 报告类型(体检报告/检验报告/影像报告) |
| report_name | VARCHAR | 100 | NO | - | 报告名称 |
| hospital | VARCHAR | 100 | YES | - | 医院名称 |
| report_data | JSON | - | YES | - | 报告数据(JSON格式) |
| file_url | VARCHAR | 255 | YES | - | 文件URL |
| ai_analysis | TEXT | - | YES | - | AI分析结果 |
| ai_suggestions | TEXT | - | YES | - | AI建议 |
| report_date | DATE | - | YES | - | 报告日期 |
| upload_time | DATETIME | - | NO | - | 上传时间 |
| create_time | DATETIME | - | NO | - | 创建时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_report_type` (`report_type`)
- KEY `idx_report_date` (`report_date`)

---

### 6. AI问诊记录表 (ai_consultation)

存储AI智能问诊的对话记录。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 记录ID |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| session_id | VARCHAR | 50 | NO | - | 会话ID |
| question | TEXT | - | NO | - | 用户提问 |
| ai_response | TEXT | - | NO | - | AI回复 |
| consultation_type | VARCHAR | 20 | NO | - | 问诊类型(症状分析/用药咨询/健康知识) |
| tokens_used | INT | - | YES | - | 消耗token数 |
| response_time | INT | - | YES | - | 响应时间(ms) |
| feedback | TINYINT | - | YES | - | 反馈(1:有帮助 0:无帮助) |
| feedback_content | VARCHAR | 200 | YES | - | 反馈内容 |
| create_time | DATETIME | - | NO | - | 创建时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_session_id` (`session_id`)
- KEY `idx_consultation_type` (`consultation_type`)
- KEY `idx_create_time` (`create_time`)

---

### 7. 预约记录表 (appointment)

存储患者预约挂号信息。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 预约ID |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| doctor_id | BIGINT | - | NO | - | 医生ID(外键) |
| appointment_date | DATE | - | NO | - | 预约日期 |
| appointment_time | VARCHAR | 20 | NO | - | 预约时段 |
| department | VARCHAR | 50 | NO | - | 就诊科室 |
| symptoms | TEXT | - | YES | - | 症状描述 |
| status | TINYINT | - | NO | - | 状态(0:待就诊 1:已就诊 2:已取消) |
| cancel_reason | VARCHAR | 200 | YES | - | 取消原因 |
| diagnosis | TEXT | - | YES | - | 诊断结果 |
| prescription | TEXT | - | YES | - | 处方 |
| fee | DECIMAL | 10,2 | YES | - | 费用 |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_doctor_id` (`doctor_id`)
- KEY `idx_appointment_date` (`appointment_date`)
- KEY `idx_status` (`status`)

---

### 8. 医生排班表 (doctor_schedule)

医生排班信息。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 排班ID |
| doctor_id | BIGINT | - | NO | - | 医生ID(外键) |
| schedule_date | DATE | - | NO | - | 排班日期 |
| time_period | VARCHAR | 20 | NO | - | 时段(上午/下午) |
| max_patients | INT | - | NO | - | 最大接诊数 |
| booked_patients | INT | - | NO | - | 已预约数 |
| status | TINYINT | - | NO | - | 状态(0:停诊 1:可预约) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_doctor_id` (`doctor_id`)
- KEY `idx_schedule_date` (`schedule_date`)
- UNIQUE KEY `uk_doctor_date_period` (`doctor_id`, `schedule_date`, `time_period`)

---

### 9. 用药提醒表 (medication_reminder)

患者用药提醒计划。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 提醒ID |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| medicine_name | VARCHAR | 100 | NO | - | 药品名称 |
| dosage | VARCHAR | 50 | NO | - | 剂量 |
| frequency | VARCHAR | 50 | NO | - | 频率(每日3次/每日2次) |
| usage_method | VARCHAR | 100 | YES | - | 用法(饭前/饭后) |
| start_date | DATE | - | NO | - | 开始日期 |
| end_date | DATE | - | NO | - | 结束日期 |
| reminder_times | VARCHAR | 100 | NO | - | 提醒时间(08:00,12:00,18:00) |
| remark | VARCHAR | 200 | YES | - | 备注 |
| status | TINYINT | - | NO | - | 状态(0:已停用 1:进行中 2:已完成) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_status` (`status`)
- KEY `idx_date_range` (`start_date`, `end_date`)

---

### 10. 用药记录表 (medication_record)

患者实际用药记录。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 记录ID |
| reminder_id | BIGINT | - | NO | - | 提醒ID(外键) |
| patient_id | BIGINT | - | NO | - | 患者ID(外键) |
| medicine_name | VARCHAR | 100 | NO | - | 药品名称 |
| planned_time | DATETIME | - | NO | - | 计划用药时间 |
| actual_time | DATETIME | - | YES | - | 实际用药时间 |
| status | TINYINT | - | NO | - | 状态(0:未服用 1:已服用 2:已忽略) |
| remark | VARCHAR | 200 | YES | - | 备注 |
| create_time | DATETIME | - | NO | - | 创建时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_reminder_id` (`reminder_id`)
- KEY `idx_patient_id` (`patient_id`)
- KEY `idx_planned_time` (`planned_time`)
- KEY `idx_status` (`status`)

---

### 11. 系统通知表 (system_notification)

系统消息通知。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 通知ID |
| user_id | BIGINT | - | NO | - | 用户ID(外键) |
| title | VARCHAR | 100 | NO | - | 通知标题 |
| content | TEXT | - | NO | - | 通知内容 |
| type | VARCHAR | 20 | NO | - | 类型(系统/预约/用药/报告) |
| link_url | VARCHAR | 255 | YES | - | 关联链接 |
| is_read | TINYINT | - | NO | - | 是否已读(0:未读 1:已读) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| read_time | DATETIME | - | YES | - | 阅读时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_user_id` (`user_id`)
- KEY `idx_is_read` (`is_read`)
- KEY `idx_create_time` (`create_time`)

---

### 12. 健康知识库表 (health_knowledge)

健康知识文章。

| 字段名 | 类型 | 长度 | 允许空 | 主键 | 说明 |
|--------|------|------|--------|------|------|
| id | BIGINT | - | NO | PK | 知识ID |
| title | VARCHAR | 200 | NO | - | 标题 |
| category | VARCHAR | 50 | NO | - | 分类 |
| tags | VARCHAR | 200 | YES | - | 标签 |
| cover_image | VARCHAR | 255 | YES | - | 封面图 |
| summary | VARCHAR | 500 | YES | - | 摘要 |
| content | LONGTEXT | - | NO | - | 内容 |
| author | VARCHAR | 50 | YES | - | 作者 |
| source | VARCHAR | 100 | YES | - | 来源 |
| view_count | INT | - | YES | - | 浏览量 |
| like_count | INT | - | YES | - | 点赞数 |
| status | TINYINT | - | NO | - | 状态(0:草稿 1:已发布) |
| create_time | DATETIME | - | NO | - | 创建时间 |
| update_time | DATETIME | - | NO | - | 更新时间 |
| publish_time | DATETIME | - | YES | - | 发布时间 |

**索引:**
- PRIMARY KEY (`id`)
- KEY `idx_category` (`category`)
- KEY `idx_status` (`status`)
- KEY `idx_publish_time` (`publish_time`)

---

## ER图说明

### 主要关系

1. **sys_user → patient_info**: 一对一关系
2. **sys_user → doctor_info**: 一对一关系
3. **patient_info → health_data**: 一对多关系
4. **patient_info → medical_report**: 一对多关系
5. **patient_info → ai_consultation**: 一对多关系
6. **patient_info → appointment**: 一对多关系
7. **doctor_info → appointment**: 一对多关系
8. **doctor_info → doctor_schedule**: 一对多关系
9. **patient_info → medication_reminder**: 一对多关系
10. **medication_reminder → medication_record**: 一对多关系

## 数据字典补充说明

### 角色类型 (role)
- `ADMIN`: 管理员
- `DOCTOR`: 医生
- `PATIENT`: 患者

### 健康数据类型 (data_type)
- `BLOOD_PRESSURE`: 血压
- `BLOOD_SUGAR`: 血糖
- `HEART_RATE`: 心率
- `TEMPERATURE`: 体温
- `WEIGHT`: 体重

### 问诊类型 (consultation_type)
- `SYMPTOM_ANALYSIS`: 症状分析
- `MEDICATION_GUIDE`: 用药咨询
- `HEALTH_KNOWLEDGE`: 健康知识

### 预约状态 (appointment.status)
- `0`: 待就诊
- `1`: 已就诊
- `2`: 已取消

### 通知类型 (notification.type)
- `SYSTEM`: 系统通知
- `APPOINTMENT`: 预约通知
- `MEDICATION`: 用药提醒
- `REPORT`: 报告通知

---

**最后更新时间**: 2025-11-06

