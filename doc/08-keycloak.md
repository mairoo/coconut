# 설치

## docker-compose.yml

### 개발

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
      - KC_HOSTNAME=keycloak
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

### 운영

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
    ports:
      - "10013:8080"
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
      - KC_HOSTNAME=keycloak
      - KC_HOSTNAME_PORT=8080
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=true
      - KC_HEALTH_ENABLED=true
      - KC_METRICS_ENABLED=true
    command: start
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

## 필수 의존성 설정

```
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
```

## .env

```
PREFIX=pincoin

KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=secure_admin_password_123
KEYCLOAK_DB_PASSWORD=secure_db_password_123

KEYCLOAK_ENABLED=false
KEYCLOAK_REALM=pincoin
KEYCLOAK_CLIENT_ID=pincoin-backend
KEYCLOAK_CLIENT_SECRET=your-secret
KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080
```

## KEYCLOAK_CLIENT_SECRET 받아서 `.env` 파일 수정

```bash
# 토큰 받기
ACCESS_TOKEN=$(curl -s -X POST http://localhost:10013/realms/master/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=secure_admin_password_123" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

echo "토큰: $ACCESS_TOKEN"

# pincoin Realm 생성
curl -X POST http://localhost:10013/admin/realms \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"realm": "pincoin", "enabled": true}'

# Client 생성
curl -X POST http://localhost:10013/admin/realms/pincoin/clients \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "pincoin-backend",
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "redirectUris": ["http://localhost:8080/login/oauth2/code/keycloak"],
    "webOrigins": ["*"],
    "standardFlowEnabled": true,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "publicClient": false
  }'

# Client Secret 조회
CLIENT_UUID=$(curl -s -X GET "http://localhost:10013/admin/realms/pincoin/clients?clientId=pincoin-backend" -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.[0].id')
CLIENT_SECRET=$(curl -s -X GET "http://localhost:10013/admin/realms/pincoin/clients/$CLIENT_UUID/client-secret" -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.value')

echo "KEYCLOAK_CLIENT_SECRET=$CLIENT_SECRET"
```

## `application.yml`

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Keycloak JWT 검증 설정 (기존 JWT와 병행 사용)
          issuer-uri: ${KEYCLOAK_AUTH_SERVER_URL:http://keycloak:8080}/realms/${KEYCLOAK_REALM:pincoin}
      client:
        registration:
          keycloak:
            client-id: ${KEYCLOAK_CLIENT_ID:pincoin-backend}
            client-secret: ${KEYCLOAK_CLIENT_SECRET:}
            scope: openid,profile,email
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_AUTH_SERVER_URL:http://keycloak:8080}/realms/${KEYCLOAK_REALM:pincoin}
            user-name-attribute: preferred_username
            
keycloak:
  # 점진적 마이그레이션을 위한 활성화 플래그
  enabled: ${KEYCLOAK_ENABLED:false}
  # 기존 사용자 매핑 전략
  user-migration:
    strategy: email-based  # email 기준으로 매핑
    auto-create: false     # 자동 생성 비활성화
  # Realm 및 클라이언트 설정
  realm: ${KEYCLOAK_REALM:pincoin}
  client-id: ${KEYCLOAK_CLIENT_ID:pincoin-backend}
  client-secret: ${KEYCLOAK_CLIENT_SECRET:}
  server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://keycloak:8080}
```

# 주요 파일

- [/api/global/properties/KeycloakProperties.kt](/src/main/kotlin/kr/pincoin/api/global/properties/KeycloakProperties.kt)
- [/api/global/config/SecurityConfig.kt](/src/main/kotlin/kr/pincoin/api/global/config/SecurityConfig.kt)
- [/api/global/config/KeycloakWebClientConfig.kt](/src/main/kotlin/kr/pincoin/api/global/config/KeycloakWebClientConfig.kt)
- [/api/app/auth/service/AuthService.kt](/src/main/kotlin/kr/pincoin/api/app/auth/service/AuthService.kt)
- [/api/app/auth/service/KeycloakAuthService.kt](/src/main/kotlin/kr/pincoin/api/app/auth/service/KeycloakAuthService.kt)

# 마이그레이션

## 절차

Phase 1: 준비 단계

- KEYCLOAK_ENABLED=false로 시작
- 기존 JWT 인증 유지
- Keycloak 설정만 추가

Phase 2: 병행 운영

- KEYCLOAK_ENABLED=true로 변경
- 기존 사용자: 기존 JWT 사용
- 신규 사용자: Keycloak 사용

Phase 3: 완전 전환

- 모든 사용자 Keycloak으로 마이그레이션
- 기존 JWT 설정 제거