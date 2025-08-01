package kr.pincoin.api.domain.inventory.service

import kr.pincoin.api.domain.inventory.error.VoucherErrorCode
import kr.pincoin.api.domain.inventory.model.Voucher
import kr.pincoin.api.domain.inventory.repository.VoucherRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class VoucherService(
    private val voucherRepository: VoucherRepository,
) {
    fun findVoucher(
        voucherId: Long,
        criteria: VoucherSearchCriteria,
    ): Voucher =
        voucherRepository.findVoucher(voucherId, criteria)
            ?: throw BusinessException(VoucherErrorCode.NOT_FOUND)

    fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): Voucher =
        voucherRepository.findVoucher(criteria)
            ?: throw BusinessException(VoucherErrorCode.NOT_FOUND)

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher> =
        voucherRepository.findVouchers(criteria, pageable)

    @Transactional
    fun createVoucher(voucher: Voucher): Voucher =
        voucherRepository.save(voucher)
}