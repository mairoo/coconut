package kr.pincoin.api.app.auth.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SignUpRequestedResponse(
    /**
     * 사용자에게 표시할 메시지
     * 예: "인증 이메일이 발송되었습니다. 이메일을 확인해주세요."
     */
    @field:JsonProperty("message")
    val message: String,

    /**
     * 가입 요청한 이메일 주소 (마스킹 처리)
     * 예: "user****@example.com"
     */
    @field:JsonProperty("email")
    val maskedEmail: String,

    /**
     * 인증 링크 만료 시간 (1단계에서만 제공)
     * 클라이언트에서 카운트다운 표시용
     */
    @field:JsonProperty("expiresAt")
    val expiresAt: LocalDateTime,
)