# 도커 설정

## `.env`

```properties
PREFIX=pincoin
```

## `docker-compose.yml`

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

networks:
  app-network:
    name: ${PREFIX}-network
    driver: bridge

volumes:
  redis-data:
    name: ${PREFIX}-redis-data
```

## redis 도커 실행

```shell
# Redis 실행
docker compose up -d redis

# Redis 실행 결과 확인
docker compose ps

# Redis 도커 컨테이너 내 CLI 접속
docker compose exec redis redis-cli
```

## Redis 설정

- [RedisConfig](/src/main/kotlin/kr/pincoin/api/global/config/RedisConfig.kt) 추가
- [application-local.yml](/src/main/resources/application-local.yml) 파일에 추가

```yaml
spring:
  data:
    redis:
      host: pincoin-redis  # 도커 redis 컨테이너 이름
      port: 6379 # 컨테이너 내부 포트 접근 가능
      repositories: # RedisTemplate 사용, Redis Repository 미사용
        enabled: false
```