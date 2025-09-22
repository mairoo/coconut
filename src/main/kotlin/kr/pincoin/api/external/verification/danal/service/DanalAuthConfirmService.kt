package kr.pincoin.api.external.verification.danal.service

import kr.pincoin.api.external.verification.danal.api.response.DanalUasConfirmResponse
import kr.pincoin.api.external.verification.danal.error.DanalErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

@Service
class DanalAuthConfirmService(
    private val danalApiClient: DanalApiClient,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Danal 본인인증 결과 확인
     */
    suspend fun confirmAuth(
        tid: String,
    ): DanalUasConfirmResponse {
        val formData = createConfirmFormData(tid)
        return danalApiClient.executeUasApiCall(formData) { response ->
            parseConfirmResponse(response)
        }
    }

    private fun createConfirmFormData(
        tid: String,
    ) =
        LinkedMultiValueMap<String, String>().apply {
            add("TXTYPE", "CONFIRM")
            add("TID", tid)

            // 기본값이 있으므로 항상 추가
            add("CONFIRMOPTION", "0") // cpid, orderid 재확인 여부
            add("IDENOPTION", "1")

            // confirmOption = 0으로 간주
            // cpid, orderid 별도 재검증 없음
        }

    private fun parseConfirmResponse(
        response: String,
    ): DanalUasConfirmResponse =
        try {
            val params = danalApiClient.parseUrlEncodedResponse(response)

            val result = DanalUasConfirmResponse(
                returnCode = params["RETURNCODE"] ?: "9999",
                returnMessage = params["RETURNMSG"] ?: "알 수 없는 오류",
                tid = params["TID"] ?: "",
                ci = params["CI"] ?: "",
                di = params["DI"] ?: "",
                orderId = params["ORDERID"],
                name = params["NAME"] ?: "",
                userId = params["USERID"],
                iden = params["IDEN"],
                dob = params["DOB"],
                sex = params["SEX"],
                phone = params["PHONE"],
                telecom = params["TELECOM"],
            )

            logger.info { "Danal 본인인증 확인 완료 - TID: ${result.tid}, returnCode: ${result.returnCode}" }
            logger.debug { "확인된 정보 - CI: ${result.ci}, DI: ${result.di}, 이름: ${result.name}" }

            result
        } catch (e: Exception) {
            logger.error(e) { "CONFIRM 응답 파싱 실패" }
            DanalUasConfirmResponse(
                returnCode = DanalErrorCode.PARSE_ERROR.code,
                returnMessage = "응답 파싱 오류: ${e.message}",
                tid = "",
                ci = "",
                di = "",
                name = "",
            )
        }
}