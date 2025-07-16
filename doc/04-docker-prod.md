# 백엔드 도커

## 구성

```
/opt/docker/pincoin/backend/build.sh
/opt/docker/pincoin/backend/deploy.sh
/opt/docker/pincoin/backend/full-deploy.sh
/opt/docker/pincoin/backend/dev.sh
/opt/docker/pincoin/backend/docker-compose.yml
/opt/docker/pincoin/backend/.env
/opt/docker/pincoin/backend/nginx/nginx.conf
/opt/docker/pincoin/backend/nginx/site.conf
/opt/docker/pincoin/backend/repo/
/opt/docker/pincoin/backend/repo/Dockerfile.prod
/opt/docker/pincoin/backend/logs/
```

## `.env`

```
PREFIX=pincoin
```

## docker compose

### `docker-compose.yml`

```yaml
services:
  redis:
    container_name: ${PREFIX}-redis
    image: redis:alpine
    restart: unless-stopped
    volumes:
      - redis-data:/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  nginx:
    container_name: ${PREFIX}-backend-nginx
    image: nginx:alpine
    restart: unless-stopped
    ports:
      - "9090:9090"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/site.conf:/etc/nginx/conf.d/site.conf
      - ./logs:/app/logs
    depends_on:
      - backend-1
      - backend-2
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  backend-1:
    container_name: ${PREFIX}-backend-1
    image: ${PREFIX}-backend:latest
    build:
      context: ./repo
      dockerfile: Dockerfile.prod
    restart: unless-stopped
    ports:
      - "10011:8080"
    depends_on:
      - redis
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - INSTANCE_ID=1
      - SPRING_PROFILES_ACTIVE=prod
      - LOGGING_FILE_NAME=/app/logs/application-instance-1.log
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - ./logs:/app/logs
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:8080/health" ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  backend-2:
    container_name: ${PREFIX}-backend-2
    image: ${PREFIX}-backend:latest
    # build: backend-1 이미지 재사용
    restart: unless-stopped
    ports:
      - "10012:8080"
    depends_on:
      - redis
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - INSTANCE_ID=2
      - SPRING_PROFILES_ACTIVE=prod
      - LOGGING_FILE_NAME=/app/logs/application-instance-2.log
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - ./logs:/app/logs
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:8080/health" ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

networks:
  app-network:
    name: ${PREFIX}-network
    driver: bridge

volumes:
  redis-data:
    name: ${PREFIX}-redis-data
```

## 스프링부트

```
cd /opt/docker/pincoin/backend
git clone git@github.com-mairoo:mairoo/coconut repo
vi repo/src/main/resources/application-prod.yml
```

## nginx 로드밸런서

### `/opt/docker/pincoin/backend/nginx/nginx.conf`

```
user nginx;
worker_processes auto;
error_log /app/logs/load-balancer-error.log notice;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for" "$http_x_forwarded_host"';

    access_log /app/logs/load-balancer-access.log main;

    sendfile on;
    tcp_nopush on;
    keepalive_timeout 65;
    gzip on;

    include /etc/nginx/conf.d/*.conf;
}
```

### `/opt/docker/pincoin/backend/nginx/site.conf`

```
upstream backend {
    server backend-1:8080 max_fails=3 fail_timeout=30s;
    server backend-2:8080 max_fails=3 fail_timeout=30s;
}

server {
    listen 9090;
    server_name localhost;

    # Docker 네트워크만 신뢰
    real_ip_header X-Forwarded-For;
    set_real_ip_from 172.17.0.0/16;
    set_real_ip_from 172.18.0.0/16;

    location / {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;

        proxy_connect_timeout 5s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
        proxy_next_upstream_tries 3;

        # API 캐시 방지
        add_header Cache-Control "max-age=0, must-revalidate, private";
    }

    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

## 주요 명령어

### 최초 시행

````shell
### 운영 환경
# 1. Redis
sudo docker compose up -d redis

# Redis 상태 확인
sudo docker compose logs redis
sudo docker compose exec redis redis-cli ping

# 2. 스프링부트 백엔드
sudo docker compose build --no-cache backend-1
sudo docker compose up -d backend-1 backend-2
sudo docker compose logs -f backend-1 backend-2

# 3. nginx 로드밸런서
sudo docker compose up -d nginx
sudo docker compose logs nginx

# 4. 전체 상태 확인
sudo docker compose ps
````

```shell
# nginx 로드밸런서 재시작
sudo docker compose restart nginx
```

## 구동 스크립트

### `/opt/docker/pincoin/backend/build.sh`

```shell
#!/bin/bash

# repo 디렉토리로 이동해서 git pull
echo "📥 Pulling latest code from git..."
cd repo
git pull
cd ..

# 이미지 빌드 (backend-1 이미지 하나만 빌드)
echo "🔨 Building backend image..."
sudo docker compose build --no-cache backend-1
```

### `/opt/docker/pincoin/backend/deploy.sh`

```shell
#!/bin/bash

