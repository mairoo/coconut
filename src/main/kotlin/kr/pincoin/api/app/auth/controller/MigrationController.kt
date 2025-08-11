package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.MigrationRequest
import kr.pincoin.api.app.auth.response.MigrationResponse
import kr.pincoin.api.app.auth.service.MigrationFacade
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class MigrationController(
    private val migrationFacade: MigrationFacade,
) {
    /**
     * 레거시 사용자 마이그레이션
     *
     * Django allauth 기반 레거시 사용자를 Keycloak으로 마이그레이션합니다.
     *
     * **처리 단계:**
     * 1. 보안 검증 (reCAPTCHA 등)
     * 2. 레거시 사용자 검증 (PBKDF2 비밀번호 확인)
     * 3. 이미 마이그레이션된 사용자 확인
     * 4. Keycloak으로 마이그레이션 수행
     *
     * **마이그레이션 프로세스:**
     * - **사용자 확인**: 기존 User 테이블에서 사용자 조회
     * - **비밀번호 검증**: 레거시 패스워드 인코더(PBKDF2)로 비밀번호 검증
     * - **중복 확인**: 이미 keycloak_id가 있는 사용자는 마이그레이션 완료로 처리
     * - **Keycloak 생성**: 레거시 사용자를 Keycloak에 생성
     * - **연결**: User 테이블의 keycloak_id 업데이트
     *
     * **보안 기능:**
     * - reCAPTCHA 검증 (무작위 공격 방어)
     * - 향후 확장: IP별 마이그레이션 제한 등
     */
    @PostMapping("/migrate")
    fun migrateUser(
        @Valid @RequestBody request: MigrationRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<MigrationResponse>> =
        migrationFacade.processMigration(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}