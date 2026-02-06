#!/bin/bash
# ============================================================
# 智慧医疗健康管理系统 — 服务器一键初始化脚本
# 适用于：Ubuntu 22.04 / 24.04 LTS 全新服务器
# 用法：  bash server-init.sh
# ============================================================

set -e

echo "============================================"
echo "🏥 智慧医疗系统 — 服务器初始化"
echo "============================================"

# ---- 颜色输出 ----
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}✅ $1${NC}"; }
warn()  { echo -e "${YELLOW}⚠️  $1${NC}"; }
error() { echo -e "${RED}❌ $1${NC}"; }

# ============================================
# 1. 系统更新
# ============================================
echo ""
echo "📦 [1/7] 更新系统包..."
apt update && apt upgrade -y
info "系统更新完成"

# ============================================
# 2. 安装 JDK 17
# ============================================
echo ""
echo "☕ [2/7] 安装 JDK 17..."
if java -version 2>&1 | grep -q "17"; then
    info "JDK 17 已安装，跳过"
else
    apt install -y openjdk-17-jdk
    info "JDK 17 安装完成: $(java -version 2>&1 | head -1)"
fi

# ============================================
# 3. 安装 MySQL 8.0
# ============================================
echo ""
echo "🐬 [3/7] 安装 MySQL 8.0..."
if command -v mysql &> /dev/null; then
    info "MySQL 已安装，跳过"
else
    apt install -y mysql-server
    systemctl enable mysql
    systemctl start mysql
    info "MySQL 安装完成: $(mysql --version)"
    
    echo ""
    warn "请手动设置 MySQL root 密码："
    echo "  sudo mysql"
    echo "  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的密码';"
    echo "  FLUSH PRIVILEGES;"
    echo "  EXIT;"
fi

# ============================================
# 4. 安装 Redis
# ============================================
echo ""
echo "🔴 [4/7] 安装 Redis..."
if command -v redis-server &> /dev/null; then
    info "Redis 已安装，跳过"
else
    apt install -y redis-server
    systemctl enable redis-server
    systemctl start redis-server
    info "Redis 安装完成: $(redis-server --version)"
fi

# ============================================
# 5. 安装 Nginx
# ============================================
echo ""
echo "🌐 [5/7] 安装 Nginx..."
if command -v nginx &> /dev/null; then
    info "Nginx 已安装，跳过"
else
    apt install -y nginx
    systemctl enable nginx
    systemctl start nginx
    info "Nginx 安装完成: $(nginx -v 2>&1)"
fi

# ============================================
# 6. 创建项目目录结构
# ============================================
echo ""
echo "📁 [6/7] 创建项目目录..."

mkdir -p /www/wwwroot/medical-backend/backup
mkdir -p /www/wwwroot/medical-frontend/dist

info "目录结构已创建："
echo "  /www/wwwroot/medical-backend/       ← 后端 JAR"
echo "  /www/wwwroot/medical-backend/backup/ ← JAR 备份"
echo "  /www/wwwroot/medical-frontend/dist/  ← 前端静态文件"

# ============================================
# 7. 创建 Systemd 服务
# ============================================
echo ""
echo "⚙️  [7/7] 配置 Systemd 服务..."

cat > /etc/systemd/system/medical.service << 'EOF'
[Unit]
Description=Medical Health System Backend
After=network.target mysql.service redis-server.service

[Service]
User=root
WorkingDirectory=/www/wwwroot/medical-backend
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -XX:+UseG1GC -jar medical-admin.jar
SuccessExitStatus=143
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

# 环境变量（部署后按需修改）
# Environment=SPRING_DATASOURCE_PASSWORD=你的数据库密码
# Environment=DEEPSEEK_API_KEY=你的API密钥

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
info "Systemd 服务 medical.service 已创建"

# ============================================
# 8. 配置 Nginx 站点
# ============================================
echo ""
echo "🌐 配置 Nginx..."

cat > /etc/nginx/sites-available/medical << 'EOF'
server {
    listen 80;
    server_name _;

    root /www/wwwroot/medical-frontend/dist;
    index index.html;

    # 前端路由 — SPA history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
    }

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml;
    gzip_min_length 1024;
}
EOF

# 启用站点
ln -sf /etc/nginx/sites-available/medical /etc/nginx/sites-enabled/medical
rm -f /etc/nginx/sites-enabled/default

nginx -t && systemctl reload nginx
info "Nginx 站点配置完成"

# ============================================
# 完成
# ============================================
echo ""
echo "============================================"
echo -e "${GREEN}🎉 服务器初始化完成！${NC}"
echo "============================================"
echo ""
echo "📋 后续操作："
echo "  1. 设置 MySQL root 密码（如上方提示）"
echo "  2. 导入数据库：mysql -u root -p < medical_health.sql"
echo "  3. 修改 Systemd 环境变量：sudo vim /etc/systemd/system/medical.service"
echo "     设置 SPRING_DATASOURCE_PASSWORD 和 DEEPSEEK_API_KEY"
echo "  4. 配置 HTTPS：sudo apt install certbot python3-certbot-nginx -y && sudo certbot --nginx"
echo "  5. 推送代码到 GitHub main 分支，CI/CD 会自动部署"
echo ""
