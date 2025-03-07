package kr.co.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.order.enums.OrderVisibility

@Converter
class OrderVisibilityConverter : AttributeConverter<OrderVisibility, Int> {
    override fun convertToDatabaseColumn(attribute: OrderVisibility): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): OrderVisibility =
        OrderVisibility.from(dbData)
}