package kr.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.order.enums.OrderPaymentMethod

@Converter(autoApply = true)
class OrderPaymentMethodConverter : AttributeConverter<OrderPaymentMethod, Int> {
    override fun convertToDatabaseColumn(attribute: OrderPaymentMethod?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): OrderPaymentMethod? =
        dbData?.let { OrderPaymentMethod.fromValue(it) }
}