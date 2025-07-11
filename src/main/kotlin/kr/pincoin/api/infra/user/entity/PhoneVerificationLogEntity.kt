package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "member_phoneverificationlog")
class PhoneVerificationLogEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "token")
    val token: String,

    @Column(name = "code")
    val code: String,

    @Column(name = "reason")
    val reason: String,

    @Column(name = "result_code")
    val resultCode: String,

    @Column(name = "message")
    val message: String,

    @Column(name = "transaction_id")
    val transactionId: String,

    @Column(name = "di")
    val di: String,

    @Column(name = "ci")
    val ci: String,

    @Column(name = "fullname")
    val fullname: String,

    @Column(name = "date_of_birth")
    val dateOfBirth: String,

    @Column(name = "gender")
    val gender: Int,

    @Column(name = "domestic")
    val domestic: Int,

    @Column(name = "telecom")
    val telecom: String,

    @Column(name = "cellphone")
    val cellphone: String,

    @Column(name = "return_message")
    val returnMessage: String,

    @Column(name = "owner_id")
    val ownerId: Int?,
) {
    companion object {
        fun of(
            id: Long? = null,
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
        ) = PhoneVerificationLogEntity(
            id = id,
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