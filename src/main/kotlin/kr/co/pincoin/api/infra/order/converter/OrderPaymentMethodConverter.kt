package kr.co.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod

@Converter
class OrderPaymentMethodConverter : AttributeConverter<OrderPaymentMethod, Int> {
    override fun convertToDatabaseColumn(attribute: OrderPaymentMethod): Int {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: Int): OrderPaymentMethod {
        return OrderPaymentMethod.from(dbData)
    }
}