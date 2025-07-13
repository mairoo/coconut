package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.QVoucherEntity
import kr.pincoin.api.infra.inventory.entity.VoucherEntity
import org.springframework.stereotype.Repository

@Repository
class VoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    ): VoucherQueryRepository {
    private val voucher = QVoucherEntity.voucherEntity

    override fun findById(
        id: Long,
    ): VoucherEntity? =
        queryFactory
            .selectFrom(voucher)
            .where(voucher.id.eq(id))
            .fetchOne()
}