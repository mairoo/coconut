package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "otp_totp_totpdevice")
class TotpDeviceEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "name")
    val name: String,

    @Column(name = "confirmed")
    val confirmed: Boolean,

    @Column(name = "key")
    val key: String,

    @Column(name = "step")
    val step: Short,

    @Column(name = "t0")
    val t0: Long,

    @Column(name = "digits")
    val digits: Short,

    @Column(name = "tolerance")
    val tolerance: Short,

    @Column(name = "drift")
    val drift: Short,

    @Column(name = "last_t")
    val lastT: Long,

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "throttling_failure_count")
    val throttlingFailureCount: Int,

    @Column(name = "throttling_failure_timestamp")
    val throttlingFailureTimestamp: LocalDateTime?,
) {
    companion object {
        fun of(
            id: Int? = null,
            name: String,
            confirmed: Boolean = false,
            key: String,
            step: Short = 30,
            t0: Long = 0L,
            digits: Short = 6,
            tolerance: Short = 1,
            drift: Short = 0,
            lastT: Long = -1L,
            userId: Int,
            throttlingFailureCount: Int = 0,
            throttlingFailureTimestamp: LocalDateTime? = null,
        ) = TotpDeviceEntity(
            id = id,
            name = name,
            confirmed = confirmed,
            key = key,
            step = step,
            t0 = t0,
            digits = digits,
            tolerance = tolerance,
            drift = drift,
            lastT = lastT,
            userId = userId,
            throttlingFailureCount = throttlingFailureCount,
            throttlingFailureTimestamp = throttlingFailureTimestamp,
        )
    }
}