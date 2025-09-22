package kr.pincoin.api.external.verification.danal.error

enum class DanalErrorCode(
    val code: String,
    val message: String,
) {
    SUCCESS("0000", "성공"),
    INVALID_PARAMETER("1001", "잘못된 파라미터"),
    NETWORK_ERROR("9001", "네트워크 오류"),
    TIMEOUT("9002", "타임아웃"),
    CONNECTION_ERROR("9003", "연결 오류"),
    PARSE_ERROR("9004", "응답 파싱 오류"),
    HTTP_400("9400", "잘못된 요청"),
    HTTP_401("9401", "인증 실패"),
    HTTP_403("9403", "접근 금지"),
    HTTP_404("9404", "페이지를 찾을 수 없음"),
    HTTP_500("9500", "서버 내부 오류"),
    HTTP_502("9502", "잘못된 게이트웨이"),
    HTTP_503("9503", "서비스 사용 불가"),
    HTTP_504("9504", "게이트웨이 타임아웃"),
    UNKNOWN_ERROR("9999", "알 수 없는 오류");

    companion object {
        fun fromStatus(statusCode: Int): DanalErrorCode =
            when (statusCode) {
                400 -> HTTP_400
                401 -> HTTP_401
                403 -> HTTP_403
                404 -> HTTP_404
                500 -> HTTP_500
                502 -> HTTP_502
                503 -> HTTP_503
                504 -> HTTP_504
                else -> UNKNOWN_ERROR
            }
    }
}