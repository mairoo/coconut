package kr.co.pincoin.api.infra.message.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.message.enums.NoticeMessageCategory

@Converter
class NoticeMessageCategoryConverter : AttributeConverter<NoticeMessageCategory, Int> {
    override fun convertToDatabaseColumn(attribute: NoticeMessageCategory): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): NoticeMessageCategory =
        NoticeMessageCategory.from(dbData)
}