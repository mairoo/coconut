# 초기 `SecurityConfig.kt`

```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val accessDeniedHandler: ApiAccessDeniedHandler,
    private val corsProperties: CorsProperties,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            // 1. 기본 설정
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .headers { headers ->
                headers.defaultsDisabled()
                headers.httpStrictTransportSecurity { hstsConfig ->
                    hstsConfig
                        .includeSubDomains(true)
                        .maxAgeInSeconds(31536000)
                        .preload(true)
                }
                headers.contentTypeOptions { }
                headers.cacheControl { }
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .rememberMe { it.disable() }
            .anonymous { it.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // 2. OAuth2 Resource Server 설정
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
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            // 4. 예외 처리
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            exposedHeaders = listOf("Set-Cookie")
            allowCredentials = true

            allowedOriginPatterns = corsProperties.allowedOrigins
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            allowedMethods = corsProperties.allowedMethods
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            allowedHeaders = corsProperties.allowedHeaders
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            maxAge = corsProperties.maxAge
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
```

## 주요 의존성

- `global.properties.CorsProperties`
- `global.responses.cursor.CursorResponse`
- `global.responses.error.ErrorResponse`
- `global.responses.page.PageResponse`
- `global.responses.success.ApiResponse`
- `global.error.ErrorCode`
- `global.error.CommonErrorCode`
- `global.security.error.AuthErrorCode`
- `global.security.handler.ApiAuthenticationEntryPoint`
- `global.security.handler.ApiAccessDeniedHandler`
