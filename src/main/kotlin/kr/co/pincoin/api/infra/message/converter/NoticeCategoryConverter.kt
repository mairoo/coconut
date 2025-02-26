package kr.co.pincoin.api.infra.message.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.message.enums.NoticeCategory

@Converter
class NoticeCategoryConverter : AttributeConverter<NoticeCategory, Int> {
    override fun convertToDatabaseColumn(attribute: NoticeCategory): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): NoticeCategory =
        NoticeCategory.from(dbData)
}