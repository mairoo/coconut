package kr.co.pincoin.api.domain.inventory.service

import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus
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
    fun update(
        id: Long,
        code: String? = null,
        remarks: String? = null,
    ): Voucher =
        voucherRepository.findVoucher(
            VoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.update(code, remarks)
            ?.let { voucherRepository.save(it) }
            ?: throw BusinessException(InventoryErrorCode.VOUCHER_NOT_FOUND)

    @Transactional
    fun updateStatus(
        id: Long,
        status: VoucherStatus,
    ): Voucher =
        voucherRepository.findVoucher(
            VoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateStatus(status)
            ?.let { voucherRepository.save(it) }
            ?: throw BusinessException(InventoryErrorCode.VOUCHER_NOT_FOUND)

    @Transactional
    fun changeVoucherProduct(
        id: Long,
        productId: Long,
    ): Voucher =
        voucherRepository.findVoucher(
            VoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateProduct(productId)
            ?.let { voucherRepository.save(it) }
            ?: throw BusinessException(InventoryErrorCode.VOUCHER_NOT_FOUND)

    @Transactional
    fun removeVoucher(id: Long): Voucher =
        voucherRepository.findVoucher(
            VoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsRemoved()
            ?.let { voucherRepository.save(it) }
            ?: throw BusinessException(InventoryErrorCode.VOUCHER_NOT_FOUND)

    @Transactional
    fun updateVouchersStatus(
        ids: List<Long>,
        status: VoucherStatus,
    ): List<Voucher> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        val vouchers = ids.mapNotNull { id ->
            voucherRepository.findVoucher(
                VoucherSearchCriteria(id = id, isRemoved = false)
            )
        }

        if (vouchers.isEmpty()) {
            return emptyList()
        }

        val updatedVouchers = vouchers.map { it.updateStatus(status) }
        return saveAll(updatedVouchers)
    }

    @Transactional
    fun removeVouchers(ids: List<Long>): List<Voucher> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        val vouchers = ids.mapNotNull { id ->
            voucherRepository.findVoucher(
                VoucherSearchCriteria(id = id, isRemoved = false)
            )
        }

        if (vouchers.isEmpty()) {
            return emptyList()
        }

        val removedVouchers = vouchers.map { it.markAsRemoved() }
        return saveAll(removedVouchers)
    }

    @Transactional
    fun restoreVoucher(id: Long): Voucher {
        val voucher = voucherRepository.findVoucher(
            VoucherSearchCriteria(id = id, isRemoved = true)
        ) ?: throw BusinessException(InventoryErrorCode.VOUCHER_NOT_FOUND)

        val restoredVoucher = Voucher.of(
            id = voucher.id,
            created = voucher.created,
            modified = voucher.modified,
            isRemoved = false,
            code = voucher.code,
            remarks = voucher.remarks,
            productId = voucher.productId,
            status = voucher.status,
        )

        return save(restoredVoucher)
    }

    @Transactional
    fun bulkUpdateVoucherStatus(
        fromStatus: VoucherStatus,
        toStatus: VoucherStatus,
        productId: Long? = null
    ): Int {
        val criteria = VoucherSearchCriteria(
            status = fromStatus,
            productId = productId,
            isRemoved = false
        )

        val vouchers = voucherRepository.findVouchers(criteria)

        if (vouchers.isEmpty()) {
            return 0
        }

        val updatedVouchers = vouchers.map { it.updateStatus(toStatus) }
        saveAll(updatedVouchers)

        return updatedVouchers.size
    }
}