package kr.pincoin.api.external.auth.keycloak.converter

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

/**
 * Keycloak JWT를 Spring Security 인증 토큰으로 변환하는 컨버터
 *
 * ## 필요성
 * - Keycloak의 JWT 구조와 Spring Security의 인증 토큰 구조가 다르므로 중간 변환 계층이 필요
 * - 외부 시스템(Keycloak)과 내부 시스템(Spring Security) 간의 호환성을 제공
 * - JWT의 클레임 정보를 기반으로 애플리케이션별 권한을 동적으로 부여
 * - 일반 사용자와 Bot Service Account를 구분하여 각각에 적합한 권한 매핑 수행
 *
 * ## 주요 역할
 * 1. **사용자 식별**: JWT의 preferred_username, email, subject 클레임에서 사용자 정보 추출
 * 2. **인증 토큰 타입 구분**: 일반 사용자 토큰 vs Service Account 토큰 구분
 * 3. **권한 매핑**:
 *    - 일반 사용자: 데이터베이스에서 사용자 역할 정보를 조회하여 권한 부여
 *    - Service Account: clientId 기반으로 사전 정의된 Bot 권한 부여
 * 4. **인증 토큰 생성**: JwtAuthenticationToken 객체로 변환하여 Spring Security Context에서 사용 가능하도록 함
 * 5. **예외 처리**: 사용자 조회 실패나 알 수 없는 Service Account 시 기본 권한 부여하여 시스템 안정성 보장
 *
 * ## 적용된 패턴
 * - **Adapter Pattern**: Keycloak JWT 구조를 Spring Security가 요구하는 인터페이스로 적응
 *   - Target Interface: AbstractAuthenticationToken (Spring Security가 기대하는 인터페이스)
 *   - Adaptee: Jwt (Keycloak이 제공하는 JWT 객체)
 *   - Adapter: KeycloakJwtAuthenticationConverter (두 시스템을 연결하는 어댑터)
 * - **Strategy Pattern**: 일반 사용자와 Service Account에 대해 서로 다른 권한 부여 전략 적용
 * - **Template Method Pattern**: Spring의 Converter<Jwt, AbstractAuthenticationToken> 인터페이스 구현
 *
 * ## 동작 흐름
 * ```
 * 1. JwtDecoder가 검증한 JWT 토큰을 입력으로 받음
 * 2. JWT 클레임을 분석하여 Service Account인지 일반 사용자인지 구분
 *    - Service Account 판별 조건: typ="Bearer" && preferred_username.endsWith("-service-account")
 * 3-A. 일반 사용자의 경우:
 *      - JWT 클레임에서 사용자 이메일 추출 (우선순위: preferred_username > email > subject)
 *      - 이메일을 기반으로 데이터베이스에서 사용자 정보와 역할 조회
 *      - 조회된 역할을 Spring Security의 GrantedAuthority로 변환
 * 3-B. Service Account의 경우:
 *      - JWT의 azp(Authorized Party) 클레임에서 clientId 추출
 *      - clientId를 기반으로 사전 정의된 Bot 권한 매핑
 * 4. JwtAuthenticationToken 생성하여 반환 (JWT 원본 + 권한 + 사용자명/clientId 포함)
 * ```
 *
 * ## 권한 부여 전략
 *
 * ### 일반 사용자
 * - **성공 시**: 데이터베이스의 실제 사용자 역할을 권한으로 부여 (ROLE_USER, ROLE_ADMIN)
 * - **사용자 없음**: ROLE_USER 기본 권한 부여 (신규 사용자 고려)
 * - **조회 실패**: ROLE_USER 기본 권한 부여 (시스템 안정성 우선)
 *
 * ### Service Account (Bot)
 * - **api-bot-service**: ROLE_BOT + ROLE_API_READ + ROLE_API_WRITE (데이터 처리 Bot)
 * - **monitoring-bot**: ROLE_BOT + ROLE_API_READ (모니터링 전용 Bot)
 * - **admin-bot**: ROLE_BOT + ROLE_ADMIN (관리 작업 Bot)
 * - **기타**: ROLE_BOT 기본 권한 (알 수 없는 Bot)
 *
 * ## Keycloak Service Account 설정 가이드
 * ```
 * Clients → Create Client
 * ├── Client ID: api-bot-service (또는 monitoring-bot, admin-bot)
 * ├── Client Authentication: ON
 * ├── Service Accounts Roles: ON
 * ├── Authentication flow: Service account roles만 체크
 * └── Valid Redirect URIs: (비워둠, service account이므로 불필요)
 * ```
 *
 * ## 주의사항
 * - Service Account Roles는 자동으로 JWT에 포함되지 않으므로 clientId 기반 매핑 사용
 * - Bot 권한은 코드에 하드코딩되어 있어 새로운 Bot 추가 시 getServiceAccountAuthorities() 메서드 수정 필요
 * - 보안을 위해 알 수 없는 clientId는 최소 권한(ROLE_BOT)만 부여
 * - Service Account 토큰 발급 시 client_credentials grant type 사용 필수
 *
 * ## 사용 예시
 * ```kotlin
 * // Controller에서 권한 체크
 * @PreAuthorize("hasRole('USER')") // 일반 사용자
 * fun userApi() { }
 *
 * @PreAuthorize("hasRole('BOT') and hasRole('API_WRITE')") // Bot 전용
 * fun botApi() { }
 *
 * @PreAuthorize("hasRole('ADMIN')") // 관리자나 admin-bot
 * fun adminApi() { }
 * ```
 */
