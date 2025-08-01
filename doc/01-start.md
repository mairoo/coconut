# 스프링부트/코틀린 프로젝트 생성

## https://start.spring.io/

- 의존성 추가 없이 빈 프로젝트

- Project: Gradle- Kotlin
- Language: Kotlin
- Project Metadata
    - Group: kr.pincoin
    - Artifact: api
    - Name: api
    - Description
    - Package name: kr.pincoin.api
    - Packaging: Jar
    - Java: 24

## IntelliJ 프로젝트 이름 변경

- File → Project Structure > Project Settings > Project > Name (기본값: 패키지 이름의 마지막 폴더 이름)

## ignore 파일 추가

- [.gitignore](/.gitignore)
- [.dockerignore](/.dockerignore)

## git 저장소 연결

```
cd repo
git init
```

원격 저장소 생성 후 연결

# 빌드 및 실행 설정

## [application-local.yml](/src/main/resources/application-local.yml) 파일 추가

- 실행 프로파일 변경: local

```yaml
spring:
  application:
    name: pincoin
```

## 최초 빌드 실행

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.3)
```