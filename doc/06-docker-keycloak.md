# Keycloak

- Keycloak: 인증 서버
- 백엔드: 권한 관리 (Group, Role 매핑)

# 설치

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

# 초기 설정



## `temp-admin` 생성

```shell
# 도커 컨테이너 시작
docker compose up -d keycloak-postgres keycloak

# temp-admin 생성
docker exec -it pincoin-keycloak /opt/keycloak/bin/kc.sh bootstrap-admin user

# 도커 컨테이너 재시작
docker compose restart keycloak
```

## Keycloak 영구 Admin 계정 생성 및 임시 계정 삭제

### 영구 Admin 계정 생성

#### 웹 콘솔에서 작업:
1. **http://localhost:8081** 접속 후 temp-admin으로 로그인
2. 좌측 상단의 **Master** realm이 선택되어 있는지 확인
3. 좌측 메뉴에서 **Users** 클릭
4. **Create new user** 버튼 클릭

#### 사용자 기본 정보 입력:
```
Username: admin
Email: admin@example.com (선택사항)
First name: Admin (선택사항)  
Last name: User (선택사항)
Email verified: ON (체크)
Enabled: ON (체크)
```

5. **Create** 버튼 클릭

### 비밀번호 설정

#### 생성된 사용자의 Credentials 탭에서:
1. **admin** 사용자를 클릭하여 상세 페이지로 이동
2. **Credentials** 탭 클릭
3. **Set password** 클릭
4. 비밀번호 설정:
   ```
   Password: Test12#$
   Password confirmation: Test12#$
   Temporary: OFF (체크 해제) ← 중요!
   ```
5. **Set password** 버튼 클릭

### Admin 권한 부여

#### Role mappings 설정:
1. 같은 사용자 페이지에서 **Role mappings** 탭 클릭
2. **Assign role** 버튼 클릭
3. **Filter by clients** 체크박스 체크
4. **master-realm » admin** 역할 하나만 할당하면 모든 관리 권한이 포함됩니다
5. **Assign** 버튼 클릭

### 임시 계정 삭제

#### temp-admin 사용자 삭제:
1. **Users** 목록으로 돌아가기
2. **temp-admin** 사용자 찾기
3. 해당 사용자 행의 **Actions** → **Delete** 클릭
4. 삭제 확인

## 새 계정으로 로그인 테스트

1. 로그아웃 (우측 상단 사용자명 클릭 → Sign out)
2. 새로 만든 계정으로 로그인:
   ```
   Username: admin
   Password: Test12#$
   ```

## realm 생성

- Realm 생성: Realms → Create Realm → (Realm name: `pincoin`)

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
    - Assign role 버튼 누르고 `Filter by realm roles` 드롭다운에서 `Filter by clients` 선택하여 다음 추가 할당
        - `realm-management`: `manage-users`
        - `realm-management`: `view-users`
        - `realm-management`: `query-users`

- `pincoin-backend` 클라이언트 설정 완료 후 `Credentials` 탭에서 Client Secret 복사

# 스프링부트 설정

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

# 도커 관리 명령어

```shell
# 도커 실행
docker compose up -d

# 도커 실행 프로세스 확인
docker compose ps

# 도커 로그 확인
docker compose logs -f keycloak

# psql 클라이언트로 직접 접근
docker compose exec keycloak-postgres psql -U keycloak -d keycloak

# postgres 유저로 접근
docker compose exec keycloak-postgres psql -U postgres
```