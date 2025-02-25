package kr.co.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.order.enums.OrderCurrency

@Converter
class OrderCurrencyConverter : AttributeConverter<OrderCurrency, String> {
    override fun convertToDatabaseColumn(attribute: OrderCurrency): String {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: String): OrderCurrency {
        return OrderCurrency.from(dbData)
    }
}