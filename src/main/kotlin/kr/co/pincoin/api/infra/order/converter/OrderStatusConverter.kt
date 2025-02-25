package kr.co.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.order.enums.OrderStatus

@Converter
class OrderStatusConverter : AttributeConverter<OrderStatus, Int> {
    override fun convertToDatabaseColumn(attribute: OrderStatus): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): OrderStatus =
        OrderStatus.from(dbData)
}