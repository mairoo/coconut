package kr.co.pincoin.api.infra.catalog.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus

@Converter
class ProductStatusConverter : AttributeConverter<ProductStatus, Int> {
    override fun convertToDatabaseColumn(attribute: ProductStatus): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): ProductStatus =
        ProductStatus.from(dbData)
}