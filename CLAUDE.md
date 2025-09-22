# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고할 가이드입니다.

## 개발 명령어

### 빌드 및 테스트
- **빌드**: `./gradlew build`
- **테스트**: `./gradlew test`
- **애플리케이션 실행**: `./gradlew bootRun`
- **JAR 생성**: `./gradlew bootJar` (버전 태그 없이 `api.jar` 생성)

### 개발 도구
- **개발 프로필 실행**: 로컬 개발시 `application-local.yml` 사용
- **데이터베이스**: PostgreSQL with Hibernate validate 모드
- **캐시**: Redis를 세션 관리 및 Rate Limiting에 사용

## 아키텍처 개요

이 프로젝트는 **Kotlin 1.9.25**와 **Java 21**을 사용하는 **Spring Boot 3.5.3** 백엔드 API로, **도메인 주도 설계(DDD)** 원칙과 **헥사고날 아키텍처**를 따라 구조화되어 있습니다.

### 핵심 아키텍처 레이어

1. **도메인 레이어** (`src/main/kotlin/kr/pincoin/api/domain/`)
   - 비즈니스 로직, 도메인 모델, 리포지토리 인터페이스 포함
   - 주요 도메인: `user`, `auth`, `order`, `inventory`, `social`, `support`
   - 각 도메인은 `model/`, `repository/`, `service/` 패키지 구조

2. **인프라스트럭처 레이어** (`src/main/kotlin/kr/pincoin/api/infra/`)
   - JPA 엔티티, 리포지토리 구현체, 외부 서비스 어댑터
   - 복잡한 쿼리를 위해 QueryDSL 사용, 커스텀 `QueryRepository` 구현
   - 도메인 모델과 JPA 엔티티 간 변환을 위한 데이터베이스 매퍼

3. **애플리케이션 레이어** (`src/main/kotlin/kr/pincoin/api/app/`)
   - REST 컨트롤러, 요청/응답 DTO, 애플리케이션 서비스
   - 도메인별 및 접근 레벨별 구성: `admin/`, `member/`, `my/`, `open/`
   - 복잡한 작업을 위해 여러 도메인 서비스를 조정하는 파사드

4. **외부 레이어** (`src/main/kotlin/kr/pincoin/api/external/`)
   - 서드파티 연동: Telegram 알림, AWS S3 등

### 주요 아키텍처 패턴

- **Repository 패턴**: 도메인 리포지토리는 인터페이스, 인프라스트럭처 레이어에서 구현
- **Mapper 패턴**: 도메인 모델과 JPA 엔티티 간 변환을 위한 별도 매퍼
- **Facade 패턴**: 여러 도메인 서비스를 조정하는 애플리케이션 파사드
- **Service 레이어**: 도메인 서비스는 비즈니스 로직, 애플리케이션 서비스는 유스케이스 처리

### 기술 스택

- **프레임워크**: Spring Boot 3.5.3 with Spring Security, Spring Data JPA
- **데이터베이스**: PostgreSQL with QueryDSL 5.1.0 (타입 안전 쿼리)
- **캐시**: Redis (세션 관리 및 Rate Limiting)
- **보안**: OAuth2 Resource Server with Keycloak 연동
- **외부 서비스**: AWS S3, Mailgun, Telegram, Google reCAPTCHA
- **테스트**: JUnit 5 with Spring Boot Test
- **모니터링**: Actuator with Prometheus metrics

### 보안 기능

포괄적인 보안 조치가 구현되어 있습니다:
- **인증**: Keycloak을 통한 OAuth2, Google OTP 2단계 인증
- **Rate Limiting**: IP 기반 속도 제한, 실패 시 점진적 지연
- **계정 보호**: 계정 잠금 정책 (5회 실패 → 15분 잠금)
- **이메일 보안**: 도메인 화이트리스트/블랙리스트, 일회용 이메일 차단
- **디바이스 핑거프린팅**: 알려지지 않은 디바이스 감지
- **reCAPTCHA**: v2/v3 통합으로 봇 차단
- **WAF 보호**: Cloudflare WAF with IP/이메일 블랙리스트

### 데이터베이스 설계

- **주 데이터베이스**: PostgreSQL with 'Asia/Seoul' 타임존
- **커넥션 풀**: HikariCP with 배치 작업 활성화
- **스키마 관리**: Hibernate validate 모드 (자동 DDL 없음)
- **쿼리**: 복잡한 쿼리는 QueryDSL, 단순 CRUD는 JPA
- **페이지네이션**: 기본 20개 항목, 최대 200개

### 개발 가이드라인

- **프로필**: 개발용 `local`, 운영용 `prod` 프로필 사용
- **로깅**: 환경별 다른 로그 레벨 (개발: DEBUG, 운영: WARN)
- **도커**: 컨테이너 배포를 위한 Dockerfile.local, Dockerfile.prod 포함
- **Git**: 컨벤셔널 커밋 메시지, 피처 브랜치 사용
- **테스트**: 서비스 단위 테스트, 리포지토리 통합 테스트 작성

### 외부 연동

- **AWS S3**: 접근 레벨별 파일 업로드/다운로드 (admin/member/my)
- **Keycloak**: 렐름 기반 OAuth2 인증
- **Mailgun**: 알림 및 인증을 위한 이메일 서비스
- **Telegram**: 에러 알림 및 경고
- **reCAPTCHA**: 폼 및 인증 봇 차단

### 설정 참고사항

- **CORS**: 로컬호스트 개발용으로 설정
- **Redis**: 회원가입 Rate Limiting 및 세션 관리에 사용
- **Time Zone**: 모든 데이터베이스 연결은 Asia/Seoul 타임존 사용
- **JWT**: Keycloak이 토큰 생성 및 검증 처리
- **페이지네이션**: 모든 리스트 엔드포인트에서 일관된 페이지네이션