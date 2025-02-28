package kr.or.bigs.firmbank.external.support.notification.telegram.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.pincoin.api.external.telegram.api.request.TelegramMessagePayload
import kr.co.pincoin.api.external.telegram.api.request.TelegramMessageRequest
import kr.co.pincoin.api.external.telegram.api.response.TelegramApiResponse
import kr.co.pincoin.api.external.telegram.api.response.TelegramMessageResponse
import kr.co.pincoin.api.external.telegram.code.TelegramErrorCode
import kr.co.pincoin.api.external.telegram.properties.TelegramProperties
import kr.co.pincoin.api.global.exception.BusinessException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class TelegramMessageApiClient(
    private val telegramWebClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val telegramProperties: TelegramProperties,
) {
    fun sendMessage(messageText: TelegramMessagePayload): Mono<TelegramApiResponse<TelegramMessageResponse>> {
        val request = TelegramMessageRequest.of(telegramProperties.chatId, messageText)

        return telegramWebClient
            .post()
            .uri("/bot${telegramProperties.botToken}/sendMessage")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String::class.java)
            .flatMap { response: String ->
                try {
                    val type = objectMapper.typeFactory.constructParametricType(
                        TelegramApiResponse::class.java,
                        TelegramMessageResponse::class.java
                    )
                    val telegramApiResponse: TelegramApiResponse<TelegramMessageResponse> =
                        objectMapper.readValue(response, type)
                    Mono.just(telegramApiResponse)
                } catch (e: Exception) {
                    Mono.error(BusinessException(TelegramErrorCode.TELEGRAM_API_PARSE_ERROR))
                }
            }
            .onErrorMap { e ->
                when (e) {
                    is BusinessException -> e
                    else -> BusinessException(TelegramErrorCode.TELEGRAM_API_SEND_ERROR)
                }
            }
    }
}