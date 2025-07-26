package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakAdminService
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTokenService
import kr.pincoin.api.external.notification.mailgun.service.MailgunApiClient
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userResourceCoordinator: UserResourceCoordinator,
    private val keycloakAdminService: KeycloakAdminService,
    private val keycloakTokenService: KeycloakTokenService,
    private val mailgunApiClient: MailgunApiClient,
) {
    private val logger = KotlinLogging.logger {}

    // 1-1. 회원가입 폼 처리
    /**
     * 회원가입 임시 저장 및 이메일 인증 발송
     *
     * **1단계: 회원가입 정보 임시 저장**
     * - 입력받은 회원정보(email, username, firstname, lastname, password) Redis에 임시 저장
     * - 비밀번호는 AES-256 암호화하여 저장 (스프링부트 백엔드 application.yaml 정의 암호화 키 사용)
     * - UUID 기반 랜덤 인증 토큰 생성 및 Redis에 매핑 저장
     * - TTL 설정 (예: 24시간)
     *
     * **2단계: 이메일 인증 발송**
     * - Keycloak이 아닌 백엔드에서 사용자 이메일로 인증 링크 발송
     * - 인증 링크에 UUID 토큰 포함
     *
     * **이메일 무작위 회원 가입 공격 대응**
     * - reCAPTCHA 검증
     * - 이메일 도메인 화이트리스트: 일회용 이메일 서비스 차단
     * - 가입 빈도 제한: IP당 일일 가입 횟수 제한 (예: 3회/일)
     *
     * **동시성 처리**
     * - 같은 이메일로 동시 가입 시도: Redis 기반 중복 검증
     * - 후진입 요청은 conflict 오류 반환
     *
     * @param signUpRequest 회원가입 요청 정보 (email, username, firstname, lastname, password, recaptchaToken)
     * @return 임시 저장 성공 응답
     */
    fun createUser(request: SignUpRequest): User {
        return runBlocking {
            try {
                // 1. reCAPTCHA 검증 (localhost에서는 생략)

                // 2. 이메일 도메인 검증

                // 3. IP별 가입 빈도 제한 검증

                // 4. 동시 가입 시도 방지 (이메일 기준)

                // 5. 이메일 검증 UUID 토큰 생성

                // 6. 비밀번호 AES 암호화

                // 7. Redis에 임시 데이터 저장

                // 8. 이메일 인증 발송

                // 9. IP별 가입 횟수 증가

                val adminToken = getAdminToken()
                userResourceCoordinator.createUserWithKeycloak(request, adminToken)
            } catch (e: BusinessException) {
                logger.error { "회원가입 비즈니스 오류: email=${request.email}, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "회원가입 시스템 오류: email=${request.email}, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * 1-2. 이메일 인증 완료 시 회원 가입 완료 처리
     *
     * **3단계: 이메일 인증 완료 처리**
     * - UUID 인증 토큰으로 Redis에서 회원정보 조회
     * - AES-256 암호화된 비밀번호를 복호화
     * - Keycloak에 사용자 생성 (복호화된 원본 비밀번호 사용)
     * - RDBMS User 테이블에 사용자 정보 저장 (keycloak_id 포함)
     * - Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
     *
     * **4단계: 인증 실패 처리**
     * - Redis TTL을 통한 토큰 만료 관리 (암호화된 데이터 포함)
     * - 재가입 시 새로운 인증 프로세스 진행
     *
     * **데이터 일관성 보장**
     * - 트랜잭션 경계: Keycloak 생성과 RDBMS 저장 간 분산 트랜잭션 관리
     * - 보상 트랜잭션: Keycloak 사용자 생성 성공 후 RDBMS 실패 시 Keycloak 사용자 삭제
     *
     * @param token UUID 기반 이메일 인증 토큰
     * @return 회원가입 완료 응답
     */

    // 이메일 인증 완료 처리
    // 1. Redis에서 임시 데이터 조회
    // 2. 비밀번호 복호화
    // 3. SignUpRequest 객체 재구성
    // 4. Keycloak과 DB에 사용자 생성 (분산 트랜잭션)
    // 5. Redis에서 임시 데이터 즉시 삭제 (토큰 무효화)
    // 6. 회원 가입 완료 안내 이메일 발송

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
}