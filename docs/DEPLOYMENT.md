# 部署文档

本指南介绍环境准备、后端/前端部署、容器化与运维要点。建议先在测试环境完成部署演练，再上线生产。

最后更新时间：2025-11-14

## 环境要求

### 服务器建议
- CPU：2 核及以上
- 内存：4 GB 及以上（生产建议 ≥ 8 GB）
- 磁盘：50 GB 及以上
- 操作系统：Linux（CentOS 7+/Ubuntu 18.04+）或 Windows Server

### 软件环境
- JDK 17（推荐）或兼容版本
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nginx 1.18+
- Node.js 16+（前端构建使用）

## 部署步骤

### 1. 数据库

#### 1.1 安装 MySQL（示例）
```bash
# CentOS
yum install -y mysql-server

# Ubuntu
apt-get update && apt-get install -y mysql-server
```

#### 1.2 创建数据库与用户
```sql
-- 登录 MySQL 后执行
CREATE DATABASE IF NOT EXISTS medical_health
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER 'medical'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON medical_health.* TO 'medical'@'%';
FLUSH PRIVILEGES;
```

#### 1.3 导入初始化脚本
```bash
mysql -u medical -p medical_health < docs/sql/medical_health.sql
```

### 2. Redis
```bash
# 安装与启动（Linux 示例）
yum install -y redis || apt-get install -y redis
systemctl enable redis
systemctl start redis

# 设置密码（按需）
redis-cli
CONFIG SET requirepass "your_redis_password"
CONFIG REWRITE
```

### 3. 后端服务

#### 3.1 打包
```bash
cd medical-backend
mvn clean package -DskipTests
# 产物位于 medical-admin/target/medical-admin.jar
```

#### 3.2 配置
编辑（或通过环境变量注入）`application-prod.yml` 关键项示例：
```yaml
spring:
  datasource:
    url: jdbc:mysql://your-server:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: medical
    password: your_db_password
  redis:
    host: your-server
    port: 6379
    password: your_redis_password

jwt:
  secret: please-change-in-prod
  expiration: 86400

deepseek:
  api-key: your_deepseek_api_key
```

（推荐用环境变量覆盖敏感信息，避免提交仓库）

#### 3.3 启动
```bash
# 方式一：前台
java -jar medical-admin/target/medical-admin.jar --spring.profiles.active=prod

# 方式二：后台
nohup java -jar medical-admin/target/medical-admin.jar --spring.profiles.active=prod > app.log 2>&1 &
```

#### 3.4 systemd 管理（可选）
`/etc/systemd/system/medical-backend.service`：
```ini
[Unit]
Description=Medical Health Backend Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/medical-backend
ExecStart=/usr/bin/java -jar /opt/medical-backend/medical-admin.jar --spring.profiles.active=prod
Restart=on-failure

[Install]
WantedBy=multi-user.target
```
执行：
```bash
systemctl daemon-reload
systemctl enable medical-backend
systemctl start medical-backend
```

### 4. 前端

#### 4.1 构建
```bash
cd medical-frontend
npm ci || npm install
npm run build
```
构建产物默认在 `dist/`，可由 Nginx 托管。

#### 4.2 Nginx 示例
`/etc/nginx/conf.d/medical-frontend.conf`：
```nginx
server {
    listen 80;
    server_name _;

    root /opt/medical-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理（如前后端不同域）
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```
重载：
```bash
nginx -t && systemctl reload nginx
```

### 5. 容器化（可选）

`docker-compose.yml`（示例）：
```yaml
version: "3.9"
services:
  mysql:
    image: mysql:8.0
    container_name: medical-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: medical_health
    ports: ["3306:3306"]
    volumes:
      - mysql-data:/var/lib/mysql
    networks: [medical-network]

  redis:
    image: redis:6
    container_name: medical-redis
    ports: ["6379:6379"]
    command: ["redis-server", "--appendonly", "yes"]
    networks: [medical-network]

  backend:
    build: ./medical-backend
    container_name: medical-backend
    depends_on: [mysql, redis]
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://mysql:3306/medical_health?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
      DB_USERNAME: root
      DB_PASSWORD: root123
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD:
      JWT_SECRET: please-change-in-prod
    ports: ["8080:8080"]
    networks: [medical-network]

  frontend:
    build: ./medical-frontend
    container_name: medical-frontend
    depends_on: [backend]
    ports: ["80:80"]
    networks: [medical-network]

volumes:
  mysql-data:

networks:
  medical-network:
    driver: bridge
```
启动：
```bash
docker-compose up -d
```

## 监控与维护

### 日志
```bash
# 后台运行日志
tail -f app.log
# 搜索错误
grep "ERROR" app.log
```

### Spring Boot Actuator（建议）
`application.yml`：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always
```
访问：
- 健康检查：http://your-server:8080/actuator/health
- 指标：http://your-server:8080/actuator/metrics

### 数据库备份（示例）
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
mkdir -p "$BACKUP_DIR"
mysqldump -u medical -p'your_db_password' medical_health > "$BACKUP_DIR/medical_health_$DATE.sql"
find "$BACKUP_DIR" -name "*.sql" -mtime +7 -delete
```
使用 `crontab -e`：
```
0 2 * * * /path/to/backup.sh
```

## 常见问题

1) 端口占用  
```bash
netstat -tunlp | grep 8080   # Linux
```

2) 内存不足  
```bash
java -Xms512m -Xmx1024m -jar medical-admin.jar
```

3) 数据库连接失败  
- 确认数据库/用户名/密码是否正确
- 检查防火墙与安全组
- 校验 JDBC URL 与时区参数

