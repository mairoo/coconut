package kr.co.pincoin.api.infra.user.repository.criteria

import java.net.InetAddress
import java.time.ZonedDateTime

data class LoginLogSearchCriteria(
    val id: Long? = null,
    val email: String? = null,
    val username: String? = null,
    val ipAddress: InetAddress? = null,
    val userId: Int? = null,
    val isSuccessful: Boolean? = null,
    val reason: String? = null,
    val createdFrom: ZonedDateTime? = null,
    val createdTo: ZonedDateTime? = null,
)