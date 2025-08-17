package kr.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.order.enums.OrderVisible

@Converter(autoApply = true)
class OrderVisibleConverter : AttributeConverter<OrderVisible, Int> {
    override fun convertToDatabaseColumn(attribute: OrderVisible?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): OrderVisible? =
        dbData?.let { OrderVisible.fromValue(it) }
}