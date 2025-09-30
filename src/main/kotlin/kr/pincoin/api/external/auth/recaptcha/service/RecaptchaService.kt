package kr.pincoin.api.external.auth.recaptcha.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kr.pincoin.api.app.auth.response.RecaptchaStatusResponse
import kr.pincoin.api.external.auth.recaptcha.api.request.RecaptchaVerifyRequest
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData
import kr.pincoin.api.external.auth.recaptcha.error.RecaptchaErrorCode
import kr.pincoin.api.external.auth.recaptcha.properties.RecaptchaProperties
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class RecaptchaService(
    private val recaptchaApiClient: RecaptchaApiClient,
    private val recaptchaProperties: RecaptchaProperties,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * reCAPTCHA 서비스 상태 조회
     */
    fun getStatus(): RecaptchaStatusResponse =
        RecaptchaStatusResponse(
            enabled = recaptchaProperties.enabled,
            message = if (recaptchaProperties.enabled) {
                "reCAPTCHA 서비스가 활성화되어 있습니다"
            } else {
                "reCAPTCHA 서비스가 비활성화되어 있습니다 (개발/테스트 모드)"
            }
        )

    /**
     * reCAPTCHA v2 검증
     */
    fun verifyV2(token: String): RecaptchaVerifyData =
        runBlocking {
            verifyV2Internal(token)
        }

    /**
     * reCAPTCHA v3 검증
     */
    fun verifyV3(
        token: String,
        minScore: Double? = null,
    ): RecaptchaVerifyData =
        runBlocking {
            verifyV3Internal(token, minScore)
        }

    /**
     * reCAPTCHA v2 검증 내부 로직
     */
    private suspend fun verifyV2Internal(token: String): RecaptchaVerifyData =
        withContext(Dispatchers.IO) {
            // enabled가 false인 경우 무조건 성공 반환
            if (!recaptchaProperties.enabled) {
                return@withContext RecaptchaVerifyData(
                    success = true,
                    hostname = null,
                    challengeTs = null,
                    errorCodes = null
                )
            }

            try {
                withTimeout(recaptchaProperties.timeout) {
                    val request = RecaptchaVerifyRequest(token = token)
                    val result = recaptchaApiClient.verifyV2Token(request)

                    when (result) {
                        is RecaptchaResponse.Success -> {
                            validateResponse(result.data)
                            result.data
                        }

                        is RecaptchaResponse.Error -> {
                            logger.warn { "reCAPTCHA v2 검증 실패: ${result.errorCode} - ${result.errorMessage}" }
                            throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                logger.warn { "reCAPTCHA v2 검증 요청 시간 초과" }
                throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
            } catch (e: BusinessException) {
                throw e
            } catch (e: Exception) {
                logger.warn { "reCAPTCHA v2 검증 중 시스템 오류: ${e.message}" }
                throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
            }
        }

    /**
     * reCAPTCHA v3 검증 내부 로직
     */
    private suspend fun verifyV3Internal(
        token: String,
        minScore: Double? = null,
    ): RecaptchaVerifyData =
        withContext(Dispatchers.IO) {
            // enabled가 false인 경우 무조건 성공 반환
            if (!recaptchaProperties.enabled) {
                return@withContext RecaptchaVerifyData(
                    success = true,
                    score = 1.0, // v3의 경우 최고 점수로 설정
                    action = null,
                    hostname = null,
                    challengeTs = null,
                    errorCodes = null
                )
            }

            try {
                withTimeout(recaptchaProperties.timeout) {
                    val request = RecaptchaVerifyRequest(token = token)
                    val result = recaptchaApiClient.verifyV3Token(request)

                    when (result) {
                        is RecaptchaResponse.Success -> {
                            validateResponse(result.data, minScore)
                            result.data
                        }

                        is RecaptchaResponse.Error -> {
                            logger.warn { "reCAPTCHA v3 검증 실패: ${result.errorCode} - ${result.errorMessage}" }
                            throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                logger.warn { "reCAPTCHA v3 검증 요청 시간 초과" }
                throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
            } catch (e: BusinessException) {
                throw e
            } catch (e: Exception) {
                logger.warn { "reCAPTCHA v3 검증 중 시스템 오류: ${e.message}" }
                throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
            }
        }

    /**
     * 응답 검증 로직
     */
    private fun validateResponse(
        data: RecaptchaVerifyData,
        minScore: Double? = null,
    ) {
        // 1. 성공 여부 확인 (가장 중요)
        if (!data.success) {
            logger.warn { "reCAPTCHA 검증 실패 - errorCodes: ${data.errorCodes}" }
            throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
        }

        // 2. 점수 검증 (v3인 경우만)
        if (data.score != null) {
            val scoreThreshold = minScore ?: recaptchaProperties.minScore
            if (data.score < scoreThreshold) {
                logger.warn { "reCAPTCHA 점수 부족: ${data.score} < $scoreThreshold" }
                throw BusinessException(RecaptchaErrorCode.VERIFICATION_FAILED)
            }
        }
    }
}