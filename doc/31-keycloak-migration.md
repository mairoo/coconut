# Keycloak 마이그레이션 가이드

## 마이그레이션 시나리오

### 1. 신규 회원가입 (AuthController)

**목표**: 새로운 사용자를 처음부터 Keycloak 기반으로 생성

#### 처리 플로우

```
POST /auth/sign-up → 이메일 인증 → GET /auth/verify-email/{token} → 완료
```

#### 주요 처리 과정

**1단계: 회원가입 요청**

- reCAPTCHA 검증 및 IP별 가입 제한 (3회/일)
- 이메일 도메인 검증 및 중복 확인
- 인증 이메일 발송 (UUID 토큰)
- 임시 데이터 AES 암호화하여 Redis 저장 (24시간 TTL)

**2단계: 이메일 인증 완료**

- Redis에서 임시 데이터 조회 및 검증
- 이메일 중복 재검증 (방어적 프로그래밍)
- Keycloak과 DB에 동시 사용자 생성 (분산 트랜잭션)
- Redis 정리 및 환영 이메일 발송

#### 최종 결과

```
User: email(O) + password("") + keycloakId(UUID)
```

### 2. 수동 마이그레이션 (MigrationController)

**목표**: 기존 Django 사용자를 Keycloak으로 수동 마이그레이션

#### 처리 플로우

```
POST /auth/migrate → 레거시 검증 → Keycloak 생성 → DB 연결 → 완료
```

#### 주요 처리 과정

1. **보안 검증**: reCAPTCHA 인증
2. **레거시 사용자 조회**: 이메일 기반 활성 사용자 검색
3. **마이그레이션 상태 확인**: 이미 마이그레이션된 사용자 체크
4. **비밀번호 검증**: Django PBKDF2 방식으로 검증
5. **Keycloak 마이그레이션**: 사용자 생성 후 DB 연결
6. **패스워드 삭제**: DB에서 레거시 패스워드 제거

#### 예외 처리 (명확한 구분)

- **사용자 없음**: `INVALID_CREDENTIALS`
- **비밀번호 틀림**: `INVALID_CREDENTIALS`
- **이미 마이그레이션됨**: `ALREADY_MIGRATED`
- **Keycloak 연동 실패**: `USER_EXISTS`, `TIMEOUT`, `SYSTEM_ERROR`

#### 최종 결과

```
마이그레이션 전: email(O) + password(PBKDF2) + keycloakId(null)
마이그레이션 후: email(O) + password("") + keycloakId(UUID)
```

### 3. 소셜 로그인 자동 마이그레이션 (SocialMigrationController)

**목표**: NextAuth.js + Keycloak 소셜 로그인 시 자동 사용자 매칭 및 마이그레이션

#### 처리 플로우

```
NextAuth.js → Keycloak → JWT Token → POST /oauth2/migrate → 케이스별 처리
```

#### 케이스별 처리 로직

모든 케이스는 최종적으로 **케이스 3 (정상 상태)**로 수렴됩니다.

| 케이스   | 사용자 상태                      | 처리 방식           | 결과      |
|-------|-----------------------------|-----------------|---------|
| 케이스 1 | password(O) + keycloakId(O) | 데이터 정합성 오류      | ERROR   |
| 케이스 2 | password(O) + keycloakId(X) | 수동 마이그레이션 필요 안내 | → 케이스 3 |
| 케이스 3 | password(X) + keycloakId(O) | 정상 로그인 (목표 상태)  | ✅       |
| 케이스 4 | password(X) + keycloakId(X) | 소셜 전용 사용자 연동    | → 케이스 3 |
| 케이스 5 | 사용자 없음                      | 신규 사용자 생성       | → 케이스 3 |

#### 설계 철학

- **책임 분리**: 백엔드는 "누가 로그인했는가"만 관심, "어떻게 로그인했는가"는 Keycloak 위임
- **통합 처리**: 모든 인증 방식(ID/PW, 소셜)은 케이스 3에서 동일하게 처리
- **자동화**: 가능한 한 사용자 개입 없이 자동 마이그레이션

#### 보안 고려사항

- 이메일 검증된 소셜 계정만 마이그레이션 허용
- Spring Security OAuth2 Resource Server로 JWT 토큰 검증
- 데이터 정합성 검증을 통한 비정상 상태 감지

## 전체 마이그레이션 전략

### 목표 상태

모든 사용자는 최종적으로 다음 상태로 수렴됩니다:

```
User: email(O) + password("") + keycloakId(UUID) + isActive(true)
```

### 마이그레이션 경로

```
신규 회원가입 ────┐
수동 마이그레이션 ──┼─→ 케이스 3 (정상 상태)
소셜 로그인 ──────┘
```