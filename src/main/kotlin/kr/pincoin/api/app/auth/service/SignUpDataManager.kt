package kr.pincoin.api.app.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.auth.utils.CryptoUtils
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * 회원가입 데이터 관리 전담 서비스
 *
 * 회원가입 프로세스에서 발생하는 모든 데이터 관리를 담당합니다.
 * Redis를 활용한 임시 데이터 저장, 암호화/복호화, 제한 관리 등을 수행합니다.
 *
 * **주요 책임:**
 * 1. 임시 회원가입 데이터 관리
 *    - AES 암호화를 통한 안전한 비밀번호 저장
 *    - JSON 직렬화를 통한 구조화된 데이터 저장
 *    - TTL을 통한 자동 만료 관리
 *
 * 2. Redis 기반 제한 관리
 *    - IP별 가입 횟수 추적 및 증가
 *    - 이메일별 동시 요청 락 관리
 *    - 자동 만료를 통한 메모리 효율성
 *
 * 3. 데이터 정리 및 후처리
 *    - 회원가입 완료 후 임시 데이터 즉시 삭제
 *    - 락 해제를 통한 리소스 정리
 *
 * **Redis 키 구조:**
 * - 임시 데이터: {signup-prefix}{token} → JSON 데이터
 * - IP 제한: {ip-limit-prefix}{IP} → 카운트
 * - 이메일 락: {email-lock-prefix}{email} → "locked"
 */
