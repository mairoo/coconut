package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.auth.utils.EmailUtils
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * 회원가입 프로세스 전체 조율 퍼사드
 *
 * 회원가입의 복잡한 2단계 프로세스를 관리하고 조율합니다.
 * 각 단계별로 필요한 하위 시스템들을 호출하여 전체 플로우를 완성합니다.
 *
 * **1단계 - 회원가입 요청 처리:**
 * - 무작위 공격 방어 (reCAPTCHA, IP 제한, 동시 요청 차단)
 * - 이메일 중복 검증 (사전 차단)
 * - 이메일 인증 발송
 * - 임시 데이터 저장
 *
 * **2단계 - 이메일 인증 완료:**
 * - 토큰 검증 및 임시 데이터 복원
 * - 이메일 중복 재검증 (방어적 프로그래밍)
 * - Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
 * - 데이터 정리 및 환영 이메일 발송
 *
 * **하위 시스템 협업:**
 * - SignUpValidator: 입력값 및 보안 검증
 * - signUpEmailService: 이메일 발송 관리
 * - SignUpDataManager: 임시 데이터 및 Redis 관리
 * - keycloakAuthService: Keycloak 연동
 * - UserResourceCoordinator: 분산 트랜잭션 관리
 */
@Component
class SignUpFacade(
    private val signUpValidator: SignUpValidator,
    private val signUpDataManager: SignUpDataManager,
    private val signUpEmailService: SignUpEmailService,
    private val userResourceCoordinator: UserResourceCoordinator,
    private val userService: UserService,
    private val emailUtils: EmailUtils,
    private val authProperties: AuthProperties,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 회원가입 요청 처리 - 1단계
     *
     * 사용자의 회원가입 요청을 받아 이메일 인증까지의 과정을 처리합니다.
     *
     * **처리 과정:**
     * 1. 무작위 회원가입 공격 대응
     *    - reCAPTCHA 검증
     *    - 이메일 도메인 검증 (일회용 이메일 서비스 등 차단)
     *    - 이메일 중복 검증 (사전 차단)
     *    - IP별 가입 빈도 제한 검증 (예: 3회/일)
     *    - 동시 가입 시도 방지 (이메일 기준)
     *
     * 2. 인증 이메일 발송
     *    - 이메일 검증 UUID 토큰 생성
     *    - Mailgun API를 통한 인증 이메일 발송
     *
     * 3. Redis에 임시 데이터 저장
     *    - 비밀번호 AES 암호화
     *    - 회원정보 + 클라이언트 정보 저장
     *    - TTL 설정 (24시간)
     *
     * 4. IP별 가입 횟수 증가
     *
     * @param request 회원가입 요청 정보 (email, username, firstName, lastName, password, recaptchaToken)
     * @param httpServletRequest HTTP 요청 정보 (IP, User-Agent, Accept-Language 추출용)
     * @return 이메일 인증 안내 응답 (마스킹된 이메일, 만료시간 포함)
     * @throws BusinessException 검증 실패, 이메일 발송 실패, 동시 요청 등의 경우
     */
    fun processSignUpRequest(
        request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ): SignUpRequestedResponse {
        return runBlocking {
            try {
                // 1. 무작위 회원 가입 공격 대응 + 이메일 중복 검증
                // 1-1. reCAPTCHA 검증
                // 1-2. 이메일 도메인 검증 (일회용 이메일 서비스 등 차단)
                // 1-3. IP별 가입 빈도 제한 검증 (예, 3회/일)
                // 1-4. 이메일 중복 검증 (이미 가입된 이메일 차단)
                // 1-5. 동시 가입 시도 방지 (이메일 기준) - Redis 기반 중복 검증, 후진입은 conflict 오류 반환
                signUpValidator.validateSignUpRequest(request, httpServletRequest)

                // 2. 인증 이메일 발송
                // 2-1. 이메일 검증 UUID 토큰 생성
                val verificationToken = UUID.randomUUID().toString()

                // 2-2. Keycloak 아닌 백엔드에서 Mailgun API로 이메일 인증 발송
                // - 인증 링크에 UUID 토큰 포함
                signUpEmailService.sendVerificationEmail(
                    request.email,
                    verificationToken,
                    httpServletRequest
                )

                // 3. Redis에 임시 데이터 저장
                // 3-1. 비밀번호 AES 암호화 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
                // 3-2. Redis에 임시 데이터 저장
                // - 입력받은 회원정보(email, username, firstname, lastname, password)
                // - TTL 설정 (예, 24시간)
                signUpDataManager.saveTemporaryData(verificationToken, request, httpServletRequest)

                // 4. IP별 가입 횟수 증가
                signUpDataManager.incrementIpSignupCount(httpServletRequest)

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
     * 이메일 인증 완료 시 회원 가입 완료 처리 - 2단계
     *
     * 사용자가 이메일 인증 링크를 클릭했을 때 회원가입을 완료합니다.
     *
     * **처리 과정:**
     * 1. Redis에서 임시 데이터 조회 및 검증
     *    - 토큰 유효성 확인
     *    - 비밀번호 복호화
     *    - SignUpRequest 객체 재구성
     *
     * 2. 이메일 중복 재검증 (방어적 프로그래밍)
     *    - 1단계와 2단계 사이의 시간차 동안 발생할 수 있는 중복 방지
     *    - Race Condition, 다중 채널 가입, 시스템 장애 등 대응
     *
     * 3. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
     *    - UserResourceCoordinator를 통한 안전한 사용자 생성
     *
     * 4. 후처리 작업
     *    - Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
     *    - 동시 가입 시도 방지 락 해제
     *    - 회원 가입 완료 안내 이메일 발송
     *
     * **인증 실패 처리:**
     * - Redis TTL을 통한 토큰 만료 관리 (암호화된 데이터 포함)
     * - 재가입 시 새로운 인증 프로세스 진행
     *
     * **데이터 일관성 보장:**
     * - 트랜잭션 경계: Keycloak 생성과 RDBMS 저장 간 분산 트랜잭션 관리
     * - 보상 트랜잭션: Keycloak 사용자 생성 성공 후 RDBMS 실패 시 Keycloak 사용자 삭제
     *
     * @param token 이메일 인증 토큰 (UUID)
     * @return 회원가입 완료 응답 (실제 이메일, 사용자명, 완료시간 포함)
     * @throws BusinessException 토큰 무효, 이메일 중복, Keycloak 연동 실패, DB 저장 실패 등의 경우
     */
    fun completeSignUp(token: String): SignUpCompletedResponse {
        return runBlocking {
            try {
                // 1. Redis에서 임시 데이터 조회
                val signupData = signUpDataManager.getAndValidateTemporaryData(token)

                // 2. 이메일 중복 재검증 (방어적 프로그래밍)
                validateEmailNotExistsForFinalCheck(signupData.email)

                // 3. 비밀번호 복호화
                val decryptedPassword = signUpDataManager.decryptPassword(signupData.encryptedPassword)

                // 4. SignUpRequest 객체 재구성
                val signUpRequest = SignUpRequest(
                    email = signupData.email,
                    username = signupData.username,
                    firstName = signupData.firstName,
                    lastName = signupData.lastName,
                    password = decryptedPassword,
                    recaptchaToken = null, // 이미 검증 완료
                )

                // 5. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
                userResourceCoordinator.createUserWithKeycloak(signUpRequest)

                // 6. Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
                // 7. 동시 가입 시도 방지 락 해제
                signUpDataManager.cleanupAfterSignUp(token, signupData.email)

                // 8. 회원 가입 완료 안내 이메일 발송
                signUpEmailService.sendWelcomeEmail(signupData.email, signupData.firstName)

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
     * 2단계 이메일 중복 재검증 (방어적 프로그래밍)
     *
     * 1단계에서 검증했지만, 1단계와 2단계 사이의 시간차 동안
     * 다른 경로로 동일 이메일이 가입되었을 가능성을 방어합니다.
     *
     * **검증이 필요한 시나리오:**
     * 1. Race Condition: 1단계 후 다른 세션에서 같은 이메일로 가입 완료
     * 2. 다중 채널: 웹/앱/관리자 등 다른 경로로 동시 가입
     * 3. 시스템 장애: 장애 복구 중 데이터 불일치 상황
     * 4. 장시간 지연: 사용자가 오래된 인증 링크 클릭
     *
     * **1단계 검증과의 차이점:**
     * - 1단계: 사용자 경험 개선용 (빠른 피드백, 불필요한 이메일 방지)
     * - 2단계: 데이터 일관성 보장용 (최종 안전장치)
     */
    private fun validateEmailNotExistsForFinalCheck(email: String) {
        try {
            userService.findUser(UserSearchCriteria(email = email, isActive = true))
            logger.warn { "2단계 중복 검증: 1단계 이후 가입된 이메일 발견 - email=$email" }
            throw BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS)
        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.EMAIL_ALREADY_EXISTS -> {
                    // 이메일 중복인 경우 그대로 전파
                    throw e
                }

                UserErrorCode.NOT_FOUND -> {
                    // User? 가 아닌 User 응답 또는 예외이므로 사용자가 없으면 정상 - 2단계 검증 통과
                    // 정상적으로 메서드 종료 (예외를 던지지 않음)
                }

                else -> {
                    // 다른 예상치 못한 에러는 시스템 에러로 처리
                    logger.error { "2단계 중복 검증 중 예기치 못한 오류: email=$email, error=${e.errorCode}" }
                    throw BusinessException(UserErrorCode.SYSTEM_ERROR)
                }
            }
        }
    }
}