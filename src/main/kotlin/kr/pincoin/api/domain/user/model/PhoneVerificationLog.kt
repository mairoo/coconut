package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class PhoneVerificationLog private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val token: String,
    val code: String,
    val reason: String,
    val resultCode: String,
    val message: String,
    val transactionId: String,
    val di: String,
    val ci: String,
    val fullname: String,
    val dateOfBirth: String,
    val gender: Int,
    val domestic: Int,
    val telecom: String,
    val cellphone: String,
    val returnMessage: String,
    val ownerId: Int? = null,
) {
    fun isSuccessful(): Boolean = resultCode == "0000"

    fun isFailed(): Boolean = !isSuccessful()

    fun hasOwner(): Boolean = ownerId != null

    fun isVerificationSuccessful(): Boolean =
        isSuccessful() && reason == "본인확인"

    fun isRegistrationSuccessful(): Boolean =
        isSuccessful() && reason == "회원가입"

    fun getGenderText(): String = when (gender) {
        1 -> "남성"
        2 -> "여성"
        else -> "미상"
    }

    fun getDomesticText(): String = when (domestic) {
        1 -> "내국인"
        2 -> "외국인"
        else -> "미상"
    }

    fun getTelecomText(): String = when (telecom) {
        "SKT" -> "SK텔레콤"
        "KTF" -> "KT"
        "LGT" -> "LG유플러스"
        "MVNO" -> "알뜰폰"
        else -> telecom
    }

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            token: String,
            code: String,
            reason: String,
            resultCode: String,
            message: String,
            transactionId: String,
            di: String,
            ci: String,
            fullname: String,
            dateOfBirth: String,
            gender: Int,
            domestic: Int,
            telecom: String,
            cellphone: String,
            returnMessage: String,
            ownerId: Int? = null,
        ): PhoneVerificationLog = PhoneVerificationLog(
            id = id,
            created = created,
            modified = modified,
            token = token,
            code = code,
            reason = reason,
            resultCode = resultCode,
            message = message,
            transactionId = transactionId,
            di = di,
            ci = ci,
            fullname = fullname,
            dateOfBirth = dateOfBirth,
            gender = gender,
            domestic = domestic,
            telecom = telecom,
            cellphone = cellphone,
            returnMessage = returnMessage,
            ownerId = ownerId,
        )
    }
}