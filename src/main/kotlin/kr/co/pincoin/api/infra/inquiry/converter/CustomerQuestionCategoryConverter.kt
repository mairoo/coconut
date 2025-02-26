package kr.co.pincoin.api.infra.inquiry.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.inquiry.enums.CustomerQuestionCategory

@Converter
class CustomerQuestionCategoryConverter : AttributeConverter<CustomerQuestionCategory, Int> {
    override fun convertToDatabaseColumn(attribute: CustomerQuestionCategory): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): CustomerQuestionCategory =
        CustomerQuestionCategory.from(dbData)
}