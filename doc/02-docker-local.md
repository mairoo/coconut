# 도커 배포

## 개발환경 (Hot Reload)

- 볼륨 마운트
- Spring Boot DevTools + `./gradlew bootRun`

### 구성

```
~/Projects/tropical/backend/
~/Projects/tropical/backend/.env
~/Projects/tropical/backend/docker-compose.yml
~/Projects/tropical/backend/repo/
```

### `.env`

```
PREFIX=pincoin-tropical
```

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
docker compose up -d

# 백엔드 인스턴스 이미지 빌드
docker compose build --no-cache backend

# 백엔드 인스턴스 중지
docker compose stop backend

# 백엔드 인스턴스 시작
docker compose up -d backend

# redis CLI
docker compose exec redis redis-cli

# 로그
docker compose logs -f backend
```