# 도커 배포

## 개발환경 (Hot Reload)

- 볼륨 마운트
- Spring Boot DevTools + `./gradlew bootRun`

### 구성

```
~/Projects/tropical/backend/
~/Projects/tropical/backend/.env
~/Projects/tropical/backend/compose.local.yml
~/Projects/tropical/backend/repo/
```

### `.env`

```
PREFIX=pincoin-tropical
```

### `compose.local.yml`

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
        max-size: "10m"
        max-file: "7"

  backend:
    container_name: ${PREFIX}-backend
    image: ${PREFIX}-backend:local
    build:
      context: ./repo
      dockerfile: Dockerfile.local
    working_dir: /app
    volumes:
      - ./repo:/app:cached  # 소스코드를 볼륨 마운트
      - gradle-cache:/root/.gradle
    ports:
      - "8080:8080"
    depends_on:
      - redis
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - SPRING_PROFILES_ACTIVE=local
    logging:
      driver: "json-file"
      options:
        max-size: "50m"
        max-file: "5"
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:8080/health" ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

networks:
  app-network:
    name: ${PREFIX}-network
    driver: bridge

volumes:
  redis-data:
    name: ${PREFIX}-redis-data
  gradle-cache:
    name: ${PREFIX}-gradle-cache
```

### 명령어

```shell
# redis, backend 모두 시작
docker compose -f compose.local.yml up -d

# 백엔드 인스턴스 이미지 빌드
docker compose -f compose.local.yml build --no-cache backend

# 백엔드 인스턴스 중지
docker compose -f compose.local.yml stop backend

# 백엔드 인스턴스 시작
docker compose -f compose.local.yml up -d backend

# redis CLI
docker compose -f compose.local.yml exec redis redis-cli

# 로그
docker compose -f compose.local.yml logs -f backend
```

## 운영 배포

### 구성

```
~/Projects/tropical/backend/
~/Projects/tropical/backend/.env
~/Projects/tropical/backend/compose.prod.yml
~/Projects/tropical/backend/repo/
~/Projects/tropical/backend/nginx/nginx.conf
~/Projects/tropical/backend/nginx/site.conf
```

### `.env`

```
PREFIX=pincoin-tropical
```

### `compose.prod.yml`

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

### nginx 로드밸런서

#### `~/Projects/tropical/backend/nginx/nginx.conf`

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

#### `~/Projects/tropical/backend/nginx/site.conf`

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


### 명령어

```shell
# 1. Redis
docker compose -f compose.prod.yml up -d redis

# Redis 상태 확인
docker compose -f compose.prod.yml logs redis
docker compose -f compose.prod.yml exec redis redis-cli ping

# 2. 스프링부트 백엔드
docker compose -f compose.prod.yml build backend-1
docker compose -f compose.prod.yml up -d backend-1 backend-2
docker compose -f compose.prod.yml logs -f backend-1 backend-2

# 3. nginx 로드밸런서
docker compose -f compose.prod.yml up -d nginx
docker compose -f compose.prod.yml logs nginx

# 4. 전체 상태 확인
docker compose -f compose.prod.yml ps
```