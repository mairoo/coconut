package kr.pincoin.api.domain.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "auth")
data class AuthProperties(
    val crypto: CryptoProperties = CryptoProperties(),
    val email: EmailProperties = EmailProperties(),
    val signup: SignupProperties = SignupProperties(),
    val oauth2: OAuth2Properties = OAuth2Properties(),
) {
    data class CryptoProperties(
        val secretKey: String = ""
    )

    data class EmailProperties(
        val allowedDomains: List<String> = emptyList(),
        val blockedDomains: List<String> = emptyList()
    ) {
        /**
         * 이메일 도메인이 허용되는지 확인
         */
        fun isDomainAllowed(domain: String): Boolean {
            // 차단된 도메인인지 먼저 확인
            if (blockedDomains.contains(domain.lowercase())) {
                return false
            }

            // 허용 도메인 리스트가 비어있으면 모든 도메인 허용
            if (allowedDomains.isEmpty()) {
                return true
            }

            // 허용된 도메인인지 확인
            return allowedDomains.contains(domain.lowercase())
        }

        /**
         * 이메일 주소가 유효한지 확인
         */
        fun isEmailAllowed(email: String): Boolean {
            val domain = email.substringAfterLast("@", "")
            return if (domain.isBlank()) {
                false
            } else {
                isDomainAllowed(domain)
            }
        }
    }

    data class SignupProperties(
        val redis: SignupRedisProperties = SignupRedisProperties(),
        val limits: SignupLimitsProperties = SignupLimitsProperties(),
    ) {
        data class SignupRedisProperties(
            val signupPrefix: String = "signup:",
            val ipLimitPrefix: String = "signup_ip:",
            val emailLockPrefix: String = "signup_lock:",
        )

        data class SignupLimitsProperties(
            val verificationTtl: Duration = Duration.ofHours(24),
            val maxDailySignupsPerIp: Int = 3,
            val emailLockDuration: Duration = Duration.ofMinutes(5),
            val ipLimitResetDuration: Duration = Duration.ofHours(24),
        )
    }

    data class OAuth2Properties(
        val redis: OAuth2RedisProperties = OAuth2RedisProperties(),
        val stateTtl: Duration = Duration.ofMinutes(10),
    ) {
        data class OAuth2RedisProperties(
            val statePrefix: String = "oauth2:state:",
        )
    }
}