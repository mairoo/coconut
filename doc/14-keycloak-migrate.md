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