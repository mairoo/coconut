package kr.pincoin.api.external.verification.danal.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.URLDecoder
import java.net.UnknownHostException

@Component
class DanalApiClient(
    private val danalUasWebClient: WebClient,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Danal UAS API 요청을 실행하고 응답을 처리하는 공통 메서드입니다.
     *
     * 처리 흐름:
     * 1. HTTP POST 요청 실행
     * 2. URL 인코딩된 응답에서 RETURNCODE 확인
     * 3. RETURNCODE가 "0000"이면 성공 응답으로 파싱
     * 4. 그 외의 경우 에러 응답으로 처리
     * 5. HTTP 통신/파싱 실패시 적절한 에러 응답 생성
     */
    suspend fun <T : Any> executeUasApiCall(
        formData: LinkedMultiValueMap<String, String>,
        responseParser: (String) -> T,
    ): T =
        try {
            // 1. HTTP POST 요청 및 응답 수신
            val response = danalUasWebClient
                .post()
                .uri("/uas/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .awaitBody<String>()

            // 2. URL 인코딩된 응답에서 RETURNCODE 확인
            val params = parseUrlEncodedResponse(response)
            val returnCode = params["RETURNCODE"] ?: "9999"

            val result = if (returnCode == "0000") {
                try {
                    // 3. 성공 응답으로 파싱
                    responseParser(response)
                } catch (e: Exception) {
                    logger.error(e) { "성공 응답 파싱 실패" }
                    throw createParsingException("성공 응답 파싱 오류: ${e.message}")
                }
            } else {
                // 4. 에러 응답으로 처리
                val returnMessage = params["RETURNMSG"] ?: "알 수 없는 오류"
                logger.error { "Danal API 에러 응답 - returnCode: $returnCode, returnMessage: $returnMessage" }
                throw createDanalApiException(returnCode, returnMessage)
            }

            // API 로그 발행은 향후 필요시 추가 가능
            // eventPublisher.publishApiLogEvent(...)

            result
        } catch (e: WebClientResponseException) {
            handleWebClientException(e)
            throw e
        } catch (e: Exception) {
            if (e.message?.contains("Danal") == true) {
                // Danal API 에러는 그대로 전파
                throw e
            }
            handleGeneralException(e)
            throw e
        }

    /**
     * WebClientResponseException 처리 (4xx, 5xx HTTP 응답)
     */
    private fun handleWebClientException(e: WebClientResponseException) {
        logger.error { "=== HTTP 에러 응답 수신 ===" }
        logger.error { "HTTP Status: ${e.statusCode}" }
        logger.error { "HTTP Status Code: ${e.statusCode.value()}" }
        logger.error { "Response Headers: ${e.headers}" }
        logger.error { "Raw Error Response Body: ${e.responseBodyAsString}" }

        try {
            // HTTP 에러 응답이 Danal 형식인지 확인하고 파싱 시도
            val params = parseUrlEncodedResponse(e.responseBodyAsString)
            if (params.containsKey("RETURNCODE")) {
                val returnCode = params["RETURNCODE"] ?: "HTTP_ERROR"
                val returnMessage = params["RETURNMSG"] ?: "HTTP 오류: ${e.statusCode}"

                logger.error { "HTTP 에러 응답 파싱 완료 - returnCode: $returnCode, returnMessage: $returnMessage" }
            } else {
                logger.error { "Danal 형식이 아닌 HTTP 에러 응답" }
            }
        } catch (parseException: Exception) {
            logger.error(parseException) { "HTTP 에러 응답 파싱 실패" }
        }
    }

    /**
     * 일반 예외 처리 (네트워크 오류 등)
     */
    private fun handleGeneralException(e: Exception) {
        logger.error { "=== 네트워크/시스템 에러 발생 ===" }
        logger.error { "Exception Type: ${e::class.java.simpleName}" }
        logger.error { "Exception Message: ${e.message}" }
        logger.error(e) { "Stack Trace:" }

        when (e) {
            is SocketTimeoutException, is ConnectException -> {
                logger.error { "네트워크 타임아웃 또는 연결 오류" }
            }

            is UnknownHostException -> {
                logger.error { "호스트를 찾을 수 없음" }
            }

            else -> {
                logger.error { "기타 네트워크 오류" }
            }
        }
    }

    internal fun parseUrlEncodedResponse(
        response: String,
    ): Map<String, String> =
        if (response.contains("=") && response.contains("&")) {
            response.split("&").associate { param ->
                val (key, value) = param.split("=", limit = 2)
                key to URLDecoder.decode(value, "UTF-8")
            }
        } else {
            emptyMap()
        }

    private fun createParsingException(
        message: String,
    ): Exception =
        RuntimeException("Danal 파싱 오류: $message")

    private fun createDanalApiException(
        returnCode: String,
        returnMessage: String,
    ): Exception =
        RuntimeException("Danal API 오류 [$returnCode]: $returnMessage")
}