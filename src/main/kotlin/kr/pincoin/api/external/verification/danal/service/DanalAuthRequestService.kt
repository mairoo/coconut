package kr.pincoin.api.external.verification.danal.service

import kr.pincoin.api.external.verification.danal.api.response.DanalUasResponse
import kr.pincoin.api.external.verification.danal.error.DanalErrorCode
import kr.pincoin.api.external.verification.danal.properties.DanalProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap

@Service
class DanalAuthRequestService(
    private val danalApiClient: DanalApiClient,
    private val danalProperties: DanalProperties,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun requestAuth(
    ): DanalUasResponse =
        danalApiClient.executeUasApiCall(createUasFormData()) { response ->
            parseUasResponse(response)
        }

    private fun createUasFormData(
    ) =
        LinkedMultiValueMap<String, String>().apply {
            add("TXTYPE", "ITEMSEND")
            add("SERVICE", "UAS")
            add("AUTHTYPE", "36")
            add("CPID", danalProperties.cpId)
            add("CPPWD", danalProperties.cpPwd)
            add("TARGETURL", danalProperties.targetUrl)
            add("CPTITLE", danalProperties.cpTitle)
        }

    private fun parseUasResponse(
        response: String,
    ): DanalUasResponse =
        try {
            val params = danalApiClient.parseUrlEncodedResponse(response)

            DanalUasResponse(
                returnCode = params["RETURNCODE"] ?: "9999",
                returnMessage = params["RETURNMSG"],
                tid = params["TID"],
            )
        } catch (e: Exception) {
            logger.error(e) { "UAS 응답 파싱 실패" }
            DanalUasResponse(
                returnCode = DanalErrorCode.PARSE_ERROR.code,
                returnMessage = "응답 파싱 오류: ${e.message}",
            )
        }
}