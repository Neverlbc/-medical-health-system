# 部署文档

本指南介绍环境准备、后端/前端部署、容器化与运维要点。

最后更新时间：2026-01-27

## 🌍 全球部署实践 (US Overseas Node)

本项目已在阿里云美国节点（Ubuntu 22.04）完成生产级部署，以下为核心配置指南。

### 1. 服务器环境
- **机型**：阿里云 ECS (US Silicon Valley)
- **操作系统**：Ubuntu 22.04 LTS
- **开放端口**：80 (HTTP), 443 (HTTPS), 3306 (MySQL), 8080 (Backend)

### 2. 后端部署 (Spring Boot)

#### 2.1 Systemd 服务配置
创建 `/etc/systemd/system/medical.service`：
```ini
[Unit]
Description=Medical Health System Backend
After=network.target mysql.service redis.service

[Service]
User=root
# 关键：指定 jar 包所在目录
WorkingDirectory=/www/wwwroot/medical-backend
# 运行命令，通过命令行参数注入敏感信息
ExecStart=/usr/bin/java -jar medical-admin.jar --spring.datasource.password=您的数据库密码
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 2.2 管理命令
```bash
sudo systemctl daemon-reload
sudo systemctl enable medical
sudo systemctl start medical
sudo systemctl status medical
```

### 3. 前端部署 (Vue 3 + Nginx)

#### 3.1 Nginx 核心配置
文件路径：`/etc/nginx/sites-available/default`
```nginx
server {
    listen 80;
    server_name lbc-ai.top medical.lbc-ai.top;

    # 这里的 root 必须指向包含 index.html 的 dist 文件夹
    root /www/wwwroot/medical-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 4. HTTPS 安全加固 (Certbot)

使用 Let's Encrypt 自动签发证书：
```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx
```
- 选择 **Redirect** 选项，强制将所有 HTTP 流量重定向到 HTTPS。
- 证书会自动配置到上述 Nginx 文件中。

### 5. 数据库远程访问 (Navicat)

为了安全地进行远程调试，需修改 MySQL 配置：
1. 编辑 `/etc/mysql/mysql.conf.d/mysqld.cnf`，将 `bind-address` 改为 `0.0.0.0`。
2. 在 MySQL 中为 root 用户开启远程权限：
   ```sql
   CREATE USER 'root'@'%' IDENTIFIED BY '您的密码';
   GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
   FLUSH PRIVILEGES;
   ```
3. 开放服务器防火墙：`sudo ufw allow 3306`（或在阿里云安全组中放行）。

## 监控与维护

### 日志查看
- **后端日志**：`sudo journalctl -u medical.service -f`
- **Nginx 访问日志**：`tail -f /var/log/nginx/access.log`
- **Nginx 错误日志**：`tail -f /var/log/nginx/error.log`

### 自动续期
Certbot 默认已配置 crontab 任务，可运行以下命令测试续期逻辑：
```bash
sudo certbot renew --dry-run
```

