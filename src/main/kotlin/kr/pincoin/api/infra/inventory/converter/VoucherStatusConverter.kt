package kr.pincoin.api.infra.inventory.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.inventory.enums.VoucherStatus

@Converter(autoApply = true)
class VoucherStatusConverter : AttributeConverter<VoucherStatus, Int> {
    override fun convertToDatabaseColumn(attribute: VoucherStatus?): Int? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: Int?): VoucherStatus? {
        return dbData?.let { VoucherStatus.fromValue(it) }
    }
}