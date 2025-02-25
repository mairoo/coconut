package kr.co.pincoin.api.infra.inventory.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus

@Converter
class VoucherStatusConverter : AttributeConverter<VoucherStatus, Int> {
    override fun convertToDatabaseColumn(
        attribute: VoucherStatus?,
    ): Int =
        attribute?.value ?: VoucherStatus.PURCHASED.value

    override fun convertToEntityAttribute(
        dbData: Int?,
    ): VoucherStatus =
        when (dbData) {
            VoucherStatus.SOLD.value -> VoucherStatus.SOLD
            VoucherStatus.REVOKED.value -> VoucherStatus.REVOKED
            else -> VoucherStatus.PURCHASED
        }
}