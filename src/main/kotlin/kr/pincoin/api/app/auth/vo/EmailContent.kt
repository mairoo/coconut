package kr.pincoin.api.app.auth.vo

/**
 * 이메일 콘텐츠 데이터 클래스
 *
 * 텍스트와 HTML 버전의 이메일 콘텐츠를 담는 데이터 클래스입니다.
 * 이메일 클라이언트 호환성을 위해 두 형식을 모두 제공합니다.
 */
data class EmailContent(
    val text: String,
    val html: String,
)