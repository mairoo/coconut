package kr.co.pincoin.api.app.order.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import java.math.BigDecimal
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderProductVoucherResponse(
    // OrderProduct 정보
    @JsonProperty("productId")
    val productId: Long?,

    @JsonProperty("productCreated")
    val productCreated: ZonedDateTime,

    @JsonProperty("productModified")
    val productModified: ZonedDateTime,

    @JsonProperty("productIsRemoved")
    val productIsRemoved: Boolean,

    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("productCode")
    val productCode: String,

    @JsonProperty("productName")
    val productName: String,

    @JsonProperty("productSubtitle")
    val productSubtitle: String,

    @JsonProperty("listPrice")
    val listPrice: BigDecimal,

    @JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal,

    @JsonProperty("quantity")
    val quantity: Int,

    // OrderProductVoucher 정보
    @JsonProperty("voucherId")
    val voucherId: Long?,

    @JsonProperty("voucherCreated")
    val voucherCreated: ZonedDateTime?,

    @JsonProperty("voucherModified")
    val voucherModified: ZonedDateTime?,

    @JsonProperty("voucherIsRemoved")
    val voucherIsRemoved: Boolean?,

    @JsonProperty("orderProductId")
    val orderProductId: Long?,

    @JsonProperty("voucherCode")
    val voucherCode: String?,

    @JsonProperty("assignedVoucherId")
    val assignedVoucherId: Long?,

    @JsonProperty("revoked")
    val revoked: Boolean?,

    @JsonProperty("remarks")
    val remarks: String?
) {
    companion object {
        fun from(projection: OrderProductVoucherProjection): OrderProductVoucherResponse =
            with(projection) {
                OrderProductVoucherResponse(
                    productId = productId,
                    productCreated = productCreated,
                    productModified = productModified,
                    productIsRemoved = productIsRemoved,
                    orderId = orderId,
                    productCode = productCode,
                    productName = productName,
                    productSubtitle = productSubtitle,
                    listPrice = listPrice,
                    sellingPrice = sellingPrice,
                    quantity = quantity,
                    voucherId = voucherId,
                    voucherCreated = voucherCreated,
                    voucherModified = voucherModified,
                    voucherIsRemoved = voucherIsRemoved,
                    orderProductId = orderProductId,
                    voucherCode = voucherCode,
                    assignedVoucherId = assignedVoucherId,
                    revoked = revoked,
                    remarks = remarks
                )
            }
    }
}