@Component
class SignUpDataManager(
    private val redisTemplate: RedisTemplate<String, String>,
    private val cryptoUtils: CryptoUtils,
    private val authProperties: AuthProperties,
    private val objectMapper: ObjectMapper,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 3-2. Redis에 임시 회원가입 데이터 저장
     *
     * 이메일 인증 완료까지 필요한 회원가입 정보를 안전하게 저장합니다.
     * 비밀번호는 AES 암호화하여 저장하고, 클라이언트 정보도 함께 보관합니다.
     *
     * **저장 데이터:**
     * - 사용자 정보: email, username, firstName, lastName
     * - 암호화된 비밀번호: AES 암호화 적용
     * - 클라이언트 정보: IP, User-Agent, Accept-Language
     * - 메타데이터: 생성시간
     *
     * **보안 고려사항:**
     * - 비밀번호 AES 암호화 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
     * - TTL 설정을 통한 자동 만료 (예, 24시간)
     * - JSON 직렬화를 통한 구조화된 저장
     *
     * @param token 이메일 인증 토큰 (UUID)
     * @param request 회원가입 요청 정보 (이미 암호화된 비밀번호 포함)
     * @param httpServletRequest HTTP 요청 정보 (클라이언트 정보 추출용)
     */
    fun saveTemporaryData(
        token: String,
        request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ) {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

        // 3-1. 비밀번호 AES 암호화 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
        val encryptedPassword = cryptoUtils.encrypt(request.password)

        val key = "${authProperties.signup.redis.signupPrefix}$token"
        val signupData = mapOf(
            "email" to request.email,
            "username" to request.username,
            "firstName" to request.firstName,
            "lastName" to request.lastName,
            "encryptedPassword" to encryptedPassword,
            "createdAt" to LocalDateTime.now().toString(),
            "ipAddress" to clientInfo.ipAddress,
            "userAgent" to clientInfo.userAgent,
            "acceptLanguage" to clientInfo.acceptLanguage,
        )

        val jsonData = objectMapper.writeValueAsString(signupData)

        // 3-2. Redis에 임시 데이터 저장
        // - 입력받은 회원정보(email, username, firstname, lastname, password)
        // - TTL 설정 (예, 24시간)
        redisTemplate.opsForValue().set(
            key,
            jsonData,
            authProperties.signup.limits.verificationTtl.toHours(),
            TimeUnit.HOURS
        )
    }

    /**
     * 임시 데이터 조회 및 검증
     *
     * 이메일 인증 토큰을 통해 임시 저장된 회원가입 데이터를 조회합니다.
     * 토큰 유효성과 데이터 무결성을 검증합니다.
     *
     * @param token 이메일 인증 토큰
     * @return 임시 회원가입 데이터
     * @throws BusinessException 토큰이 무효하거나 데이터 파싱 실패 시
     */
    fun getAndValidateTemporaryData(token: String): TemporarySignupData {
        return getTemporarySignupData(token)
            ?: throw BusinessException(UserErrorCode.VERIFICATION_TOKEN_INVALID)
    }

    /**
     * 비밀번호 복호화
     *
     * AES 암호화된 비밀번호를 원본으로 복호화합니다.
     *
     * @param encryptedPassword 암호화된 비밀번호
     * @return 복호화된 원본 비밀번호
     */
    fun decryptPassword(encryptedPassword: String): String {
        return cryptoUtils.decrypt(encryptedPassword)
    }

    /**
     * 회원가입 완료 후 정리
     *
     * 회원가입이 성공적으로 완료된 후 관련 데이터를 정리합니다.
     * 보안상 즉시 삭제하여 민감 정보 노출을 방지합니다.
     *
     * **정리 작업:**
     * 1. Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
     * 2. 동시 가입 시도 방지 락 해제
     *
     * @param token 이메일 인증 토큰
     * @param email 가입 완료된 이메일 주소
     */
    fun cleanupAfterSignUp(token: String, email: String) {
        // 6. Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
        deleteTemporarySignupData(token)

        // 7. 동시 가입 시도 방지 락 해제
        releaseEmailLock(email)
    }

    /**
     * 4. IP별 가입 횟수 증가
     *
     * 성공적으로 이메일 발송이 완료된 후 IP별 가입 횟수를 증가시킵니다.
     * 일일 제한 관리를 위한 카운터를 유지합니다.
     *
     * **동작 방식:**
     * 1. IP별 카운터 증가 (Redis INCR)
     * 2. 첫 가입 시 TTL 설정 (24시간 후 자동 리셋)
     * 3. 기존 가입 시 TTL 연장하지 않음 (일일 리셋 유지)
     *
     * @param httpServletRequest HTTP 요청 정보 (IP 추출용)
     */
    fun incrementIpSignupCount(httpServletRequest: HttpServletRequest) {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)
        val key = "${authProperties.signup.redis.ipLimitPrefix}${clientInfo.ipAddress}"

        redisTemplate.opsForValue().increment(key)
        redisTemplate.expire(key, authProperties.signup.limits.ipLimitResetDuration.toHours(), TimeUnit.HOURS)
    }

    /**
     * Redis에서 임시 회원가입 데이터 조회
     *
     * 토큰을 키로 사용하여 저장된 JSON 데이터를 조회하고 파싱합니다.
     *
     * @param token 이메일 인증 토큰
     * @return 파싱된 임시 회원가입 데이터, 토큰이 무효하거나 만료된 경우 null
     */
    private fun getTemporarySignupData(token: String): TemporarySignupData? {
        val key = "${authProperties.signup.redis.signupPrefix}$token"
        val jsonData = redisTemplate.opsForValue().get(key) ?: return null

        return try {
            val dataMap = objectMapper.readValue(jsonData, Map::class.java)

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
     *
     * 회원가입 완료 또는 토큰 무효화 시 호출됩니다.
     * 민감한 정보가 포함되어 있으므로 즉시 삭제가 중요합니다.
     *
     * @param token 이메일 인증 토큰
     */
    private fun deleteTemporarySignupData(token: String) {
        val key = "${authProperties.signup.redis.signupPrefix}$token"
        redisTemplate.delete(key)
    }

    /**
     * 이메일 락 해제
     *
     * 동시 가입 시도 방지를 위해 설정된 락을 해제합니다.
     * 회원가입 완료 또는 실패 시 호출되어야 합니다.
     *
     * @param email 락 해제할 이메일 주소
     */
    private fun releaseEmailLock(email: String) {
        val lockKey = "${authProperties.signup.redis.emailLockPrefix}$email"
        redisTemplate.delete(lockKey)
    }

    /**
     * 임시 회원가입 데이터 클래스
     *
     * Redis에서 조회한 JSON 데이터를 매핑하는 데이터 클래스입니다.
     * 회원가입 2단계에서 사용자 생성에 필요한 모든 정보를 포함합니다.
     */
    data class TemporarySignupData(
        val email: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val encryptedPassword: String,
        val createdAt: String,
        val ipAddress: String,
        val userAgent: String,
        val acceptLanguage: String,
    )
}