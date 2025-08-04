package kr.pincoin.api.app.auth.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.RecaptchaV2VerifyRequest
import kr.pincoin.api.app.auth.request.RecaptchaV3VerifyRequest
import kr.pincoin.api.app.auth.response.RecaptchaStatusResponse
import kr.pincoin.api.app.auth.response.RecaptchaTestResponse
import kr.pincoin.api.external.auth.recaptcha.properties.RecaptchaProperties
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/open/recaptcha")
class RecaptchaController(
    private val recaptchaService: RecaptchaService,
    private val recaptchaProperties: RecaptchaProperties,
) {
    /**
     * reCAPTCHA v2 검증 테스트
     */
    @PostMapping("/v2/verify")
    fun verifyV2(
        @Valid @RequestBody request: RecaptchaV2VerifyRequest
    ): ResponseEntity<ApiResponse<RecaptchaTestResponse>> =
        RecaptchaTestResponse(
            message = "reCAPTCHA v2 검증 성공",
            data = recaptchaService.verifyV2(request.token),
        )
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * reCAPTCHA v3 검증 테스트
     */
    @PostMapping("/v3/verify")
    fun verifyV3(
        @Valid @RequestBody request: RecaptchaV3VerifyRequest
    ): ResponseEntity<ApiResponse<RecaptchaTestResponse>> =
        recaptchaService.verifyV3(request.token, request.minScore)
            .let { data ->
                RecaptchaTestResponse(
                    message = "reCAPTCHA v3 검증 성공 (점수: ${data.score})",
                    data = data,
                )
            }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * reCAPTCHA 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): ResponseEntity<ApiResponse<RecaptchaStatusResponse>> =
        RecaptchaStatusResponse(
            enabled = recaptchaProperties.enabled,
            message = if (recaptchaProperties.enabled) {
                "reCAPTCHA 서비스가 활성화되어 있습니다"
            } else {
                "reCAPTCHA 서비스가 비활성화되어 있습니다 (개발/테스트 모드)"
            }
        )
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}