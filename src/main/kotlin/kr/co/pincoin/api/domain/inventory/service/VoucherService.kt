package kr.co.pincoin.api.domain.inventory.service

import kr.co.pincoin.api.domain.inventory.model.Voucher
import kr.co.pincoin.api.domain.inventory.repository.VoucherRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.InventoryErrorCode
import kr.co.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
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
        criteria: VoucherSearchCriteria,
    ): Voucher? = voucherRepository.findVoucher(criteria)

    fun findVouchers(
        criteria: VoucherSearchCriteria,
    ): List<Voucher> = voucherRepository.findVouchers(criteria)

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher> = voucherRepository.findVouchers(criteria, pageable)

    @Transactional
    fun save(
        voucher: Voucher,
    ): Voucher {
        try {
            return voucherRepository.save(voucher)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(InventoryErrorCode.VOUCHER_SAVE_FAILED)
        }
    }

    @Transactional
    fun saveAll(
        vouchers: List<Voucher>,
    ): List<Voucher> =
        voucherRepository.saveAll(vouchers)
}