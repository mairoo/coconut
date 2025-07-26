package kr.pincoin.api.app.auth.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 회원가입 응답 데이터 클래스
 *
 * 회원가입 프로세스의 각 단계별로 적절한 응답을 제공합니다.
 * - 1단계(임시저장): 이메일 인증 안내 메시지와 만료 시간
 * - 2단계(인증완료): 회원가입 완료 확인 메시지
 */
data class SignUpResponse(
    /**
     * 사용자에게 표시할 메시지
     * 예: "인증 이메일이 발송되었습니다. 이메일을 확인해주세요."
     */
    @JsonProperty("message")
    val message: String,

    /**
     * 가입 요청한 이메일 주소 (마스킹 처리)
     * 예: "user****@example.com"
     */
    @JsonProperty("email")
    val maskedEmail: String,

    /**
     * 인증 링크 만료 시간 (1단계에서만 제공)
     * 클라이언트에서 카운트다운 표시용
     */
    @JsonProperty("expiresAt")
    val expiresAt: LocalDateTime? = null,
)