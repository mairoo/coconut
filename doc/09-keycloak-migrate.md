# Keycloak 점진적 마이그레이션 가이드

## 📋 개요

기존 JWT 기반 인증 시스템을 중단 없이 Keycloak으로 점진적으로 마이그레이션하는 가이드입니다.

## 🚀 마이그레이션 단계

### Phase 1: 준비 단계 (현재)

- **목표**: Keycloak 설정 추가, 기존 시스템 유지
- **환경 변수**: `KEYCLOAK_ENABLED=false`
- **동작**: 기존 JWT 인증만 사용

#### 설정 확인

```yaml
keycloak:
  enabled: false
```

### Phase 2: 병행 운영

- **목표**: Keycloak과 기존 JWT 동시 지원
- **환경 변수**: `keycloak.enabled=true`
- **동작**:
    1. 로그인 시 Keycloak 먼저 시도
    2. Keycloak 실패 시 기존 JWT로 폴백
    3. 신규 사용자는 Keycloak 권장

#### 로그인 플로우

```
사용자 로그인 요청
       ↓
Keycloak 인증 시도
       ↓
    성공? ── YES → Keycloak 토큰으로 내부 JWT 생성
       ↓
      NO
       ↓
기존 JWT 인증 시도
       ↓
    성공? ── YES → 기존 JWT 토큰 생성
       ↓
      NO
       ↓
   인증 실패
```

### Phase 3: 완전 전환

- **목표**: 모든 사용자 Keycloak으로 이전
- **동작**: Keycloak 인증만 사용
- **청리**: 기존 JWT 관련 코드 제거

## 🔧 구현된 파일들

### 1. KeycloakProperties.kt

Keycloak 설정을 위한 프로퍼티 클래스

### 2. KeycloakAuthService.kt

Keycloak 인증 로직을 처리하는 서비스

### 3. AuthService.kt (수정)

기존 서비스에 Keycloak 연동 추가

### 4. SecurityConfig.kt (수정)

Spring Security에 OAuth2 Resource Server 설정 추가

### 5. application.yml (추가)

Keycloak OAuth2 클라이언트 설정

## 🧪 테스트 시나리오

### Phase 1 테스트

```bash
# 기존 JWT 로그인 확인
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123!"}'
```

### Phase 2 테스트

```bash
# 환경 변수 변경
keycloak.enabled=true

# Keycloak 사용자로 로그인 시도 (성공 시 Keycloak 사용)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "keycloak-user@example.com", "password": "password123!"}'

# 기존 사용자로 로그인 시도 (Keycloak 실패 시 기존 JWT 사용)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "existing-user@example.com", "password": "password123!"}'
```

## 📊 모니터링 포인트

### 로그 확인

```
# Keycloak 인증 성공
"Keycloak 로그인: 성공"

# Keycloak 인증 실패 후 JWT 폴백
"Keycloak 로그인: 실패" → "비밀번호 로그인: 성공"

# 완전 실패
"Keycloak 로그인: 실패" → "비밀번호 로그인: 비밀번호 불일치"
```

### 메트릭 수집

- Keycloak 인증 성공/실패 횟수
- JWT 폴백 사용 횟수
- 인증 방식별 응답 시간

## ⚠️ 주의사항

### 1. 사용자 매핑

- `email` 기준으로 기존 사용자와 Keycloak 사용자 매핑
- `auto-create: false`로 설정하여 의도치 않은 사용자 생성 방지

### 2. 패스워드 처리

- Keycloak 사용자의 경우 `password` 필드가 빈 값
- 기존 사용자는 계속 기존 패스워드 사용

### 3. 권한 매핑

- Keycloak에서 생성된 사용자는 기본적으로 일반 사용자 권한
- 필요시 수동으로 권한 조정

## 🔄 롤백 계획

### Phase 2에서 Phase 1로 롤백

```bash
# 환경 변수만 변경
keycloak.enabled=false
```

### 완전 롤백

1. Keycloak 관련 코드 주석 처리
2. 기존 JWT 로직만 유지
3. 환경 변수 정리

## 📈 마이그레이션 완료 기준

### Phase 2 → Phase 3 전환 기준

- [ ] 전체 로그인의 90% 이상이 Keycloak 사용
- [ ] Keycloak 인증 오류율 1% 미만
- [ ] 성능 이슈 없음 확인
- [ ] 1주일 이상 안정적 운영

### 성공 지표

- 중단 시간 0분
- 사용자 불편 최소화
- 데이터 손실 없음
- 인증 실패율 기존 수준 유지