package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.auth.utils.EmailUtils
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 회원가입 검증 전담 서비스
 *
 * 회원가입 요청에 대한 모든 검증 로직을 담당합니다.
 * 보안성과 데이터 무결성을 보장하기 위해 다층적 검증을 수행합니다.
 *
 * **주요 검증 항목:**
 * 1. 무작위 공격 방어
 *    - reCAPTCHA v3 검증 (봇/스팸 차단)
 *    - 이메일 도메인 검증 (일회용 이메일 서비스 차단)
 *    - IP별 가입 빈도 제한 (브루트포스 공격 방어)
 *    - 동시 가입 시도 방지 (중복 요청 차단)
 *
 * 2. 비즈니스 규칙 검증
 *    - 이메일 중복 검사 (가입 전 사전 차단)
 *    - 이메일 형식 및 도메인 정책
 *    - 사용자명 중복 검사 (향후 구현)
 *    - 비밀번호 정책 준수 (향후 구현)
 *
 * **Redis 기반 제한 관리:**
 * - IP별 일일 가입 제한: {ip-limit-prefix}{IP} → 카운트
 * - 이메일별 동시 요청 락: {email-lock-prefix}{email} → "locked"
 * - TTL을 통한 자동 만료 관리
 */
@Component
class SignUpValidator(
    private val recaptchaService: RecaptchaService,
    private val emailUtils: EmailUtils,
    private val redisTemplate: RedisTemplate<String, String>,
    private val authProperties: AuthProperties,
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 회원가입 요청 전체 검증
     *
     * 회원가입 요청에 대한 모든 보안 및 비즈니스 검증을 수행합니다.
     * 하나라도 실패하면 BusinessException을 발생시켜 전체 프로세스를 중단합니다.
     *
     * **검증 순서:**
     * 1. reCAPTCHA 검증 (가장 먼저 봇 차단)
     * 2. 이메일 도메인 검증 (허용되지 않은 도메인 차단)
     * 3. 이메일 중복 검증 (이미 가입된 이메일 차단) ← 추가
     * 4. IP별 가입 빈도 제한 검증 (브루트포스 공격 방어)
     * 5. 동시 가입 시도 방지 (같은 이메일로 중복 요청 차단)
     *
     * @param request 회원가입 요청 정보
     * @param httpServletRequest HTTP 요청 정보 (클라이언트 정보 추출용)
     * @throws BusinessException 검증 실패 시 적절한 에러 코드와 함께 발생
     */
    suspend fun validateSignUpRequest(
        request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ) {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

        // 1. 무작위 회원 가입 공격 대응
        // 1-1. reCAPTCHA 검증
        validateRecaptcha(request.recaptchaToken)

        // 1-2. 이메일 도메인 검증 (일회용 이메일 서비스 등 차단)
        validateEmailDomain(request.email)

        // 1-3. IP별 가입 빈도 제한 검증 (예, 3회/일)
        validateIpSignupLimit(clientInfo.ipAddress)

        // 1-4. 이메일 중복 검증 (이미 가입된 이메일 차단)
        validateEmailNotExists(request.email)

        // 1-5. 동시 가입 시도 방지 (이메일 기준) - Redis 기반 중복 검증, 후진입은 conflict 오류 반환
        preventConcurrentSignup(request.email)
    }

    /**
     * 1-1. reCAPTCHA 검증
     *
     * Google reCAPTCHA v3를 사용하여 봇과 스팸을 차단합니다.
     * 최소 점수 이상의 신뢰도를 가진 요청만 허용합니다.
     *
     * **검증 과정:**
     * 1. 토큰 존재 여부 확인
     * 2. reCAPTCHA 서비스 호출
     * 3. 최소 점수(기본 0.7) 이상인지 확인
     *
     * @param recaptchaToken 클라이언트에서 전송된 reCAPTCHA 토큰
     * @param minScore 최소 허용 점수 (0.0~1.0, 기본값 0.7)
     * @throws BusinessException 토큰 누락 또는 검증 실패 시
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
     *
     * 허용되지 않은 이메일 도메인을 차단합니다.
     * 일회용 이메일 서비스, 스팸 도메인 등을 필터링합니다.
     *
     * **검증 대상:**
     * - 일회용 이메일 서비스 (10minutemail, tempmail 등)
     * - 알려진 스팸 도메인
     * - 비즈니스 정책상 제외된 도메인
     *
     * @param email 검증할 이메일 주소
     * @throws BusinessException 허용되지 않은 도메인인 경우
     */
    private fun validateEmailDomain(email: String) {
        if (!emailUtils.isAllowedDomain(email)) {
            logger.warn { "허용되지 않은 이메일 도메인: $email" }
            throw BusinessException(UserErrorCode.EMAIL_DOMAIN_NOT_ALLOWED)
        }
    }

    /**
     * 1-3. IP별 가입 빈도 제한 검증
     *
     * 동일 IP에서의 과도한 가입 시도를 차단합니다.
     * 브루트포스 공격이나 대량 계정 생성을 방지합니다.
     *
     * **제한 정책:**
     * - 기본값: IP당 일일 3회 가입 제한
     * - Redis 키: {ip-limit-prefix}{IP}
     * - TTL: 24시간 (일일 리셋)
     *
     * @param ip 클라이언트 IP 주소
     * @throws BusinessException 일일 가입 제한을 초과한 경우
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
     * 1-4. 이메일 중복 검증
     */
    private fun validateEmailNotExists(email: String) {
        try {
            userService.findUser(UserSearchCriteria(email = email, isActive = true))
            logger.warn { "이미 가입된 이메일로 회원가입 시도: $email" }
            throw BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS)
        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.EMAIL_ALREADY_EXISTS -> {
                    // 이메일 중복인 경우 그대로 전파
                    throw e
                }
                UserErrorCode.NOT_FOUND -> {
                    // User? 가 아닌 User 응답 또는 예외이기 때문에 사용자가 없으면 정상 - 회원가입 진행 가능
                    logger.debug { "이메일 중복 검증 통과: $email" }
                    // 정상적으로 메서드 종료 (예외를 던지지 않음)
                }
                else -> {
                    // 다른 에러는 시스템 에러로 처리
                    logger.error(e) { "이메일 중복 검증 중 예기치 못한 오류: email=$email, error=${e.errorCode}" }
                    throw BusinessException(UserErrorCode.SYSTEM_ERROR)
                }
            }
        }
    }

    /**
     * 1-5. 동시 가입 시도 방지
     *
     * 같은 이메일로 동시에 여러 가입 요청이 들어오는 것을 방지합니다.
     * Redis 기반 분산 락을 사용하여 동시성 문제를 해결합니다.
     *
     * **동작 원리:**
     * 1. 이메일별 락 키 생성: {email-lock-prefix}{email}
     * 2. Redis SETNX로 원자적 락 획득 시도
     * 3. 락 획득 실패 시 동시 요청으로 판단하여 차단
     * 4. 락 TTL: 설정된 시간 후 자동 해제
     *
     * **동시성 시나리오:**
     * - 첫 번째 요청: 락 획득 성공 → 프로세스 진행
     * - 두 번째 요청: 락 획득 실패 → SIGNUP_IN_PROGRESS 오류
     *
     * @param email 가입하려는 이메일 주소
     * @throws BusinessException 동일 이메일로 이미 가입 진행 중인 경우
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
}