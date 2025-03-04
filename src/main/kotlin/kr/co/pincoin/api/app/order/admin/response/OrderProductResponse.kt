package kr.co.pincoin.api.app.order.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.order.model.OrderProduct
import java.math.BigDecimal
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderProductResponse(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("created")
    val created: ZonedDateTime?,

    @JsonProperty("modified")
    val modified: ZonedDateTime?,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean,

    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("code")
    val code: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("subtitle")
    val subtitle: String,

    @JsonProperty("listPrice")
    val listPrice: BigDecimal,

    @JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal,

    @JsonProperty("quantity")
    val quantity: Int
) {
    companion object {
        fun from(orderProduct: OrderProduct): OrderProductResponse =
            with(orderProduct) {
                OrderProductResponse(
                    id = id,
                    created = created,
                    modified = modified,
                    isRemoved = isRemoved,
                    orderId = orderId,
                    code = code,
                    name = name,
                    subtitle = subtitle,
                    listPrice = listPrice,
                    sellingPrice = sellingPrice,
                    quantity = quantity
                )
            }
    }
}