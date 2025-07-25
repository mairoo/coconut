# User 테이블과 keycloak 매핑 전략

## KeycloakId (UUID)

- nullable keycloakId

    - 신규 프로젝트에서 시스템 복잡도 감소
    - 명확한 비즈니스 규칙
    - 예외 처리 코드 불필요

- nullable keycloakId

    - 이미 운영 중인 서비스에 Keycloak 도입
    - 기존 사용자들은 keycloakId가 없는 상태
    - 점진적 마이그레이션 가능

## 이메일

- 별도 필드 추가 없음
- 이메일 주소 대소문자 구분 입력 오류에 따른 불일치 가능성

## SQL

```sql
-- auth_user 테이블에 Keycloak 연동 컬럼 추가
ALTER TABLE auth_user
    ADD COLUMN keycloak_id UUID NULL;

-- UNIQUE 인덱스 추가 (중복 방지)
CREATE UNIQUE INDEX idx_auth_user_keycloak_id_unique ON auth_user (keycloak_id) WHERE keycloak_id IS NOT NULL;
```

# Keycloak 점진적 이관 설계

## 개요

기존 세션 기반 인증 시스템에서 Keycloak JWT 기반 인증으로 점진적 마이그레이션

## 데이터베이스 스키마 변경

### User 테이블 수정

기존 테이블에 `keycloak_id` UUID 필드 추가

- **목적**: 레거시 사용자와 Keycloak 사용자 간 매핑
- **초기값**: NULL (기존 사용자들)
- **인덱스**: 성능을 위한 인덱스 생성 필요

## 서비스 아키텍처 설계

### 인터페이스 기반 버전 관리

- `AuthService` 인터페이스 정의
- V1 구현체: 점진적 마이그레이션 로직 포함
- V2 구현체: 향후 Keycloak 기반 인증 처리
- Spring Profile을 통한 구현체 전환

## V1 구현체 (점진적 마이그레이션) 동작 방식

### 1. 로그인 프로세스

**1단계: Keycloak 우선 인증 시도**

- 사용자 이메일/패스워드로 Keycloak 인증 요청
- 성공 시: JWT 액세스 토큰과 리프레시 토큰 반환
- 실패 시: 2단계로 진행

**2단계: 레거시 사용자 검증**

- 기존 User 테이블에서 이메일로 사용자 조회
- 레거시 패스워드 인코더(PBKDF2)로 비밀번호 검증
- 검증 실패 시: 인증 오류 반환
- 검증 성공 시: 3단계로 진행

**3단계: Keycloak 마이그레이션**

- 해당 사용자를 Keycloak에 새로 생성 (입력받은 패스워드로)
- User 테이블의 `keycloak_id` 필드 업데이트
- 새로 생성된 Keycloak 계정으로 JWT 토큰 발급

### 2. 리프레시 토큰 처리

- Keycloak의 리프레시 토큰 엔드포인트 호출
- 새로운 액세스 토큰 발급
- 리프레시 토큰은 HTTP-only 쿠키로 관리

### 3. 로그아웃 처리

- Keycloak 세션 무효화
- HTTP-only 쿠키 삭제

### 4. 회원가입 처리

**1단계: 회원가입 정보 임시 저장**

- 입력받은 회원정보(email, username, firstname, lastname, password) Redis에 임시 저장
- 비밀번호는 AES 암호화하여 저장 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
- UUID 인증 토큰 생성 및 Redis에 매핑 저장
- TTL 설정 (예: 24시간)

**2단계: 이메일 인증 발송**

- Keycloak이 아닌 백엔드에서 사용자 이메일로 인증 링크 발송
- 인증 링크에 토큰 포함

**3단계: 이메일 인증 완료 처리**

- 인증 토큰으로 Redis에서 회원정보 조회
- 암호화된 비밀번호를 복호화
- Keycloak에 사용자 생성 (복호화된 원본 비밀번호 사용)
- RDBMS User 테이블에 사용자 정보 저장 (keycloak_id 포함)
- Redis에서 임시 데이터 삭제

**4단계: 인증 실패 처리**

- TTL 만료 시 Redis에서 자동 삭제 (암호화된 데이터 포함)
- 재가입 시 새로운 인증 프로세스 진행

## 보안 고려사항

### 패스워드 인코더 관리

- 레거시 인코더: PBKDF2 구현체 직접 생성하여 사용
- Keycloak: 자체 패스워드 정책 및 인코딩 방식 활용 (백엔드에서 직접 비밀번호 암호화 관련 처리 없음)
- 마이그레이션 시 레거시 테이블의 패스워드는 검증용으로만 사용

### 토큰 응답 형식

- 액세스 토큰: 응답 본문으로 전달
- 리프레시 토큰: HTTP-only, Secure, SameSite=Strict 쿠키로 관리
- CORS 설정에서 credentials 허용 필요

### 이메일 인증 보안

- 인증 토큰: UUID 기반 랜덤 생성
- Redis TTL을 통한 토큰 만료 관리
- 인증 완료 후 즉시 토큰 무효화
- 비밀번호 암호화: AES-256 암호화로 Redis 저장, 인증 완료 시 복호화하여 Keycloak 전달

## 동시성 및 예외 처리

### 동시성 문제

- 같은 이메일로 동시 가입 시도: Redis 기반 중복 검증, 후진입 요청은 conflict 오류 반환
- 마이그레이션 중 동시 로그인: DB 락 또는 재시도 로직으로 처리

### 데이터 일관성

- 트랜잭션 경계: Keycloak 생성과 RDBMS 저장 간 분산 트랜잭션 관리
- 보상 트랜잭션: Keycloak 사용자 생성 성공 후 RDBMS 실패 시 Keycloak 사용자 삭제