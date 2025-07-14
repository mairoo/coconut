package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class TotpDevice private constructor(
    val id: Int? = null,
    val name: String,
    val confirmed: Boolean = false,
    val key: String,
    val step: Short = 30,
    val t0: Long = 0L,
    val digits: Short = 6,
    val tolerance: Short = 1,
    val drift: Short = 0,
    val lastT: Long = -1L,
    val userId: Int,
    val throttlingFailureCount: Int = 0,
    val throttlingFailureTimestamp: LocalDateTime? = null,
) {
    private fun copy(
        name: String = this.name,
        confirmed: Boolean = this.confirmed,
        key: String = this.key,
        step: Short = this.step,
        t0: Long = this.t0,
        digits: Short = this.digits,
        tolerance: Short = this.tolerance,
        drift: Short = this.drift,
        lastT: Long = this.lastT,
        userId: Int = this.userId,
        throttlingFailureCount: Int = this.throttlingFailureCount,
        throttlingFailureTimestamp: LocalDateTime? = this.throttlingFailureTimestamp,
    ): TotpDevice = TotpDevice(
        id = this.id,
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
        ): TotpDevice = TotpDevice(
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