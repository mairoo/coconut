package kr.pincoin.api.external.verification.danal.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import kr.pincoin.api.external.verification.danal.properties.DanalProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.nio.charset.StandardCharsets
import java.time.Duration

@Configuration
class DanalWebClientConfig(
    private val objectMapper: ObjectMapper,
    private val danalProperties: DanalProperties,
) {
    @Bean
    fun danalUasWebClient(): WebClient =
        WebClient.builder()
            .baseUrl(danalProperties.baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name())
            .defaultHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
            .codecs { configurer ->
                configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
                configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                configurer.defaultCodecs().maxInMemorySize(512 * 1024)
            }
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .responseTimeout(Duration.ofMillis(danalProperties.timeout))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, danalProperties.timeout.toInt())
                )
            )
            .build()
}