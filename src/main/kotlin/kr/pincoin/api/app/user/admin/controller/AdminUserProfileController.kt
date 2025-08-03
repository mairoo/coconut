package kr.pincoin.api.app.user.admin.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.admin.request.AdminPasswordChangeRequest
import kr.pincoin.api.app.user.admin.service.AdminUserProfileService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/users")
class AdminUserProfileController(
    private val adminUserProfileService: AdminUserProfileService,
) {
    /**
     * # 비밀번호 관리 시나리오
     *
     * ## 1. 비밀번호 강제 설정 (영구)
     * → 새 비밀번호를 영구적으로 설정, 사용자가 그대로 사용 가능
     *
     * ## 2. 비밀번호 강제 설정 + 강제 변경 유도 (Temporary)
     * → 임시 비밀번호 설정, 해당 비밀번호로 로그인 시 자동으로 변경 화면 표시
     * → 사용 사례: 비밀번호 분실, 신규 사용자 초기 비밀번호
     *
     * ## 3. 비밀번호 유지 + 강제 변경 유도 (Required Action)
     * → 현재 비밀번호는 유지하되, UPDATE_PASSWORD Required Action 추가
     * → 다음 로그인 시 비밀번호 변경 화면 표시
     * → 사용 사례: 보안상 이유로 비밀번호 변경 필요하지만 기존 비밀번호는 알려진 경우
     *
     * ## 4. 사용자 직접 비밀번호 변경 불가
     */

    /**
     * 관리자가 사용자 비밀번호 강제 변경
     * temporary=true면 임시 비밀번호로 설정 (해당 비밀번호로 로그인 후 즉시 변경 필수)
     */
    @PatchMapping("/{userId}/password")
    fun changeUserPassword(
        @PathVariable userId: Int,
        @Valid @RequestBody request: AdminPasswordChangeRequest,
    ): ResponseEntity<ApiResponse<Nothing?>> =
        adminUserProfileService.changeUserPassword(userId, request)
            .let {
                val message = if (request.temporary) {
                    "임시 비밀번호가 설정되었습니다. 사용자는 다음 로그인 시 비밀번호를 변경해야 합니다."
                } else {
                    "사용자 비밀번호가 성공적으로 변경되었습니다."
                }
                ApiResponse.of(
                    data = null,
                    message = message,
                )
            }
            .let { ResponseEntity.ok(it) }

    /**
     * 관리자가 사용자에게 비밀번호 재설정 강제 (Required Action)
     * 현재 비밀번호는 유지하고, 다음 로그인 시 비밀번호 변경 화면만 표시
     * Keycloak의 UPDATE_PASSWORD 액션을 사용
     */
    @PostMapping("/{userId}/password/force-reset")
    fun forcePasswordReset(
        @PathVariable userId: Int,
    ): ResponseEntity<ApiResponse<Nothing?>> =
        adminUserProfileService.forcePasswordReset(userId)
            .let {
                ApiResponse.of(
                    data = null,
                    message = "사용자에게 비밀번호 재설정이 강제되었습니다. 다음 로그인 시 비밀번호 변경 화면이 표시됩니다.",
                )
            }
            .let { ResponseEntity.ok(it) }

    /**
     * 사용자 프로필 조회
     *
     * TODO: UserProfileFacade로 위임 예정
     *
     * **구현 계획:**
     * - JWT 토큰에서 사용자 정보 추출
     * - Keycloak 사용자 정보 조회
     * - DB 사용자 정보와 병합
     * - 개인정보는 마스킹 처리
     */
    // fun getUserProfile(accessToken: String): UserProfileResponse = userProfileFacade.getUserProfile(accessToken)

    /**
     * 사용자 프로필 수정
     *
     * TODO: UserProfileFacade로 위임 예정
     *
     * **구현 계획:**
     * - JWT 토큰에서 사용자 정보 추출
     * - 입력값 검증 및 권한 확인
     * - Keycloak과 DB에서 동시 업데이트
     * - 이메일 변경 시 재인증 프로세스
     */
    // fun updateUserProfile(accessToken: String, request: UpdateProfileRequest): UpdateProfileResponse = userProfileFacade.updateUserProfile(accessToken, request)

    /**
     * 계정 비활성화 (탈퇴)
     *
     * TODO: UserProfileFacade로 위임 예정
     *
     * **구현 계획:**
     * - 비밀번호 재확인
     * - Keycloak 계정 비활성화
     * - DB에서 소프트 삭제 처리
     * - 관련 세션 모두 무효화
     * - 개인정보 보호를 위한 데이터 마스킹
     * - redis에 해당 이메일 주소 재가입 금지 저장
     */
    // fun deactivateAccount(accessToken: String, password: String): DeactivateAccountResponse = userProfileFacade.deactivateAccount(accessToken, password)
}