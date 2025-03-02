package kr.co.pincoin.api.infra.order.repository.criteria

import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import java.time.ZonedDateTime

data class OrderPaymentSearchCriteria(
    val id: Long? = null,
    val orderId: Long? = null,
    val account: PaymentBankAccount? = null,
    val isRemoved: Boolean? = null,
    val receivedFrom: ZonedDateTime? = null,
    val receivedTo: ZonedDateTime? = null,
    val createdFrom: ZonedDateTime? = null,
    val createdTo: ZonedDateTime? = null,
)