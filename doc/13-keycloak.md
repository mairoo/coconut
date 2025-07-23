# 아키텍처 비교

## Keycloak OAuth2 방식

```
클라이언트 → Keycloak → 애플리케이션
(OAuth2)    (JWT 검증)
```

- 외부 인증 서버: Keycloak이 별도의 인증 서버 역할
- 표준 프로토콜: OAuth2/OpenID Connect 표준 준수
- 중앙 집중식: 여러 애플리케이션의 인증을 중앙에서 관리

장점

- 표준 준수: OAuth2/OpenID Connect 표준 프로토콜 사용
- 중앙 집중 관리: 여러 애플리케이션의 인증을 중앙에서 관리
- SSO 지원: Single Sign-On 기능 제공
- 확장성: 마이크로서비스 아키텍처에 적합
- 보안: 전문적인 인증 서버의 보안 기능 활용
- 사용자 관리: Keycloak 관리 콘솔을 통한 편리한 사용자 관리

단점

- 복잡성: 추가적인 인프라 구성 필요
- 외부 의존성: Keycloak 서버 의존
- 네트워크 오버헤드: 외부 서비스와의 통신
- 학습 곡선: OAuth2/OpenID Connect 이해 필요

## 기존 JWT 스프링 시큐리티 방식

```
클라이언트 → 애플리케이션 (자체 JWT 생성/검증)
```

- 자체 인증: 애플리케이션이 직접 JWT 생성 및 검증
- 독립적: 외부 의존성 없이 자체적으로 인증 처리
- 단일 애플리케이션: 주로 단일 애플리케이션 환경에 적합

장점

- 단순성: 애플리케이션 내에서 모든 인증 로직 처리
- 독립성: 외부 인증 서버 불필요
- 성능: 네트워크 호출 없이 로컬 처리
- 제어: 세밀한 커스터마이징 가능
- 도메인 통합: 비즈니스 로직과 밀접한 연동

단점

- 확장성 제한: 다중 애플리케이션 환경에서 복잡
- 보안 책임: 모든 보안 로직을 직접 구현해야 함
- 표준화 부족: 커스텀 구현으로 인한 일관성 문제
- SSO 어려움: 여러 애플리케이션 간 SSO 구현 복잡

# 구현 특징 비교

## `KeycloakJwtAuthenticationConverter`

특징:

- JWT는 Keycloak에서 생성되어 전달됨
- 애플리케이션은 JWT 검증 및 권한 매핑만 수행
- 사용자 정보는 JWT 클레임에서 추출
- 스프링 시큐리티의 OAuth2 Resource Server 설정을 통한 자동 JWT 검증

- 단수 역할 부여

```kotlin
val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))

if (user != null) {
    val role = if (user.isSuperuser) "ROLE_ADMIN" else "ROLE_USER"
    val authority = SimpleGrantedAuthority(role)
    listOf(authority)
} else {
    listOf(SimpleGrantedAuthority("ROLE_USER"))
}
```

- 복수 역할 부여

```kotlin
val user = userRepository.findUserWithRoles(UserSearchCriteria(email = email, isActive = true))

if (user != null) {
    val authorities = user.roles.map { role ->
        SimpleGrantedAuthority(role.name)
    }
    authorities
} else {
    listOf(SimpleGrantedAuthority("ROLE_MEMBER"))
}
```

## 기존 스프링시큐리티 UserDetails 기반 로그인 처리 비교

특징:

- 커스텀 OncePerRequestFilter로 구현
- 애플리케이션이 직접 JWT 생성, 검증, 파싱 수행
- UserDetailsService를 통한 데이터베이스 조회
- 공개 엔드포인트 필터링으로 성능 최적화
- 상세한 예외 처리 및 로깅

### `UserDetailsAdapter`

**`User` 도메인 객체를 스프링시큐리티의 `UserDetails`로 변환하는 어댑터**

- 도메인 모델(User)이 외부 프레임워크(UserDetails)에 의존하지 않도록 분리
- 도메인 계층 User -> 어댑터(단방형 mapper) -> UserDetails
- 단순 인터페이스 변환만 수행하며 비즈니스 로직은 포함하지 않음

```kotlin
data class UserDetailsAdapter(
    val user: User,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> =
        user.roles.map { SimpleGrantedAuthority(it.toString()) }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    // UserDetails 인터페이스의 나머지 메서드들은 기본값 true 반환
    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
```

### `UserDetailsServiceAdapter`

