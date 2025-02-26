package kr.co.pincoin.api.infra.message.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.message.enums.FaqMessageCategory

@Converter
class FaqMessageCategoryConverter : AttributeConverter<FaqMessageCategory, Int> {
    override fun convertToDatabaseColumn(attribute: FaqMessageCategory): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): FaqMessageCategory =
        FaqMessageCategory.from(dbData)
}