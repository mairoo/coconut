package kr.pincoin.api.external.auth.recaptcha.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pincoin.api.external.auth.recaptcha.api.request.RecaptchaVerifyRequest
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyResponse
import kr.pincoin.api.external.auth.recaptcha.properties.RecaptchaProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Component
class RecaptchaApiClient(
    private val recaptchaWebClient: WebClient,
    private val recaptchaProperties: RecaptchaProperties,
    private val objectMapper: ObjectMapper,
) {
    /**
     * reCAPTCHA 토큰 검증
     */
    suspend fun verifyToken(
        request: RecaptchaVerifyRequest,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        try {
            val formData = LinkedMultiValueMap<String, String>().apply {
                add("secret", recaptchaProperties.secretKey)
                add("response", request.token)
                request.remoteIp?.let { add("remoteip", it) }
            }

            val response = recaptchaWebClient
                .post()
                .uri("/recaptcha/api/siteverify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .awaitBody<String>()

            handleSuccessResponse(response)
        } catch (e: WebClientResponseException) {
            handleHttpError(e)
        } catch (e: Exception) {
            handleGenericError(e)
        }

    /**
     * 성공 응답 파싱
     */
    private fun handleSuccessResponse(
        response: String,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        try {
            val recaptchaResponse = objectMapper.readValue(response, RecaptchaVerifyResponse::class.java)

            val data = RecaptchaVerifyData(
                success = recaptchaResponse.success,
                score = recaptchaResponse.score,
                action = recaptchaResponse.action,
                hostname = recaptchaResponse.hostname,
                challengeTs = recaptchaResponse.challengeTs,
                errorCodes = recaptchaResponse.errorCodes
            )

            RecaptchaResponse.Success(data)
        } catch (e: Exception) {
            RecaptchaResponse.Error("PARSE_ERROR", "응답 파싱 실패: ${e.message}")
        }

    /**
     * HTTP 에러 처리
     */
    private fun handleHttpError(
        e: WebClientResponseException,
    ): RecaptchaResponse<RecaptchaVerifyData> =
        try {
            // Google reCAPTCHA는 HTTP 200으로 에러를 반환하는 경우가 많음
            // 응답 본문에 실제 에러 정보가 있을 수 있음
            val jsonNode = objectMapper.readTree(e.responseBodyAsString)
            if (jsonNode.has("error-codes")) {
                RecaptchaResponse.Error(
                    errorCode = "RECAPTCHA_ERROR",
                    errorMessage = "reCAPTCHA 에러: ${jsonNode.get("error-codes")}"
                )
            } else {
                RecaptchaResponse.Error(
                    errorCode = "HTTP_ERROR_${e.statusCode.value()}",
                    errorMessage = "HTTP 오류: ${e.statusText}"
                )
            }
        } catch (_: Exception) {
            RecaptchaResponse.Error(
                errorCode = "HTTP_ERROR_${e.statusCode.value()}",
                errorMessage = "HTTP 오류: ${e.statusText}"
            )
        }

    /**
     * 일반적인 예외 처리
     */
    private fun handleGenericError(
        e: Exception,
    ): RecaptchaResponse<RecaptchaVerifyData> {
        val errorCode = when (e) {
            is java.net.SocketTimeoutException,
            is java.net.ConnectException -> "TIMEOUT"

            is java.net.UnknownHostException -> "CONNECTION_ERROR"
            is java.io.IOException -> "NETWORK_ERROR"
            else -> "UNKNOWN"
        }

        return RecaptchaResponse.Error(
            errorCode = errorCode,
            errorMessage = "reCAPTCHA 서버 오류: ${e.message ?: "알 수 없는 오류"}"
        )
    }
}