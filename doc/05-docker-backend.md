# 로컬 빌드

## [Dockerfile.local](/Dockerfile.local)

## [application-local.yml](/src/main/resources/application-local.yml) 추가 설정

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate # (spring.jpa.generate-ddl 옵션 미사용)
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        type.descriptor.sql: trace
        jdbc.batch_size: 50
        order_inserts: true
        order_updates: true
    open-in-view: false # 트랜잭션 경계 설정
  data:
    web:
      pageable:
        default-page-size: 20  # 기본 페이지 사이즈
        max-page-size: 200 # 최대 페이지 사이즈값을 기본값과 동일하게
```

# 주요 설정 파일 추가

# 백엔드 도커 이미지 빌드 및 컨테이너 실행

```shell
# 도커 이미지 빌드
docker compose build backend 

# 도커 컨테이너 실행
docker compose up -d backend 

# 도커 컨테이너 프로세스 목록
docker compose ps

# 백엔드 로그 확인
docker compose logs -f backend
```

# 도커 컨테이너 로깅

- [application-local.yml](/src/main/resources/application-local.yml) 파일에 추가

```yaml
logging:
  level:
    root: INFO # 운영 WARN
    org.hibernate.SQL: DEBUG  # SQL 로그 레벨 / 운영 WARN
    org.hibernate.orm.jdbc.bind: TRACE  # 바인딩 파라미터 로그 레벨 / 운영 WARN
    kr.pincoin.api: DEBUG # 운영 INFO
    org.springframework.web: DEBUG
    org.springframework.jdbc.core.JdbcTemplate: DEBUG
    org.springframework.jdbc.core.StatementCreatorUtils: TRACE
  file:
    name: /app/logs/application.log
```

```shell
# 도커 백엔드 이미지 강제 빌드
docker compose build --no-cache backend

# 도커 백엔드 재시작
docker compose restart backend
```