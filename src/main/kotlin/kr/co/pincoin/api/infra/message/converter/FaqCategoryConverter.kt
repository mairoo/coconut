package kr.co.pincoin.api.infra.message.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.message.enums.FaqCategory

@Converter
class FaqCategoryConverter : AttributeConverter<FaqCategory, Int> {
    override fun convertToDatabaseColumn(attribute: FaqCategory): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): FaqCategory =
        FaqCategory.from(dbData)
}