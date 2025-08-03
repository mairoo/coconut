package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakAdminService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Component

/**
 * Keycloak 사용자 관리 전담 서비스
 *
 * Keycloak과의 모든 사용자 관련 연동을 담당합니다.
 * 인증 서버와의 안전하고 효율적인 통신을 보장하며,
 * 토큰 관리, 사용자 생성, 인증 등의 핵심 기능을 제공합니다.
 *
 * **주요 책임:**
 * 1. Admin 토큰 관리
 *    - Keycloak Admin API 호출을 위한 토큰 획득
 *    - 토큰 갱신 및 만료 처리
 *    - 권한 검증 및 오류 처리
 *
 * 2. 사용자 인증 및 관리
 *    - 사용자 로그인 처리 (향후 구현)
 *    - JWT 토큰 발급 및 갱신 (향후 구현)
 *    - 사용자 세션 관리 (향후 구현)
 *
 * 3. 레거시 마이그레이션 지원
 *    - 기존 사용자의 Keycloak 이전 (향후 구현)
 *    - 패스워드 검증 및 동기화 (향후 구현)
 *
 * **설계 원칙:**
 * - Keycloak 응답 형태(Success/Error) 일관성 유지
 * - 적절한 예외 처리 및 로깅
 * - 재사용 가능한 토큰 관리
 * - 확장 가능한 구조 (로그인, 토큰 갱신 등)
 */
@Component
class AuthKeycloakService(
    private val keycloakAdminService: KeycloakAdminService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Admin 토큰 획득
     *
     * Keycloak Admin API를 호출하기 위한 관리자 토큰을 획득합니다.
     * 사용자 생성, 수정, 삭제 등 관리 작업에 필요합니다.
     *
     * **사용 시나리오:**
     * - 회원가입 시 새 사용자 생성
     * - 사용자 정보 수정
     * - 계정 상태 변경 (활성화/비활성화)
     * - 역할 및 권한 관리
     *
     * **토큰 특징:**
     * - 높은 권한을 가진 관리자 토큰
     * - 제한된 시간 동안 유효 (일반적으로 5-10분)
     * - 민감한 작업이므로 로깅 시 토큰 값 노출 금지
     *
     * **오류 처리:**
     * - Keycloak 서버 연결 실패
     * - 인증 정보 불일치
     * - 네트워크 타임아웃
     * - 권한 부족
     *
     * @return Keycloak Admin API 호출용 액세스 토큰
     * @throws BusinessException Keycloak 연동 실패 시 적절한 에러 코드와 함께 발생
     */
    suspend fun getAdminToken(): String {
        return when (val result = keycloakAdminService.getAdminToken()) {
            is KeycloakResponse.Success -> {
                result.data.accessToken
            }

            is KeycloakResponse.Error -> {
                logger.error {
                    "Admin 토큰 획득 실패 - 코드: ${result.errorCode}, 메시지: ${result.errorMessage}"
                }
                throw BusinessException(KeycloakErrorCode.ADMIN_TOKEN_FAILED)
            }
        }
    }

    /**
     * 사용자 정보 조회
     *
     * TODO: 사용자 정보 조회 기능 구현 예정
     */
    /*
    suspend fun getUserInfo(keycloakUserId: String): UserInfoResponse {
        // 구현 예정
    }
    */

    /**
     * 사용자 정보 수정
     *
     * TODO: 사용자 정보 수정 기능 구현 예정
     */
    /*
    suspend fun updateUserInfo(
        keycloakUserId: String,
        updateRequest: UpdateUserRequest
    ) {
        // 구현 예정
    }
    */

    /**
     * 비밀번호 변경
     *
     * TODO: 비밀번호 변경 기능 구현 예정
     */
    /*
    suspend fun changePassword(
        keycloakUserId: String,
        currentPassword: String,
        newPassword: String
    ) {
        // 구현 예정
    }
    */

    /**
     * 계정 상태 변경
     *
     * TODO: 계정 활성화/비활성화 기능 구현 예정
     */
    /*
    suspend fun setUserEnabled(keycloakUserId: String, enabled: Boolean) {
        // 구현 예정
    }
    */
}