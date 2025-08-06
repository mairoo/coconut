package kr.pincoin.api.app.auth.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.RecaptchaV2VerifyRequest
import kr.pincoin.api.app.auth.request.RecaptchaV3VerifyRequest
import kr.pincoin.api.app.auth.response.RecaptchaStatusResponse
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/open/recaptcha")
class RecaptchaController(
    private val recaptchaService: RecaptchaService,
) {
    /**
     * reCAPTCHA v2 검증 테스트
     */
    @PostMapping("/v2/verify")
    fun verifyV2(
        @Valid @RequestBody request: RecaptchaV2VerifyRequest
    ): ResponseEntity<ApiResponse<RecaptchaVerifyData>> =
        recaptchaService.verifyV2(request.token)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * reCAPTCHA v3 검증 테스트
     */
    @PostMapping("/v3/verify")
    fun verifyV3(
        @Valid @RequestBody request: RecaptchaV3VerifyRequest
    ): ResponseEntity<ApiResponse<RecaptchaVerifyData>> =
        recaptchaService.verifyV3(request.token, request.minScore)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * reCAPTCHA 상태 확인
     */
    @GetMapping("/status")
    fun getStatus(): ResponseEntity<ApiResponse<RecaptchaStatusResponse>> =
        recaptchaService.getStatus()
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}