check_health() {
    local service=$1
    local port=""
    if [ "$service" = "backend-1" ]; then
        port="10011"
    elif [ "$service" = "backend-2" ]; then
        port="10012"
    fi

    echo "⏳ Waiting for $service to be healthy..."
    for i in {1..36}; do  # 3분 대기 (5초 * 36)
        if curl -f -s http://localhost:$port/health > /dev/null 2>&1; then
            echo "✅ $service is healthy!"
            return 0
        fi
        echo -n "."
        sleep 5
    done

    echo "❌ $service failed to become healthy!"
    return 1
}

restart_service() {
    local service=$1
    echo "🔄 Restarting $service..."

    sudo docker compose stop $service
    sudo docker compose up -d $service

    if check_health $service; then
        return 0
    else
        return 1
    fi
}

# 서비스 순차적 재시작
echo "🔄 Rolling restart..."

# backend-1 재시작
if restart_service "backend-1"; then
    echo "✅ backend-1 restarted successfully"
else
    echo "❌ backend-1 restart failed"
    exit 1
fi

# backend-2 재시작
if restart_service "backend-2"; then
    echo "✅ backend-2 restarted successfully"
else
    echo "❌ backend-2 restart failed"
    exit 1
fi
```

### `/opt/docker/pincoin/backend/full-deploy.sh`

```shell
#!/bin/bash

echo "🚀 Starting full deployment..."
source ./build.sh && source ./deploy.sh && echo "🎉 Full deployment completed!"
```

# 호스트 설정

## `/etc/nginx/sites-available/pincoin.kr`

```
# HTTP to HTTPS 리다이렉트 (백엔드)
server {
    listen 80;
    listen [::]:80;
    server_name api.pincoin.kr;

    # HTTP 요청을 HTTPS로 리다이렉트
    return 301 https://$server_name$request_uri;
}

# HTTPS 서버 (백엔드)
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name api.pincoin.kr;

    # SSL 인증서 경로 (Let's Encrypt 기준)
    ssl_certificate /opt/docker/pincoin/ssl/pincoin.kr.pem;
    ssl_certificate_key /opt/docker/pincoin/ssl/pincoin.kr.key;

    # SSL 보안 설정
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # HSTS 헤더 (HTTPS 강제)
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # 요청 크기 제한
    client_max_body_size 10M;

    # 보안 헤더
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";

    # 프록시 설정
    proxy_buffer_size 128k;
    proxy_buffers 4 256k;
    proxy_busy_buffers_size 256k;
    proxy_connect_timeout 10s;
    proxy_send_timeout 30s;
    proxy_read_timeout 30s;

    # 로그 설정
    access_log /opt/docker/pincoin/backend/logs/host-access.log;
    error_log /opt/docker/pincoin/backend/logs/host-error.log;

    location / {
        proxy_pass http://localhost:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## logrotate

### 구성

```shell
sudo chmod 755 /opt/docker/pincoin/backend/logs/
sudo chown www-data:root /opt/docker/pincoin/backend/logs/
sudo chown www-data:root /opt/docker/pincoin/backend/logs/*.log
```

### `/etc/logrotate.d/pincoin`

```
# 백엔드 호스트 nginx 로그
/opt/docker/pincoin/backend/logs/host-*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    su www-data root
    create 644 www-data root
    postrotate
        systemctl reload nginx 2>/dev/null || true
    endscript
}

# 백엔드 도커 nginx 로그
/opt/docker/pincoin/backend/logs/load-balancer-*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    su root root
    create 644 root root
    copytruncate
}

# 백엔드 애플리케이션 로그
/opt/docker/pincoin/backend/logs/application*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    su root root
    create 644 root root
    copytruncate
}
```

### 주요 명령어

```shell
# 설정 문법 검사
sudo logrotate -d /etc/logrotate.d/pincoin

# 강제 로테이션 테스트
sudo logrotate -f /etc/logrotate.d/pincoin

# 상태 확인
sudo cat /var/lib/logrotate/status | grep pincoin
```

# 도커 정리

`/opt/docker/scripts/cleanup.sh`

```shell
#!/bin/bash
# Docker 정리 스크립트

# 로그 설정
LOG_FILE="/opt/docker/logs/cleanup.log"
mkdir -p "$(dirname "$LOG_FILE")"

echo "$(date): Docker cleanup started" >> "$LOG_FILE"

# 정지된 컨테이너 삭제
docker container prune -f >> "$LOG_FILE" 2>&1

# 사용하지 않는 네트워크 삭제
docker network prune -f >> "$LOG_FILE" 2>&1

# 1주일 이상 된 이미지만 삭제 (안전)
docker image prune --filter "until=168h" -f >> "$LOG_FILE" 2>&1

# 사용하지 않는 볼륨 삭제 (주의: 데이터 손실 가능)
# docker volume prune -f >> "$LOG_FILE" 2>&1

# 디스크 사용량 로깅
echo "$(date): Disk usage after cleanup:" >> "$LOG_FILE"
df -h >> "$LOG_FILE"
docker system df >> "$LOG_FILE"

echo "$(date): Docker cleanup completed" >> "$LOG_FILE"
```


```shell
# 스크립트 실행 권한
sudo chmod +x /opt/docker/scripts/cleanup.sh

# crontab 설정
sudo crontab -e

# 새벽 3시에 실행
0 3 * * * /opt/docker/scripts/cleanup.sh
```