**`UserRepository`를 스프링시큐리티의 `UserDetailsService`로 변환하는 어댑터**

- 도메인 계층 UserRepository.findByEmail -> 어댑터(단방향 mapper) -> UserDetailsService.loadUserByUsername
- 메서드명과 반환 타입의 불일치 해결:
- findByEmail -> loadUserByUsername
- Optional<User> -> UserDetails

```kotlin
@Service
@Transactional(readOnly = true) // 조회 전용 트랜잭션 적용
class UserDetailsServiceAdapter(
    private val userRepository: UserRepository,
) : UserDetailsService {
    private val log = KotlinLogging.logger {}

    override fun loadUserByUsername(
        email: String,
    ): UserDetails =
        UserDetailsAdapter(
            userRepository.findUser(UserSearchCriteria(email = email, userIsActive = true))
                ?: throw UsernameNotFoundException(AuthErrorCode.INVALID_CREDENTIALS.message)
                    .also { log.error { "이메일 없음: $email" } })
}
```

### `JwtAuthenticationFilter`

의존성 주입

- UserDetailsService: 의존성 주입 시 `UserDetailsServiceAdapter` 구현체가 주입

```kotlin
@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val bearerToken = request.getBearerToken()

            // JWT 토큰 유효성 확인 및 username 추출 후 인증 처리
            bearerToken?.let {
                jwtTokenProvider.validateToken(it)
                    ?.let { username -> authenticateUser(username, request) }
            }

            filterChain.doFilter(request, response)
        } catch (_: JwtAuthenticationException) {
            SecurityContextHolder.clearContext()
            handleAuthenticationException(request, response)
        } catch (_: Exception) {
            SecurityContextHolder.clearContext()
            handleAuthenticationException(
                request,
                response
            )
        }
    }

    private fun authenticateUser(username: String, request: HttpServletRequest) {
        try {
            // 1. 데이터베이스에서 username 조회
            val userDetails = userDetailsService.loadUserByUsername(username)

            // 2. 인증 객체 생성
            val auth: Authentication = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )

            // 3. WebAuthenticationDetails, WebAuthenticationDetailsSource 저장
            // WebAuthenticationDetails 객체는 인증 요청과 관련된 웹 관련 정보
            // - RemoteAddress (클라이언트 IP 주소)
            // - SessionId (현재 세션 ID)
            // setDetails()의 활용 용도
            // - 특정 IP 주소나 지역에서의 접근 제한
            // - 세션 기반의 추가적인 보안 검증
            // - 사용자 행동 분석 및 로깅
            // - 감사(audit) 기록 생성
            // - 다중 요소 인증(MFA) 구현
            // - IP 기반 접근 제한이나 차단
            // authentication.setDetails(new
            // WebAuthenticationDetailsSource().buildDetails(request));

            // 4. 현재 인증된 사용자 정보를 보안 컨텍스트에 저장 = 로그인 처리
            SecurityContextHolder.getContext().authentication = auth
        } catch (_: UsernameNotFoundException) {
            // 인증 실패 시 상위 예외 핸들러에서 로깅하므로 예외만 변환하여 던짐
            throw JwtAuthenticationException(AuthErrorCode.INVALID_CREDENTIALS)
        }
    }
}
```

# 사용자 감사 기능 추가

`UserAuditorAware` 추가

```kotlin
@Component
class UserAuditorAware(
    private val userRepository: UserRepository
) : AuditorAware<Long> {
    private val logger = KotlinLogging.logger {}

    override fun getCurrentAuditor(): Optional<Long> {
        return try {
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication?.isAuthenticated != true) {
                return Optional.empty()
            }

            when (val principal = authentication.principal) {
                is Jwt -> {
                    // JWT에서 사용자 정보 추출
                    val email = principal.getClaimAsString("preferred_username")
                        ?: principal.getClaimAsString("email")
                        ?: principal.subject

                    if (email.isNullOrBlank()) {
                        logger.warn { "JWT에서 사용자 식별 정보를 찾을 수 없음" }
                        return Optional.empty()
                    }

                    // 사용자 조회
                    val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))

                    if (user?.id != null) {
                        Optional.of(user.id)
                    } else {
                        logger.warn { "감사 로그용 사용자 조회 실패: email=$email" }
                        Optional.empty()
                    }
                }

                else -> {
                    logger.warn { "알 수 없는 Principal 타입: ${principal?.javaClass?.name}" }
                    Optional.empty()
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "현재 사용자 정보 조회 중 오류 발생" }
            Optional.empty()
        }
    }
}
```