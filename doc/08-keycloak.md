# docker-compose.yml

## 운영
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

  keycloak-postgres:
    container_name: ${PREFIX}-keycloak-postgres
    image: postgres:15-alpine
    restart: unless-stopped
    volumes:
      - keycloak-postgres-data:/var/lib/postgresql/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - POSTGRES_DB=keycloak
      - POSTGRES_USER=keycloak
      - POSTGRES_PASSWORD=${KEYCLOAK_DB_PASSWORD:-keycloak123}
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  keycloak:
    container_name: ${PREFIX}-keycloak
    image: quay.io/keycloak/keycloak:23.0.3
    restart: unless-stopped
    depends_on:
      - keycloak-postgres
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - KEYCLOAK_ADMIN=${KEYCLOAK_ADMIN:-admin}
      - KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD:-admin123}
      - KC_DB=postgres
      - KC_DB_URL=jdbc:postgresql://keycloak-postgres:5432/keycloak
      - KC_DB_USERNAME=keycloak
      - KC_DB_PASSWORD=${KEYCLOAK_DB_PASSWORD:-keycloak123}
      - KC_HOSTNAME=localhost
      - KC_HOSTNAME_PORT=8080
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=true
      - KC_HEALTH_ENABLED=true
      - KC_METRICS_ENABLED=true
    command: start-dev
    volumes:
      - keycloak-data:/opt/keycloak/data
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
      - keycloak
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
      - keycloak
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - INSTANCE_ID=1
      - SPRING_PROFILES_ACTIVE=prod
      - LOGGING_FILE_NAME=/app/logs/application-instance-1.log
      - KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080
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
    # build: backend-1 이미지 재사용
    image: ${PREFIX}-backend:latest
    restart: unless-stopped
    ports:
      - "10012:8080"
    depends_on:
      - redis
      - keycloak
      - backend-1
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - INSTANCE_ID=2
      - SPRING_PROFILES_ACTIVE=prod
      - LOGGING_FILE_NAME=/app/logs/application-instance-2.log
      - KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080
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
  keycloak-postgres-data:
    name: ${PREFIX}-keycloak-postgres-data
  keycloak-data:
    name: ${PREFIX}-keycloak-data
```

## 개발
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

  keycloak-postgres:
    container_name: ${PREFIX}-keycloak-postgres
    image: postgres:15-alpine
    restart: unless-stopped
    volumes:
      - keycloak-postgres-data:/var/lib/postgresql/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - POSTGRES_DB=keycloak
      - POSTGRES_USER=keycloak
      - POSTGRES_PASSWORD=${KEYCLOAK_DB_PASSWORD:-keycloak123}
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  keycloak:
    container_name: ${PREFIX}-keycloak
    image: quay.io/keycloak/keycloak:23.0.3
    restart: unless-stopped
    depends_on:
      - keycloak-postgres
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - KEYCLOAK_ADMIN=${KEYCLOAK_ADMIN:-admin}
      - KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD:-admin123}
      - KC_DB=postgres
      - KC_DB_URL=jdbc:postgresql://keycloak-postgres:5432/keycloak
      - KC_DB_USERNAME=keycloak
      - KC_DB_PASSWORD=${KEYCLOAK_DB_PASSWORD:-keycloak123}
      - KC_HOSTNAME=localhost
      - KC_HOSTNAME_PORT=8080
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=true
      - KC_HEALTH_ENABLED=true
      - KC_METRICS_ENABLED=true
    command: start-dev
    volumes:
      - keycloak-data:/opt/keycloak/data
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

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
      - keycloak
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - SPRING_PROFILES_ACTIVE=local
      - KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080
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
  keycloak-postgres-data:
    name: ${PREFIX}-keycloak-postgres-data
  keycloak-data:
    name: ${PREFIX}-keycloak-data
  gradle-cache:
    name: ${PREFIX}-gradle-cache
```

## 필수 의존성 설정

```
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
```