@Component
class KeycloakJwtAuthenticationConverter(
    private val userRepository: UserRepository,
) : Converter<Jwt, AbstractAuthenticationToken> {
    private val logger = KotlinLogging.logger {}

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val email = jwt.getClaimAsString("preferred_username")
            ?: jwt.getClaimAsString("email")
            ?: jwt.subject

        val clientId = jwt.getClaimAsString("azp") // Authorized party (client)

        val authorities = if (isServiceAccount(jwt)) {
            getServiceAccountAuthorities(clientId)
        } else {
            getUserAuthorities(email)
        }

        return JwtAuthenticationToken(jwt, authorities, email ?: clientId)
    }

    /**
     * JWT 토큰이 Service Account(Bot)인지 판별
     *
     * ## 판별 조건
     * - typ 클레임이 "Bearer"
     * - preferred_username이 "-service-account"로 종료
     *
     * ## 토큰 예시
     *
     * ### 일반 사용자 토큰 (false 반환)
     * ```json
     * {
     *   "typ": "Bearer",
     *   "preferred_username": "user@example.com",
     *   "email": "user@example.com",
     *   "azp": "frontend-app"
     * }
     * ```
     *
     * ### Service Account 토큰 (true 반환)
     * ```json
     * {
     *   "typ": "Bearer",
     *   "preferred_username": "api-bot-service-service-account",
     *   "azp": "api-bot-service"
     * }
     * ```
     */
    private fun isServiceAccount(
        jwt: Jwt,
    ): Boolean =
        jwt.getClaimAsString("typ") == "Bearer" &&
                jwt.getClaimAsString("preferred_username")?.endsWith("-service-account") == true

    private fun getServiceAccountAuthorities(
        clientId: String?,
    ): Collection<GrantedAuthority> =
        when (clientId) {
            "api-bot-service" -> listOf(
                SimpleGrantedAuthority("ROLE_BOT"),
                SimpleGrantedAuthority("ROLE_API_READ"),
                SimpleGrantedAuthority("ROLE_API_WRITE")
            )

            "monitoring-bot" -> listOf(
                SimpleGrantedAuthority("ROLE_BOT"),
                SimpleGrantedAuthority("ROLE_API_READ")
            )

            "admin-bot" -> listOf(
                SimpleGrantedAuthority("ROLE_BOT"),
                SimpleGrantedAuthority("ROLE_ADMIN")
            )

            else -> listOf(SimpleGrantedAuthority("ROLE_BOT"))
        }

    private fun getUserAuthorities(email: String?): Collection<GrantedAuthority> {
        if (email.isNullOrBlank()) {
            logger.warn { "JWT에서 이메일을 찾을 수 없음" }
            return listOf(SimpleGrantedAuthority("ROLE_USER"))
        }

        return try {
            val user = userRepository.findUser(
                UserSearchCriteria(email = email, isActive = true)
            )

            if (user != null) {
                // 단일 역할
                val role = if (user.isSuperuser) "ROLE_ADMIN" else "ROLE_USER"
                val authority = SimpleGrantedAuthority(role)
                listOf(authority)

                // 다중 역할
                /**
                 * val authorities = user.roles.map { role ->
                 *   SimpleGrantedAuthority(role.name)
                 * }
                 * authorities
                 */
            } else {
                logger.warn { "사용자를 찾을 수 없음: email=$email" }
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            }
        } catch (e: Exception) {
            logger.error(e) { "권한 조회 중 오류 발생: email=$email" }
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        }
    }
}