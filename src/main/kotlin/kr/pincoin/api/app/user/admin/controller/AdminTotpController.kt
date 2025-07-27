package kr.pincoin.api.app.user.admin.controller

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.user.common.response.TotpStatusResponse
import kr.pincoin.api.domain.coordinator.user.TotpResourceCoordinator
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * 관리자용 TOTP 2FA 관리 컨트롤러
 *
 * 관리자가 다른 사용자의 2FA를 관리할 수 있는 API를 제공합니다.
 * 모든 API는 ADMIN 권한이 필요하며, 보안 감사 로그가 기록됩니다.
 */
@RestController
@RequestMapping("/admin/totp")
@PreAuthorize("hasRole('ADMIN')")
class AdminTotpController(
    private val totpResourceCoordinator: TotpResourceCoordinator
) {

    /**
     * 특정 사용자의 2FA 상태 조회
     *
     * 관리자가 특정 사용자의 2FA 활성화 상태를 확인합니다.
     * 보안 감사, 지원 업무, 정책 준수 확인 등에 활용됩니다.
     *
     * **사용 시나리오:**
     * - 보안 정책 준수 확인 (특권 사용자 2FA 필수 여부)
     * - 사용자 지원 요청 시 상태 확인
     * - 보안 감사 및 리포팅
     *
     * @param targetUserEmail 조회할 사용자의 이메일
     * @return 해당 사용자의 2FA 상태 정보
     */
    @GetMapping("/users/{targetUserEmail}/status")
    suspend fun getUserTotpStatus(
        @PathVariable targetUserEmail: String
    ): ResponseEntity<ApiResponse<TotpStatusResponse>> =
        totpResourceCoordinator.getTotpStatus(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 특정 사용자에게 2FA 설정 강제
     *
     * 관리자가 특정 사용자에게 2FA 설정을 강제합니다.
     * 대상 사용자는 다음 로그인 시 반드시 2FA를 설정해야 합니다.
     *
     * **동작 방식:**
     * 1. 대상 사용자의 Keycloak 계정에 "CONFIGURE_TOTP" 필수 액션 추가
     * 2. 해당 사용자가 다음 로그인 시 Keycloak에서 2FA 설정 화면 강제 표시
     * 3. 사용자가 2FA 설정을 완료해야만 로그인 및 서비스 이용 가능
     *
     * **적용 대상:**
     * - 관리자 계정
     * - 특권 사용자 계정
     * - 보안 정책에 따른 필수 대상자
     * - 보안 사고 발생 시 임시 조치 대상
     *
     * **주의사항:**
     * - 대상 사용자는 다음 로그인 시 즉시 2FA 설정 필요
     * - 설정 완료 전까지는 서비스 이용 불가
     * - 중요한 결정이므로 충분한 사전 안내 권장
     *
     * @param targetUserEmail 2FA 설정을 강제할 사용자 이메일
     * @param httpServletRequest HTTP 요청 정보 (감사 로그용)
     * @return 강제 설정 완료 메시지
     */
    @PostMapping("/users/{targetUserEmail}/force-setup")
    suspend fun forceUserTotpSetup(
        @PathVariable targetUserEmail: String,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<String>> =
        totpResourceCoordinator.forceUserTotpSetup(targetUserEmail)
            .let { ApiResponse.of("사용자 '$targetUserEmail'에게 2FA 설정이 강제되었습니다. 해당 사용자는 다음 로그인 시 2FA 설정이 필요합니다.") }
            .let { ResponseEntity.ok(it) }

    /**
     * 특정 사용자의 2FA 강제 비활성화 (긴급 지원)
     *
     * 긴급 상황이나 사용자 지원 요청 시 관리자가 2FA를 비활성화합니다.
     * 매우 민감한 작업이므로 충분한 검토 후 실행해야 합니다.
     *
     * **사용 시나리오:**
     * - 사용자가 디바이스를 분실하고 백업 코드도 없는 경우
     * - 사용자가 OTP 앱을 삭제하고 복구가 불가능한 경우
     * - 계정 복구 지원 시 임시 조치
     * - 보안 사고 대응 시 긴급 접근 필요
     * - 퇴사자 계정 정리 시
     *
     * **보안 고려사항:**
     * - 반드시 사용자 본인 확인 후 실행
     * - 전화, 이메일 등 대체 인증 수단으로 본인 확인
     * - 비활성화 후 즉시 비밀번호 변경 권장
     * - 모든 작업을 보안 감사 로그에 기록
     * - 가능한 한 빠른 시일 내 2FA 재설정 안내
     *
     * **주의사항:**
     * - 매우 민감한 작업이므로 남용 금지
     * - 정당한 사유 없이 사용 금지
     * - 사용자에게 사전/사후 알림 필수
     *
     * @param targetUserEmail 2FA를 비활성화할 사용자 이메일
     * @param httpServletRequest HTTP 요청 정보 (감사 로그용)
     * @return 비활성화 완료 메시지
     */
    @DeleteMapping("/users/{targetUserEmail}/force-disable")
    suspend fun forceDisableUserTotp(
        @PathVariable targetUserEmail: String,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ApiResponse<String>> =
        totpResourceCoordinator.disableTotp(targetUserEmail)
            .let { ApiResponse.of("관리자에 의해 사용자 '$targetUserEmail'의 2FA가 비활성화되었습니다. 보안을 위해 즉시 비밀번호 변경을 권장합니다.") }
            .let { ResponseEntity.ok(it) }
}