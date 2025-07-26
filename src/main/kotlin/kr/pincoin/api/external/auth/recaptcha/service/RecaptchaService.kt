package kr.pincoin.api.external.auth.recaptcha.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kr.pincoin.api.external.auth.recaptcha.api.request.RecaptchaVerifyRequest
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData
import kr.pincoin.api.external.auth.recaptcha.properties.RecaptchaProperties
import org.springframework.stereotype.Service

@Service
class RecaptchaService(
    private val recaptchaApiClient: RecaptchaApiClient,
    private val recaptchaProperties: RecaptchaProperties,
) {
    /**
     * reCAPTCHA v2 검증
     */
    suspend fun verifyV2(
        token: String,
        remoteIp: String? = null,
        expectedHostname: String? = null,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(recaptchaProperties.timeout) {
                    val request = RecaptchaVerifyRequest(
                        token = token,
                        remoteIp = remoteIp,
                        expectedHostname = expectedHostname
                    )

                    val result = recaptchaApiClient.verifyToken(request)

                    when (result) {
                        is RecaptchaResponse.Success -> {
                            validateV2Response(result.data, expectedHostname)
                        }

                        is RecaptchaResponse.Error -> result
                    }
                }
            } catch (_: TimeoutCancellationException) {
                handleTimeout("reCAPTCHA v2 검증")
            } catch (e: Exception) {
                handleError(e, "reCAPTCHA v2 검증")
            }
        }

    /**
     * reCAPTCHA v3 검증
     */
    suspend fun verifyV3(
        token: String,
        action: String,
        remoteIp: String? = null,
        expectedHostname: String? = null,
        minScore: Double? = null,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(recaptchaProperties.timeout) {
                    val request = RecaptchaVerifyRequest(
                        token = token,
                        remoteIp = remoteIp,
                        action = action,
                        expectedHostname = expectedHostname
                    )

                    val result = recaptchaApiClient.verifyToken(request)

                    when (result) {
                        is RecaptchaResponse.Success -> {
                            validateV3Response(result.data, action, expectedHostname, minScore)
                        }

                        is RecaptchaResponse.Error -> result
                    }
                }
            } catch (_: TimeoutCancellationException) {
                handleTimeout("reCAPTCHA v3 검증")
            } catch (e: Exception) {
                handleError(e, "reCAPTCHA v3 검증")
            }
        }

    /**
     * 일반적인 검증 (v2/v3 자동 판별)
     */
    suspend fun verify(
        token: String,
        action: String? = null,
        remoteIp: String? = null,
        expectedHostname: String? = null,
        minScore: Double? = null,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        if (action != null) {
            verifyV3(token, action, remoteIp, expectedHostname, minScore)
        } else {
            verifyV2(token, remoteIp, expectedHostname)
        }

    /**
     * 조건부 검증 (설정에 따라 스킵 가능)
     */
    suspend fun verifyIfEnabled(
        token: String,
        action: String? = null,
        remoteIp: String? = null,
        expectedHostname: String? = null,
        minScore: Double? = null,
    ): RecaptchaResponse<RecaptchaVerifyData> {
        if (!isEnabled()) {
            // 검증이 비활성화된 경우 성공으로 처리
            return RecaptchaResponse.Success(
                RecaptchaVerifyData(
                    success = true,
                    score = 1.0,
                    action = action,
                    hostname = expectedHostname,
                    challengeTs = null,
                    errorCodes = null
                )
            )
        }

        return verify(token, action, remoteIp, expectedHostname, minScore)
    }

    /**
     * v2 응답 검증
     */
    private fun validateV2Response(
        data: RecaptchaVerifyData,
        expectedHostname: String?,
    ): RecaptchaResponse<RecaptchaVerifyData> {
        // 성공 여부 확인
        if (!data.success) {
            val errorMessage = mapErrorCodes(data.errorCodes)
            return RecaptchaResponse.Error(
                errorCode = "VERIFICATION_FAILED",
                errorMessage = errorMessage
            )
        }

        // 호스트명 검증 (선택적)
        if (expectedHostname != null && data.hostname != null && data.hostname != expectedHostname) {
            return RecaptchaResponse.Error(
                errorCode = "HOSTNAME_MISMATCH",
                errorMessage = "호스트명 불일치: 예상=${expectedHostname}, 실제=${data.hostname}"
            )
        }

        return RecaptchaResponse.Success(data)
    }

    /**
     * v3 응답 검증
     */
    private fun validateV3Response(
        data: RecaptchaVerifyData,
        expectedAction: String,
        expectedHostname: String?,
        minScore: Double?,
    ): RecaptchaResponse<RecaptchaVerifyData> {
        // 성공 여부 확인
        if (!data.success) {
            val errorMessage = mapErrorCodes(data.errorCodes)
            return RecaptchaResponse.Error(
                errorCode = "VERIFICATION_FAILED",
                errorMessage = errorMessage
            )
        }

        // 액션 검증
        if (data.action != expectedAction) {
            return RecaptchaResponse.Error(
                errorCode = "ACTION_MISMATCH",
                errorMessage = "액션 불일치: 예상=${expectedAction}, 실제=${data.action}"
            )
        }

        // 점수 검증
        val scoreThreshold = minScore ?: recaptchaProperties.minScore
        if (data.score != null && data.score < scoreThreshold) {
            return RecaptchaResponse.Error(
                errorCode = "LOW_SCORE",
                errorMessage = "점수 부족: ${data.score} < $scoreThreshold",
            )
        }

        // 호스트명 검증 (선택적)
        if (expectedHostname != null && data.hostname != null && data.hostname != expectedHostname) {
            return RecaptchaResponse.Error(
                errorCode = "HOSTNAME_MISMATCH",
                errorMessage = "호스트명 불일치: 예상=${expectedHostname}, 실제=${data.hostname}",
            )
        }

        return RecaptchaResponse.Success(data)
    }

    /**
     * 환경별 검증 활성화 여부 확인
     */
    fun isEnabled(): Boolean = recaptchaProperties.enabled

    /**
     * reCAPTCHA 에러 코드 매핑
     */
    private fun mapErrorCodes(errorCodes: List<String>?): String {
        if (errorCodes.isNullOrEmpty()) {
            return "reCAPTCHA 검증 실패: 알 수 없는 오류"
        }

        return when {
            errorCodes.contains("missing-input-secret") -> "Secret key가 누락되었습니다"
            errorCodes.contains("invalid-input-secret") -> "유효하지 않은 Secret key입니다"
            errorCodes.contains("missing-input-response") -> "reCAPTCHA 응답이 누락되었습니다"
            errorCodes.contains("invalid-input-response") -> "유효하지 않은 reCAPTCHA 응답입니다"
            errorCodes.contains("bad-request") -> "잘못된 요청입니다"
            errorCodes.contains("timeout-or-duplicate") -> "토큰이 만료되었거나 이미 사용되었습니다"
            else -> "reCAPTCHA 검증 실패: ${errorCodes.joinToString(", ")}"
        }
    }

    /**
     * 타임아웃 에러 처리
     */
    private fun handleTimeout(
        operation: String,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        RecaptchaResponse.Error(
            errorCode = "TIMEOUT",
            errorMessage = "$operation 요청 시간 초과"
        )

    /**
     * 일반 에러 처리
     */
    private fun handleError(
        error: Throwable,
        operation: String,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        RecaptchaResponse.Error(
            errorCode = "SYSTEM_ERROR",
            errorMessage = "$operation 중 오류 발생: ${error.message ?: "알 수 없는 오류"}"
        )
}