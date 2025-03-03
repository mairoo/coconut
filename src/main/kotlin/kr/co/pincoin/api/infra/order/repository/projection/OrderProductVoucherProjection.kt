package kr.co.pincoin.api.infra.order.repository.projection

import com.querydsl.core.annotations.QueryProjection
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderProductVoucherProjection @QueryProjection constructor(
    // OrderProduct 정보
    val productId: Long?,
    val productCreated: ZonedDateTime,
    val productModified: ZonedDateTime,
    val productIsRemoved: Boolean,
    val orderId: Long,
    val productCode: String,
    val productName: String,
    val productSubtitle: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val quantity: Int,

    // OrderProductVoucher 정보
    val voucherId: Long?,
    val voucherCreated: ZonedDateTime?,
    val voucherModified: ZonedDateTime?,
    val voucherIsRemoved: Boolean?,
    val orderProductId: Long?,
    val voucherCode: String?,
    val assignedVoucherId: Long?,
    val revoked: Boolean?,
    val remarks: String?
)