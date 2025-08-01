package kr.pincoin.api.app.user.my.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.common.request.TotpSetupRequest
import kr.pincoin.api.app.user.common.response.TotpSetupResponse
import kr.pincoin.api.app.user.common.response.TotpStatusResponse
import kr.pincoin.api.domain.coordinator.user.TotpResourceCoordinator
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.security.annotation.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 일반 사용자용 TOTP 2FA 컨트롤러
 *
 * 사용자가 자발적으로 2FA 설정을 관리할 수 있는 API를 제공합니다.
 * 관리자 권한 없이 본인의 2FA 설정만 관리할 수 있습니다.
 */
@RestController
@RequestMapping("/my/totp")
class MyTotpController(
    private val totpResourceCoordinator: TotpResourceCoordinator
) {
    /**
     * 내 2FA 상태 조회
     *
     * 현재 로그인한 사용자의 2FA 활성화 상태를 조회합니다.
     *
     * @param userEmail 현재 로그인한 사용자 이메일 (JWT에서 추출)
     * @return 2FA 상태 정보 (활성화 여부, 백업 코드 개수 등)
     */
    @GetMapping("/status")
    suspend fun getMyTotpStatus(
        @CurrentUser userEmail: String,
    ): ResponseEntity<ApiResponse<TotpStatusResponse>> =
        totpResourceCoordinator.getTotpStatus(userEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 내 2FA 설정 시작
     *
     * 2FA를 활성화하기 위한 설정 프로세스를 시작합니다.
     * QR 코드 URL, 수동 입력 키, 백업 코드를 생성하여 반환합니다.
     *
     * **설정 플로우:**
     * 1. 이 API 호출로 QR 코드 및 백업 코드 생성
     * 2. Google Authenticator 앱에서 QR 코드 스캔 또는 수동 입력
     * 3. 앱에서 생성된 6자리 OTP 코드 확인
     * 4. `/complete` API로 OTP 코드 검증하여 설정 완료
     *
     * **주의사항:**
     * - 이미 2FA가 활성화된 경우 오류 반환
     * - 디바이스 분실 등 문제 발생 시 관리자에게 문의
     * - 설정 세션은 10분 후 만료
     *
     * @param userEmail 현재 로그인한 사용자 이메일
     * @return QR 코드 URL, 수동 입력 키, 백업 코드
     */
    @PostMapping("/setup/start")
    suspend fun startMyTotpSetup(
        @CurrentUser userEmail: String
    ): ResponseEntity<ApiResponse<TotpSetupResponse>> =
        totpResourceCoordinator.startTotpSetup(userEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 내 2FA 설정 완료
     *
     * Google Authenticator에서 생성된 6자리 OTP 코드를 검증하여
     * 2FA 설정을 완료합니다.
     *
     * **검증 및 완료 과정:**
     * 1. Redis에서 임시 저장된 TOTP Secret 조회
     * 2. 입력된 OTP 코드 형식 검증 (실제 검증은 Keycloak에서 처리)
     * 3. 검증 성공 시 Keycloak에 TOTP 인증정보 영구 저장
     * 4. 임시 데이터 정리
     * 5. 다음 로그인부터 2FA 필수 적용
     *
     * **검증 실패 시:**
     * - 잘못된 OTP 코드 입력 시 오류 반환
     * - 설정 세션 만료 시 처음부터 다시 시작 필요
     *
     * @param userEmail 현재 로그인한 사용자 이메일
     * @param request 6자리 OTP 코드
     * @return 설정 완료 메시지
     */
    @PostMapping("/setup/complete")
    suspend fun completeMyTotpSetup(
        @CurrentUser userEmail: String,
        @Valid @RequestBody request: TotpSetupRequest
    ): ResponseEntity<ApiResponse<String>> =
        totpResourceCoordinator.completeTotpSetup(userEmail, request)
            .let { ApiResponse.of("2FA 설정이 완료되었습니다. 다음 로그인부터 OTP 코드가 필요합니다.") }
            .let { ResponseEntity.ok(it) }

    /**
     * 내 2FA 비활성화
     *
     * 현재 활성화된 2FA를 완전히 비활성화합니다.
     *
     * **비활성화 과정:**
     * 1. Keycloak에서 TOTP 인증정보 삭제
     * 2. 저장된 백업 코드 모두 삭제
     * 3. 다음 로그인부터 이메일/비밀번호만으로 로그인 가능
     *
     * **주의사항:**
     * - 비활성화 후에는 추가 보안 레이어 없음
     * - 디바이스 분실 등으로 다시 접근이 어려워지면 관리자에게 문의
     * - 보안상 중요한 계정의 경우 비활성화 신중히 고려
     * - 필요 시 언제든 다시 활성화 가능
     *
     * @param userEmail 현재 로그인한 사용자 이메일
     * @return 비활성화 완료 메시지
     */
    @DeleteMapping("/disable")
    suspend fun disableMyTotp(
        @CurrentUser userEmail: String
    ): ResponseEntity<ApiResponse<String>> =
        totpResourceCoordinator.disableTotp(userEmail)
            .let { ApiResponse.of("2FA가 비활성화되었습니다. 다음 로그인부터 이메일/비밀번호만으로 로그인 가능합니다.") }
            .let { ResponseEntity.ok(it) }
}