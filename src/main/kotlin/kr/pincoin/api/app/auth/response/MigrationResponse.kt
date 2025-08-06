package kr.pincoin.api.app.auth.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MigrationResponse(
    @field:JsonProperty("message")
    val message: String,

    @field:JsonProperty("migratedAt")
    val migratedAt: LocalDateTime? = null,

    @field:JsonProperty("email")
    val email: String? = null,
) {
    companion object {
        fun of(
            email: String,
            migratedAt: LocalDateTime = LocalDateTime.now(),
        ) = MigrationResponse(
            message = "레거시 사용자 마이그레이션이 성공적으로 완료되었습니다.",
            migratedAt = migratedAt,
            email = email,
        )
    }
}