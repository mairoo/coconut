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
      # - KC_HOSTNAME=keycloak
      # - KC_HOSTNAME_PORT=8080
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
      - KEYCLOAK_AUTH_SERVER_URL=http://localhost:10013
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
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=true
      - KC_PROXY=edge # 프록시 모드
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
```

## KEYCLOAK_CLIENT_SECRET 받아서 `.env` 파일 수정

```
Unable to resolve Configuration with the provided Issuer of "http://keycloak:8080/realms/pincoin",
errors: [404 Not Found on GET request for "http://keycloak:8080/realms/pincoin/.well-known/openid-configuration": "{"error":"Realm does not exist"}"]
```

## 주요 객체 생성

1. Realm 생성: Realms → Create Realm → (Realm name: `pincoin`)
2. `pincoin-backend` Client 생성: Clients → Create Client →
    1. General Settings:
        - **Client type: OpenID Connect**
        - **Client ID: pincoin-backend**
        - Name: (없음)
        - Description: (없음)
        - Always display in UI: OFF (체크 해제)
    2. Capability Config:
        - **Client authentication: ON** (중요! 켜야 함)
        - Authorization: OFF (체크 해제)
        - **Standard flow: ON** (Authorization Code Flow)
        - **Direct access grants: ON** (Resource Owner Password Credentials)
        - Implicit flow: OFF (체크 해제)
        - **Service accounts roles: ON** (서비스 계정 활성화)
        - OAuth 2.0 Device Authorization Grant: OFF (체크 해제)
        - OIDC CIBA Grant: OFF (체크 해제)
    3. Login Settings
        - Root URL: (없음)
        - Home URL: (없음)
        - Valid redirect URIs: http://localhost:8080/*
        - Valid post logout redirect URIs: (없음)
        - Web origins: (없음)
3. `pincoin-backend` 클라이언트 설정 완료 후 Client Secret 복사
4. `pincoin-backend` 클라이언트 설정 Service accounts roles 선택
    - Assign role 버튼 누르고 `Filter by realm roles` 드롭다운에서 `Filter by clients` 선택하여 다음 추가 할당
        - `realm-management`: `manage-users`
        - `realm-management`: `view-users`
        - `realm-management`: `query-users`

운영에서

Login Settings

    - Root URL: (없음 - 여러 도메인 사용)
    - Home URL: (없음 - 여러 도메인 사용)
    - Valid redirect URIs: https://api.pincoin.kr/*, https://www.pincoin.kr/*, https://card.pincoin.kr/*
    - Valid post logout redirect URIs: https://www.pincoin.kr/, https://card.pincoin.kr/, https://www.pincoin.kr/login, https://card.pincoin.kr/login
    - Web origins: https://api.pincoin.kr, https://www.pincoin.kr, https://card.pincoin.kr

## `application.yml`

- http://keycloak:8080: 도커 내부 HTTP 격리된 네트워크
    - 개발: 외부 노출 10013 포트로 웹 콘솔 도커 접근
    - 운영: Cloudflare WAF → 호스트 Nginx (HTTPS) → 도커 내부

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # Keycloak JWT 검증 설정 (기존 JWT와 병행 사용)
          issuer-uri: http://keycloak:8080/realms/pincoin
      client:
        registration:
          keycloak:
            client-id: pincoin-backend
            client-secret: your-secret
            scope: openid,profile,email
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          keycloak:
            issuer-uri: http://keycloak:8080/realms/pincoin
            user-name-attribute: preferred_username

keycloak:
  # 점진적 마이그레이션을 위한 활성화 플래그
  enabled: true
  # 기존 사용자 매핑 전략
  user-migration:
    strategy: email-based  # email 기준으로 매핑
    auto-create: true # 자동 생성 여부
  # Realm 및 클라이언트 설정
  realm: pincoin
  client-id: pincoin-backend
  client-secret: your-secret
  server-url: http://keycloak:8080
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

# 리셋

```bash
# 1. 컨테이너 중지 및 제거
docker compose down

# 2. Keycloak 관련 볼륨 제거 (데이터베이스 포함)
docker volume ls | grep keycloak
docker volume rm [볼륨명]

# 3. 컨테이너 재시작
docker compose up -d keycloak-postgres keycloak
```

깡통 상태인지 확인하는 법

- Admin Console에 로그인 후 Master realm만 존재 (나머지 기타 realm 없어야 함)
- Clients 메뉴에 기본 클라이언트들 6개만 존재
    - account - 사용자 계정 관리 페이지용
    - account-console - 새로운 계정 콘솔 UI용
    - admin-cli - 관리 CLI 도구용 (curl로 토큰 받을 때 사용하던 것)
    - broker - Identity Provider 브로커 기능용
    - master-realm - Master realm 관리용
    - security-admin-console - 관리자 콘솔 웹 UI용
