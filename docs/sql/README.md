# 数据库 SQL 脚本说明

本说明文档介绍本项目数据库脚本的内容与使用方法，涵盖初始化、导入/导出、备份恢复与常见问题。

## 📁 文件说明

### 1. medical_health.sql
完整的数据库初始化脚本，包含：
- 12 张核心数据表的建表语句
- 必要的索引与外键约束
- 初始化示例数据
- 数据库视图
- 存储过程
- 触发器

## 🚀 快速开始

### 方式一：命令行导入

#### Windows（PowerShell/CMD）
```bash
#（可选）进入 MySQL 安装目录的 bin 目录
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

# 先创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS medical_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入 SQL（将路径替换为你本机的工程路径）
mysql -u root -p medical_health < D:\code\medical-health-system\docs\sql\medical_health.sql
```

#### Linux / macOS
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS medical_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入 SQL（相对路径示例）
mysql -u root -p medical_health < docs/sql/medical_health.sql
```

### 方式二：使用图形化客户端

#### Navicat
1. 连接到 MySQL 服务器
2. 新建数据库 `medical_health`
3. 右键数据库 → 运行 SQL 文件
4. 选择 `medical_health.sql` 并执行

#### MySQL Workbench
1. 连接到 MySQL 服务器
2. File → Run SQL Script
3. 选择 `medical_health.sql` 执行

#### phpMyAdmin
1. 创建数据库 `medical_health`
2. 选择该数据库
3. 点击“导入”
4. 选择 `medical_health.sql` 并执行

### 方式三：使用 Docker

```bash
# 启动 MySQL 容器（示例）
docker run --name medical-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=medical_health \
  -p 3306:3306 -d mysql:8.0

# 等待 MySQL 启动完成（约 10~20 秒）
sleep 15

# 导入 SQL（在项目根目录执行）
docker exec -i medical-mysql \
  mysql -uroot -proot123 medical_health < docs/sql/medical_health.sql
```

## 📊 数据库结构（概览）

### 核心表（12 张）

| 序号 | 表名 | 说明 |
|-----|------|------|
| 1 | sys_user | 系统用户 |
| 2 | patient_info | 患者信息 |
| 3 | doctor_info | 医生信息 |
| 4 | health_data | 健康体征数据（血压/血糖/心率/体温等） |
| 5 | medical_report | 医疗/体检报告（含 JSON 字段） |
| 6 | ai_consultation | AI 问诊记录 |
| 7 | appointment | 预约记录 |
| 8 | doctor_schedule | 医生排班 |
| 9 | medication_reminder | 用药提醒计划 |
| 10 | medication_record | 用药执行记录 |
| 11 | system_notification | 系统通知 |
| 12 | health_knowledge | 健康知识库 |

（初始化脚本包含少量示例数据，用于本地演示与联调）

### 视图（2 个）

| 视图名 | 说明 |
|--------|------|
| v_patient_health_overview | 患者健康数据概览 |
| v_doctor_statistics | 医生工作统计 |

### 存储过程（2 个）

| 存储过程名 | 说明 |
|-----------|------|
| sp_update_patient_age | 自动计算并更新患者年龄 |
| sp_generate_medication_records | 根据提醒计划生成用药记录 |

### 触发器（3 个）

| 触发器名 | 说明 |
|---------|------|
| tr_appointment_insert | 预约成功后更新排班可用数 |
| tr_appointment_update | 取消预约后回收排班可用数 |
| tr_patient_age_update | 患者生日变更时更新年龄 |

## 👥 测试账号（示例）

为便于本地演示，建议使用以下账号（也可参考根 README 中的默认账号）：

```
管理员：admin / admin123
医生：doctor / doctor123
患者：patient / patient123
```

账号口令如与你的环境不一致，请以实际导入数据为准。

## 💾 备份与恢复

### 备份
```bash
# 备份结构和数据
mysqldump -u root -p medical_health > backup_$(date +%Y%m%d).sql

# 仅备份结构
mysqldump -u root -p --no-data medical_health > schema_only.sql

# 仅备份数据
mysqldump -u root -p --no-create-info medical_health > data_only.sql

# 备份指定表
mysqldump -u root -p medical_health sys_user patient_info > users_backup.sql
```

### 恢复
```bash
# 恢复整个数据库
mysql -u root -p medical_health < backup_20251106.sql

# 恢复指定表（使用之前的备份文件）
mysql -u root -p medical_health < users_backup.sql
```

## ⚠️ 注意事项

1. 字符集设置
   - 数据库字符集：`utf8mb4`
   - 排序规则：`utf8mb4_unicode_ci`
   - 确保 MySQL 配置文件（my.cnf/my.ini）与客户端均使用 `utf8mb4`

2. 外键约束
   - 存在级联删除/更新（CASCADE）的表关系
   - 导入顺序不当可能出现外键错误，可临时禁用外键检查再恢复

3. JSON 字段
   - `medical_report.report_data` 为 JSON 类型
   - 需 MySQL 5.7.8 及以上版本

4. 触发器
   - 导入后自动生效；如不需要，可在导入后手工删除

5. 密码加密
   - 示例密码为 BCrypt 哈希
   - 实际项目请使用 Spring Security 的 `BCryptPasswordEncoder` 生成

6. 时区设置
   - 确保 MySQL 时区与应用服务器时区一致
   - JDBC URL 建议包含 `serverTimezone=Asia/Shanghai`

## 🐛 常见问题

### 1. 导入出现字符集错误
```sql
-- 设置客户端字符集
SET NAMES utf8mb4;
```

### 2. 外键约束失败
```sql
-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;
-- 导入数据
-- ...
-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
```

### 3. 导入提示数据库不存在
```sql
-- 先创建数据库
CREATE DATABASE IF NOT EXISTS medical_health 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;
```

### 4. 权限不足
```sql
-- MySQL 8.0 及以上
CREATE USER 'your_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON medical_health.* TO 'your_user'@'%';
FLUSH PRIVILEGES;
```

### 5. 存储过程/触发器无法创建
```
可能原因：客户端不支持 DELIMITER、或脚本中断。
建议：使用 MySQL CLI 或支持 DELIMITER 的 GUI 客户端执行。
```

## 📈 性能与维护建议

1. 索引优化
   - 已为常用查询字段创建索引
   - 根据实际查询情况增补复合索引，避免过多低选择性索引

2. 分区表（可选）
   - 对于数据量较大的表（如 `health_data`）可考虑按时间分区
   ```sql
   -- 按年份分区示例
   ALTER TABLE health_data 
   PARTITION BY RANGE (YEAR(measure_time)) (
     PARTITION p2023 VALUES LESS THAN (2024),
     PARTITION p2024 VALUES LESS THAN (2025),
     PARTITION p2025 VALUES LESS THAN (2026)
   );
   ```

3. 定期维护
   ```sql
   -- 分析表
   ANALYZE TABLE sys_user;
   -- 优化表
   OPTIMIZE TABLE sys_user;
   -- 检查表
   CHECK TABLE sys_user;
   ```

## 📞 参考与支持

如有问题，请参考：
- MySQL 官方文档：https://dev.mysql.com/doc/
- 项目文档索引：../README.md
- 数据库设计文档：../DATABASE.md

---

最后更新：2025-11-14

