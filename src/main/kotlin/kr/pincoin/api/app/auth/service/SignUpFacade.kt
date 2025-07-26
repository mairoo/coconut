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
import kr.pincoin.api.global.exception.BusinessException
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
 * - 이메일 인증 발송
 * - 임시 데이터 저장
 *
 * **2단계 - 이메일 인증 완료:**
 * - 토큰 검증 및 임시 데이터 복원
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
    private val authKeycloakService: AuthKeycloakService,
    private val userResourceCoordinator: UserResourceCoordinator,
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
                // 1. 무작위 회원 가입 공격 대응
                // 1-1. reCAPTCHA 검증
                // 1-2. 이메일 도메인 검증 (일회용 이메일 서비스 등 차단)
                // 1-3. IP별 가입 빈도 제한 검증 (예, 3회/일)
                // 1-4. 동시 가입 시도 방지 (이메일 기준) - Redis 기반 중복 검증, 후진입은 conflict 오류 반환
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
     * 2. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
     *    - Admin 토큰 획득
     *    - UserResourceCoordinator를 통한 안전한 사용자 생성
     *
     * 3. 후처리 작업
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
     * @throws BusinessException 토큰 무효, Keycloak 연동 실패, DB 저장 실패 등의 경우
     */
    fun completeSignUp(token: String): SignUpCompletedResponse {
        return runBlocking {
            try {
                // 1. Redis에서 임시 데이터 조회
                val signupData = signUpDataManager.getAndValidateTemporaryData(token)

                // 2. 비밀번호 복호화
                val decryptedPassword = signUpDataManager.decryptPassword(signupData.encryptedPassword)

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
                val adminToken = authKeycloakService.getAdminToken()

                // 5. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
                userResourceCoordinator.createUserWithKeycloak(signUpRequest, adminToken)

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
}