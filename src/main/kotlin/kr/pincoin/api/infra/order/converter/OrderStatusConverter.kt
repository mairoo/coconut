package kr.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.order.enums.OrderStatus

@Converter(autoApply = true)
class OrderStatusConverter : AttributeConverter<OrderStatus, Int> {
    override fun convertToDatabaseColumn(attribute: OrderStatus?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): OrderStatus? =
        dbData?.let { OrderStatus.fromValue(it) }
}