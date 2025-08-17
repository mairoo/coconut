package kr.pincoin.api.infra.inventory.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.inventory.enums.ProductStatus

@Converter(autoApply = true)
class ProductStatusConverter : AttributeConverter<ProductStatus, Int> {
    override fun convertToDatabaseColumn(attribute: ProductStatus?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): ProductStatus? =
        dbData?.let { ProductStatus.fromValue(it) }
}