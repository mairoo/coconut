package kr.pincoin.api.infra.inventory.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.inventory.enums.ProductStock

@Converter(autoApply = true)
class ProductStockConverter : AttributeConverter<ProductStock, Int> {
    override fun convertToDatabaseColumn(attribute: ProductStock?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): ProductStock? =
        dbData?.let { ProductStock.fromValue(it) }
}