# 로컬 개발 환경

## 디렉토리 구성

```
~/Projects/pincoin/backend/
~/Projects/pincoin/backend/repo
~/Projects/pincoin/backend/.env
~/Projects/pincoin/backend/docker-compose.yml
```

## `.env`

```properties
PREFIX=pincoin
KEYCLOAK_DB=postgres
KEYCLOAK_POSTGRES_DATABASE=keycloak
KEYCLOAK_POSTGRES_USER=keycloak
KEYCLOAK_POSTGRES_PASSWORD=Test12#$
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=Test12#$
```

## `docker-compose.yml` 복사

| 구분    | 도커                | 외부 노출 | 도커 내부 |
|-------|-------------------|-------|-------|
| 백엔드   | redis             | -     | 6379  |
| 백엔드   | mariadb           | 3306  | 3306  |
| 백엔드   | keycloak-postgres | 5432  | 5432  |
| 백엔드   | keycloak          | 8081  | 8080  |
| 백엔드   | backend           | 8080  | 8080  | 
| 프론트엔드 | frontend          | 3000  | 3000  |

## keycloak 웹 콘솔 설정

### 접속

- http://localhost:8081
- 아이디: KEYCLOAK_ADMIN 설정 값 (예, admin)
- 비밀번호: KEYCLOAK_ADMIN_PASSWORD 설정 값 (예, secure_admin_password_123)

### realm 생성

- Realm 생성: Realms → Create Realm → (Realm name: `pincoin`)

### client 생성

- Client 생성: Clients → Create Client →
    1. General Settings:
        - **Client type: OpenID Connect**
        - **Client ID: pincoin-backend**
        - Name: (없음)
        - Description: (없음)
        - Always display in UI: OFF
    2. Capability Config:
        - **Client authentication: ON**  (중요!)
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

## `application-local.yml` 파일 생성 및 수정

```
<     url: jdbc:postgresql://postgresql:5432/database
<     username: username
<     password: password
<             client-secret: your-client-secret
<   client-secret: your-client-secret
```