package kr.co.pincoin.api.infra.catalog.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.catalog.enums.ProductStock

@Converter
class ProductStockConverter : AttributeConverter<ProductStock, Int> {
    override fun convertToDatabaseColumn(attribute: ProductStock): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): ProductStock =
        ProductStock.from(dbData)
}