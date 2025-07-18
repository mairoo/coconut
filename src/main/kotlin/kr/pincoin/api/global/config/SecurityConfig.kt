package kr.pincoin.api.global.config

import kr.pincoin.api.global.properties.CorsProperties
import kr.pincoin.api.global.properties.KeycloakProperties
import kr.pincoin.api.global.security.encoder.DjangoPasswordEncoder
import kr.pincoin.api.global.security.filter.JwtAuthenticationFilter
import kr.pincoin.api.global.security.handler.ApiAuthenticationEntryPoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
    private val corsProperties: CorsProperties,
    private val keycloakProperties: KeycloakProperties,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // 순서 중요: 기본 설정 → OAuth2 설정 (조건부) → 권한 설정 → 필터 → 예외 처리 → 빌드

        // 1. 기본 설정
        val httpSecurity = http
            .cors { cors ->
                // CORS(Cross-Origin Resource Sharing) 설정
                cors.configurationSource(corsConfigurationSource())
            }
            .headers { headers ->
                // 기본 보안 헤더를 비활성화하고 필요한 것만 직접 설정
                headers.defaultsDisabled()

                // HTTPS 보안 정책 설정
                headers.httpStrictTransportSecurity { hstsConfig ->
                    hstsConfig
                        .includeSubDomains(true) // 서브도메인에도 HTTPS 적용
                        .maxAgeInSeconds(31536000) // HSTS 유효기간 1년
                        .preload(true) // 브라우저의 HSTS 프리로드 목록에 포함
                }

                // Content-Type 헤더를 브라우저가 임의로 변경하는 것을 방지
                headers.contentTypeOptions { }
                // 브라우저 캐시 제어 헤더 설정
                headers.cacheControl { }
            }
            .csrf { it.disable() } // CSRF 보안 비활성화 (REST API이므로 불필요)
            .formLogin { it.disable() } // 폼 로그인 비활성화
            .httpBasic { it.disable() } // HTTP Basic 인증 비활성화
            .rememberMe { it.disable() } // Remember-Me 기능 비활성화
            .anonymous { it.disable() } // 익명 사용자 기능 비활성화
            .sessionManagement { session ->
                // JWT 사용을 위한 세션리스 정책 설정
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        // 2. OAuth2 설정
        val finalHttpSecurity = if (keycloakProperties.enabled) {
            httpSecurity
                .oauth2ResourceServer { oauth2 ->
                    oauth2.jwt { jwt ->
                        jwt.decoder(jwtDecoder())
                    }
                }
                .oauth2Client {
                    // OAuth2 클라이언트 설정은 application.yml에서 처리
                }
        } else {
            httpSecurity
        }

        return finalHttpSecurity
            // 3. 권한 설정
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/actuator/health",
                        "/actuator/prometheus",
                        "/actuator/info"
                    ).permitAll()
                    .requestMatchers("/actuator/**").denyAll()
                    .requestMatchers(
                        "/auth/**",
                        "/oauth2/**",
                        "/open/**",
                        "/webhooks/**",
                        "/health",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            // 4. 필터 설정
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            // 5. 예외 처리
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 시 처리
                    .accessDeniedHandler(accessDeniedHandler) // 인가 실패 시 처리
            }
            .build()
    }

    @Bean
    @ConditionalOnProperty(name = ["keycloak.enabled"], havingValue = "true")
    fun jwtDecoder(): JwtDecoder {
        val jwkSetUri =
            "${keycloakProperties.serverUrl}/realms/${keycloakProperties.realm}/protocol/openid-connect/certs"
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            exposedHeaders = listOf("Set-Cookie") // 클라이언트에 노출할 헤더
            allowCredentials = true // 인증 정보 포함 허용

            // allowedOrigins와 allowedOriginPatterns는 동시 사용 불가
            // 와일드카드 패턴 허용
            allowedOriginPatterns = corsProperties.allowedOrigins
                .split(",")
                .map { it.trim() }
                .map { origin ->
                    if (origin.contains("*")) origin else origin
                }
            allowedMethods = corsProperties.allowedMethods.split(",") // 허용할 HTTP 메서드
            allowedHeaders = corsProperties.allowedHeaders.split(",") // 허용할 헤더
            maxAge = corsProperties.maxAge // 프리플라이트 요청의 캐시 시간
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    // 비밀번호 암호화 알고리즘: 디폴트 - BCryptPasswordEncoder
    @Bean
    fun passwordEncoder(): PasswordEncoder = DjangoPasswordEncoder()
}