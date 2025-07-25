# Keycloak

- Keycloak: 인증 서버
- 백엔드: 권한 관리 (Group, Role 매핑)

# Keycloak 설치

## `.env`

다음 내용 추가

```properties
KEYCLOAK_DB=postgres
KEYCLOAK_POSTGRES_DATABASE=keycloak
KEYCLOAK_POSTGRES_USER=keycloak
KEYCLOAK_POSTGRES_PASSWORD=secure_db_password_123
```

## `docker-compose.yml`

```yaml
services:
  keycloak-postgres:
    container_name: ${PREFIX}-keycloak-postgres
    image: postgres:15-alpine
    restart: unless-stopped
    ports:
      - "15432:5432"
    volumes:
      - keycloak-postgres-data:/var/lib/postgresql/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - POSTGRES_DB=${KEYCLOAK_POSTGRES_DATABASE}
      - POSTGRES_USER=${KEYCLOAK_POSTGRES_USER}
      - POSTGRES_PASSWORD=${KEYCLOAK_POSTGRES_PASSWORD}
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  keycloak:
    container_name: ${PREFIX}-keycloak
    image: quay.io/keycloak/keycloak:26.3.1
    restart: unless-stopped
    ports:
      - "8081:8080"
    depends_on:
      - keycloak-postgres
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - KC_DB=${KEYCLOAK_DB}
      - KC_DB_URL=jdbc:postgresql://keycloak-postgres:5432/${KEYCLOAK_POSTGRES_DATABASE}
      - KC_DB_USERNAME=${KEYCLOAK_POSTGRES_USER}
      - KC_DB_PASSWORD=${KEYCLOAK_POSTGRES_PASSWORD}
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

volumes:
  keycloak-postgres-data:
    name: ${PREFIX}-keycloak-postgres-data
  keycloak-data:
    name: ${PREFIX}-keycloak-data
```

# 초기 설정

## 임시 계정 `temp-admin` 생성

```shell
# 도커 컨테이너 시작
docker compose up -d keycloak-postgres keycloak

# temp-admin 생성
docker exec -it pincoin-keycloak /opt/keycloak/bin/kc.sh bootstrap-admin user
Enter username [temp-admin]:temp-admin
Enter password: [비밀번호]
Enter password again: [비밀번호]

# 도커 컨테이너 재시작
docker compose restart keycloak
```

## Keycloak 영구 `admin` 계정 생성 및 `temp-admin` 삭제

1. **http://localhost:8081** 접속 후 `temp-admin`으로 로그인
2. 좌측 상단의 **Master** realm이 선택되어 있는지 확인
3. 좌측 메뉴에서 **Users** 클릭
4. **Create new user** 버튼 클릭

- Email verified: On (체크)
- Username: `admin`
- Email: `admin@example.com`
- First name: John
- Last name: Doe

5. Credentials 탭 비밀번호 저장

- Password: 비밀번호
- Password confirmation: 비밀번호 확인
- Temporary: Off (체크 안 함)

6. Role mapping 탭 관리자 역할 부여

`Realm roles` 선택 후 `Assign role`에서 `admin` 체크 후 `Assgin`

## 새 계정으로 로그인 테스트

1. 로그아웃 (우측 상단 사용자명 클릭 → Sign out)
2. 새로 만든 계정으로 로그인:
   ```
   Username: admin
   Password: Test12#$
   ```
3. Keycloak 임시 계정 `temp-admin` 삭제

## realm 생성

- Realm 생성: Realms → Create Realm → (Realm name: `pincoin`)

## master realm, pincoin realm 이벤트 로깅 저장 설정

- Event Listeners: jboss-logging + email
- User Events: Save events 체크, Expiration 90 days, 모든 이벤트 유지
- Admin Events: Save events 체크, Expiration 90 days, Include representation(관리자가 변경한 데이터의 상세 내용까지 함께 저장) 체크 안 함, 모든 이벤트
  유지

## client 생성

- Client 생성: Clients → Create Client →
    1. General Settings:
        - **Client type: OpenID Connect**
        - **Client ID: pincoin-backend**
        - Name: (없음)
        - Description: (없음)
        - Always display in UI: OFF
    2. Capability Config:
        - **Client authentication: ON** (중요!)
        - Authorization: OFF
        - **Standard flow: ON**
        - **Direct access grants: ON**
        - Implicit flow: OFF
        - **Service accounts roles: ON**
        - OAuth 2.0 Device Authorization Grant: OFF
        - OIDC CIBA Grant: OFF
    3. Login Settings
        - Root URL: (없음)
        - Home URL: (없음)
        - Valid redirect URIs: http://localhost:8080/*
        - Valid post logout redirect URIs: (없음)
        - Web origins: (없음)

- `pincoin-backend` 클라이언트 상세 보기 `Service accounts roles` 탭 선택
    - Assign role 버튼 누르고 Client roles 선택 후 역할 추가
        - `realm-management`: `manage-users`
        - `realm-management`: `view-users`
        - `realm-management`: `query-users`

- `pincoin-backend` 클라이언트 설정 완료 후 `Credentials` 탭에서 Client Secret 복사

# 스프링부트 설정

## `docker-compose.yml`

backend 도커 설정에 추가 사항: 의존성 추가, KEYCLOAK 접속 주소 설정

```yaml
    depends_on:
      - redis
      - keycloak # (1) 추가
    environment:
      - TZ=Asia/Seoul
      - SPRING_PROFILES_ACTIVE=local
      - KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080 # (2) 추가
```

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
  realm: pincoin
  client-id: pincoin-backend
  client-secret: your-client-secret
  server-url: http://keycloak:8080
  timeout: 5000
  cookie-domains: # 도메인에 프로토콜 및 포트번호 미포함, 서브도메인으로 지정하면 다른 서브도메인에서 접근 불가
    - localhost
```

# Keycloak postgres DB 완전 초기화

```bash
docker compose down keycloak keycloak-postgres

docker volume rm pincoin-keycloak-postgres-data
docker volume rm pincoin-keycloak-data
```

# 기타 도커 관리 명령어

```shell
# 도커 실행 프로세스 확인
docker compose ps

# 도커 로그 확인
docker compose logs -f keycloak

# psql 클라이언트로 직접 접근
docker compose exec keycloak-postgres psql -U keycloak -d keycloak

# postgres 유저로 접근
docker compose exec keycloak-postgres psql -U postgres
```