package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.auth.utils.CryptoUtils
import kr.pincoin.api.domain.auth.utils.EmailUtils
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakAdminService
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTokenService
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.external.notification.mailgun.api.request.MailgunRequest
import kr.pincoin.api.external.notification.mailgun.service.MailgunApiClient
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userResourceCoordinator: UserResourceCoordinator,
    private val keycloakAdminService: KeycloakAdminService,
    private val keycloakTokenService: KeycloakTokenService,
    private val mailgunApiClient: MailgunApiClient,
    private val recaptchaService: RecaptchaService,
    private val redisTemplate: RedisTemplate<String, String>,
    private val cryptoUtils: CryptoUtils,
    private val emailUtils: EmailUtils,
    private val authProperties: AuthProperties,
) {
    private val logger = KotlinLogging.logger {}

    // 1-1. 회원가입 폼 처리
    /**
     * 회원가입 임시 저장 및 이메일 인증 발송
     */
    fun signUp(
        request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ): SignUpRequestedResponse {
        return runBlocking {
            try {
                val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

                // 1. 무작위 회원 가입 공격 대응
                // 1-1. reCAPTCHA 검증
                validateRecaptcha(request.recaptchaToken)

                // 1-2. 이메일 도메인 검증 (일회용 이메일 서비스 등 차단)
                validateEmailDomain(request.email)

                // 1-3. IP별 가입 빈도 제한 검증 (예, 3회/일)
                validateIpSignupLimit(clientInfo.ipAddress)

                // 1-4. 동시 가입 시도 방지 (이메일 기준) - Redis 기반 중복 검증, 후진입은 conflict 오류 반환
                preventConcurrentSignup(request.email)

                // 2. 인증 이메일 발송
                // 2-1. 이메일 검증 UUID 토큰 생성
                val verificationToken = UUID.randomUUID().toString()

                // 2-2. Keycloak 아닌 백엔드에서 Mailgun API로 이메일 인증 발송
                // - 인증 링크에 UUID 토큰 포함
                sendVerificationEmail(request.email, verificationToken, httpServletRequest)

                // 3. Redis에 임시 데이터 저장
                // 3-1. 비밀번호 AES 암호화 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
                val encryptedPassword = cryptoUtils.encrypt(request.password)

                // 3-2. Redis에 임시 데이터 저장
                // - 입력받은 회원정보(email, username, firstname, lastname, password)
                // - TTL 설정 (예, 24시간)
                saveTemporarySignupData(verificationToken, request.copy(password = encryptedPassword), clientInfo)

                // 4. IP별 가입 횟수 증가
                incrementIpSignupCount(clientInfo.ipAddress)

                SignUpRequestedResponse(
                    message = "인증 이메일이 발송되었습니다. 이메일을 확인해주세요.",
                    maskedEmail = emailUtils.maskEmail(request.email),
                    expiresAt = LocalDateTime.now().plus(authProperties.signup.limits.verificationTtl),
                )
            } catch (e: BusinessException) {
                logger.error { "회원가입 오류: email=${request.email}, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "회원가입 예기치 못한 오류: email=${request.email}, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * 1-2. 이메일 인증 완료 시 회원 가입 완료 처리
     * 인증 실패 처리
     * - Redis TTL을 통한 토큰 만료 관리 (암호화된 데이터 포함)
     * - 재가입 시 새로운 인증 프로세스 진행
     * 데이터 일관성 보장
     * - 트랜잭션 경계: Keycloak 생성과 RDBMS 저장 간 분산 트랜잭션 관리
     * - 보상 트랜잭션: Keycloak 사용자 생성 성공 후 RDBMS 실패 시 Keycloak 사용자 삭제
     */
    fun verifyEmailAndCompleteSignup(token: String): SignUpCompletedResponse {
        return runBlocking {
            try {
                // 1. Redis에서 임시 데이터 조회
                val signupData = getTemporarySignupData(token)
                    ?: throw BusinessException(UserErrorCode.VERIFICATION_TOKEN_INVALID)

                // 2. 비밀번호 복호화
                val decryptedPassword = cryptoUtils.decrypt(signupData.encryptedPassword)

                // 3. SignUpRequest 객체 재구성
                val signUpRequest = SignUpRequest(
                    email = signupData.email,
                    username = signupData.username,
                    firstName = signupData.firstName,
                    lastName = signupData.lastName,
                    password = decryptedPassword,
                    recaptchaToken = null // 이미 검증 완료
                )

                // 4. Admin 토큰 획득
                val adminToken = getAdminToken()

                // 5. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
                userResourceCoordinator.createUserWithKeycloak(signUpRequest, adminToken)

                // 6. Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
                deleteTemporarySignupData(token)

                // 7. 동시 가입 시도 방지 락 해제
                releaseEmailLock(signupData.email)

                // 8. 회원 가입 완료 안내 이메일 발송
                sendWelcomeEmail(signupData.email, signupData.firstName)

                SignUpCompletedResponse(
                    message = "회원가입이 성공적으로 완료되었습니다.",
                    email = signupData.email,
                    username = signupData.username,
                    completedAt = LocalDateTime.now(),
                )

            } catch (e: BusinessException) {
                logger.error { "이메일 인증 완료 처리 오류: token=$token, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "이메일 인증 완료 예기치 못한 오류: token=$token, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * Redis에서 임시 회원가입 데이터 조회
     */
    private fun getTemporarySignupData(token: String): TemporarySignupData? {
        val key = "${authProperties.signup.redis.signupPrefix}$token"
        val jsonData = redisTemplate.opsForValue().get(key) ?: return null

        return try {
            val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            val dataMap = mapper.readValue(jsonData, Map::class.java)

            TemporarySignupData(
                email = dataMap["email"] as String,
                username = dataMap["username"] as String,
                firstName = dataMap["firstName"] as String,
                lastName = dataMap["lastName"] as String,
                encryptedPassword = dataMap["encryptedPassword"] as String,
                createdAt = dataMap["createdAt"] as String,
                ipAddress = dataMap["ipAddress"] as String,
                userAgent = dataMap["userAgent"] as String,
                acceptLanguage = dataMap["acceptLanguage"] as String
            )
        } catch (e: Exception) {
            logger.error { "임시 데이터 파싱 오류: token=$token, error=${e.message}" }
            null
        }
    }

    /**
     * Redis에서 임시 회원가입 데이터 삭제
     */
    private fun deleteTemporarySignupData(token: String) {
        val key = "${authProperties.signup.redis.signupPrefix}$token"
        redisTemplate.delete(key)
    }

    /**
     * 이메일 락 해제
     */
    private fun releaseEmailLock(email: String) {
        val lockKey = "${authProperties.signup.redis.emailLockPrefix}$email"
        redisTemplate.delete(lockKey)
    }

    /**
     * 회원가입 완료 환영 이메일 발송
     */
    private suspend fun sendWelcomeEmail(email: String, firstName: String) {
        val emailContent = buildWelcomeEmailContent(firstName)

        val mailgunRequest = MailgunRequest(
            to = email,
            subject = "회원가입을 축하합니다!",
            text = emailContent.text,
            html = emailContent.html,
        )

        try {
            mailgunApiClient.sendEmail(mailgunRequest).block()
        } catch (e: Exception) {
            logger.warn { "환영 이메일 발송 실패: email=$email, error=${e.message}" }
            // 환영 이메일 실패는 회원가입 완료를 방해하지 않음
        }
    }

    /**
     * 환영 이메일 콘텐츠 생성
     */
    private fun buildWelcomeEmailContent(firstName: String): EmailContent {
        val text = """
        안녕하세요 ${firstName}님!
        
        회원가입이 성공적으로 완료되었습니다.
        이제 모든 서비스를 이용하실 수 있습니다.
        
        궁금한 사항이 있으시면 언제든지 문의해 주세요.
        
        감사합니다.
    """.trimIndent()

        val html = """
        <html>
        <body>
            <h2>회원가입 완료!</h2>
            <p>안녕하세요 <strong>${firstName}</strong>님!</p>
            <p>회원가입이 성공적으로 완료되었습니다.</p>
            <p>이제 모든 서비스를 이용하실 수 있습니다.</p>
            <hr>
            <p>궁금한 사항이 있으시면 언제든지 문의해 주세요.</p>
            <p>감사합니다.</p>
        </body>
        </html>
    """.trimIndent()

        return EmailContent(text, html)
    }


    /**
     * Admin 토큰 획득
     */
    private suspend fun getAdminToken(): String {
        return when (val result = keycloakAdminService.getAdminToken()) {
            is KeycloakResponse.Success -> {
                result.data.accessToken
            }

            is KeycloakResponse.Error -> {
                throw BusinessException(KeycloakErrorCode.ADMIN_TOKEN_FAILED)
            }
        }
    }

    /**
     * 1-1. reCAPTCHA 검증
     */
    private suspend fun validateRecaptcha(
        recaptchaToken: String?,
        minScore: Double = 0.7,
    ) {
        // 토큰 존재 여부 확인
        if (recaptchaToken.isNullOrBlank()) {
            logger.warn { "reCAPTCHA 토큰이 제공되지 않음" }
            throw BusinessException(UserErrorCode.RECAPTCHA_TOKEN_REQUIRED)
        }

        // reCAPTCHA 검증 수행
        val result = recaptchaService.verifyV3(recaptchaToken, minScore)

        if (result is RecaptchaResponse.Error) {
            logger.warn { "reCAPTCHA 검증 실패 - 코드: ${result.errorCode}, 메시지: ${result.errorMessage}" }
            throw BusinessException(UserErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        // 성공 시 아무것도 하지 않음
    }

    /**
     * 1-2. 이메일 도메인 검증
     */
    private fun validateEmailDomain(email: String) {
        if (!emailUtils.isAllowedDomain(email)) {
            logger.warn { "허용되지 않은 이메일 도메인: $email" }
            throw BusinessException(UserErrorCode.EMAIL_DOMAIN_NOT_ALLOWED)
        }
    }

    /**
     * 1-3. IP별 가입 빈도 제한 검증
     */
    private fun validateIpSignupLimit(ip: String) {
        val key = "${authProperties.signup.redis.ipLimitPrefix}$ip"
        val currentCount = redisTemplate.opsForValue().get(key)?.toIntOrNull() ?: 0

        if (currentCount >= authProperties.signup.limits.maxDailySignupsPerIp) {
            logger.warn { "IP별 일일 가입 제한 초과: ip=$ip, count=$currentCount" }
            throw BusinessException(UserErrorCode.DAILY_SIGNUP_LIMIT_EXCEEDED)
        }
    }

    /**
     * 1-4. 동시 가입 시도 방지
     */
    private fun preventConcurrentSignup(email: String) {
        val lockKey = "${authProperties.signup.redis.emailLockPrefix}$email"
        val lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(
                lockKey,
                "locked",
                authProperties.signup.limits.emailLockDuration.toMinutes(),
                TimeUnit.MINUTES
            ) ?: false

        if (!lockAcquired) {
            logger.warn { "동시 가입 시도 차단: email=$email" }
            throw BusinessException(UserErrorCode.SIGNUP_IN_PROGRESS)
        }
    }

    /**
     * 2-2. 인증 이메일 발송
     */
    private suspend fun sendVerificationEmail(
        email: String,
        token: String,
        httpServletRequest: HttpServletRequest,
    ) {
        val verificationUrl = buildVerificationUrl(token, httpServletRequest)
        val emailContent = buildVerificationEmailContent(verificationUrl)

        val mailgunRequest = MailgunRequest(
            to = email,
            subject = "이메일 인증을 완료해주세요",
            text = emailContent.text,
            html = emailContent.html,
        )

        try {
            mailgunApiClient.sendEmail(mailgunRequest).block()
        } catch (e: Exception) {
            logger.error { "인증 이메일 발송 실패: email=$email, error=${e.message}" }
            throw BusinessException(UserErrorCode.EMAIL_SEND_FAILED)
        }
    }

    /**
     * 3-2. Redis에 임시 회원가입 데이터 저장
     */
    private fun saveTemporarySignupData(
        token: String,
        request: SignUpRequest,
        clientInfo: ClientUtils.ClientInfo
    ) {
        val key = "${authProperties.signup.redis.signupPrefix}$token"
        val signupData = mapOf(
            "email" to request.email,
            "username" to request.username,
            "firstName" to request.firstName,
            "lastName" to request.lastName,
            "encryptedPassword" to request.password,
            "createdAt" to LocalDateTime.now().toString(),
            "ipAddress" to clientInfo.ipAddress,
            "userAgent" to clientInfo.userAgent,
            "acceptLanguage" to clientInfo.acceptLanguage,
        )

        val jsonData = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(signupData)
        redisTemplate.opsForValue().set(
            key,
            jsonData,
            authProperties.signup.limits.verificationTtl.toHours(),
            TimeUnit.HOURS
        )
    }

    /**
     * 4. IP별 가입 횟수 증가
     */
    private fun incrementIpSignupCount(ip: String) {
        val key = "${authProperties.signup.redis.ipLimitPrefix}$ip"
        redisTemplate.opsForValue().increment(key)
        redisTemplate.expire(key, authProperties.signup.limits.ipLimitResetDuration.toHours(), TimeUnit.HOURS)
    }

    /**
     * 인증 URL 생성
     */
    private fun buildVerificationUrl(
        token: String,
        httpServletRequest: HttpServletRequest,
    ): String {
        val scheme = if (httpServletRequest.isSecure) "https" else "http"
        val domain = DomainUtils.getRequestDomain(httpServletRequest)
        return "$scheme://$domain/auth/verify-email/$token"
    }

    /**
     * 인증 이메일 콘텐츠 생성
     */
    private fun buildVerificationEmailContent(
        verificationUrl: String,
    ): EmailContent {
        val ttlHours = authProperties.signup.limits.verificationTtl.toHours()

        val text = """
            안녕하세요!
            
            회원가입을 완료하려면 아래 링크를 클릭해주세요:
            $verificationUrl
            
            이 링크는 ${ttlHours}시간 후에 만료됩니다.
            
            만약 회원가입을 신청하지 않으셨다면 이 이메일을 무시해주세요.
        """.trimIndent()

        val html = """
            <html>
            <body>
                <h2>이메일 인증</h2>
                <p>안녕하세요!</p>
                <p>회원가입을 완료하려면 아래 버튼을 클릭해주세요:</p>
                <a href="$verificationUrl" 
                   style="background-color: #007bff; color: white; padding: 10px 20px; 
                          text-decoration: none; border-radius: 5px; display: inline-block;">
                    이메일 인증하기
                </a>
                <p>또는 다음 링크를 복사하여 브라우저에 붙여넣기 하세요:</p>
                <p><a href="$verificationUrl">$verificationUrl</a></p>
                <p><small>이 링크는 ${ttlHours}시간 후에 만료됩니다.</small></p>
                <hr>
                <p><small>만약 회원가입을 신청하지 않으셨다면 이 이메일을 무시해주세요.</small></p>
            </body>
            </html>
        """.trimIndent()

        return EmailContent(text, html)
    }

    /**
     * 이메일 콘텐츠 데이터 클래스
     */
    private data class EmailContent(
        val text: String,
        val html: String
    )


    /**
     * 임시 회원가입 데이터 클래스
     */
    private data class TemporarySignupData(
        val email: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val encryptedPassword: String,
        val createdAt: String,
        val ipAddress: String,
        val userAgent: String,
        val acceptLanguage: String
    )
}