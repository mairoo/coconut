package kr.pincoin.api.app.auth.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SignUpCompletedResponse(
    /**
     * 회원가입 완료 메시지
     * 예: "회원가입이 성공적으로 완료되었습니다."
     */
    @JsonProperty("message")
    val message: String,

    /**
     * 가입 완료된 이메일 주소 (마스킹 없음)
     * 인증이 완료되었으므로 전체 이메일 주소 표시
     */
    @JsonProperty("email")
    val email: String,

    /**
     * 생성된 사용자명
     * 로그인 시 사용할 사용자명 정보
     */
    @JsonProperty("username")
    val username: String,

    /**
     * 회원가입 완료 시간
     * 클라이언트에서 "가입일" 표시용
     */
    @JsonProperty("completedAt")
    val completedAt: LocalDateTime,
)