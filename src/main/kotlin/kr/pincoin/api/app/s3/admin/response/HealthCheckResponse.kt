package kr.pincoin.api.app.s3.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class HealthCheckResponse(
    @field:JsonProperty("status")
    val status: String,

    @field:JsonProperty("healthy")
    val healthy: Boolean,

    @field:JsonProperty("timestamp")
    val timestamp: String,

    @field:JsonProperty("service")
    val service: String,

    @field:JsonProperty("checks")
    val checks: List<String>? = null,
) {
    companion object {
        fun of(
            checks: List<String> = emptyList(),
        ) = HealthCheckResponse(
            status = "UP",
            healthy = true,
            timestamp = LocalDateTime.now().toString(),
            service = "S3",
            checks = checks,
        )
    }
}