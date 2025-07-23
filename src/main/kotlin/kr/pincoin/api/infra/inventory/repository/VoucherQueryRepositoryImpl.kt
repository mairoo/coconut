package kr.pincoin.api.infra.inventory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.pincoin.api.infra.inventory.entity.QVoucherEntity
import kr.pincoin.api.infra.inventory.entity.VoucherEntity
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class VoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : VoucherQueryRepository {
    private val voucher = QVoucherEntity.voucherEntity

    override fun findById(
        id: Long,
    ): VoucherEntity? =
        queryFactory
            .selectFrom(voucher)
            .where(voucher.id.eq(id))
            .fetchOne()

    override fun findVoucher(
        voucherId: Long,
        criteria: VoucherSearchCriteria,
    ): VoucherEntity? =
        queryFactory
            .selectFrom(voucher)
            .where(
                eqId(voucherId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): VoucherEntity? =
        queryFactory
            .selectFrom(voucher)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<VoucherEntity> =
        executeVoucherPageQuery(criteria, pageable)

    private fun executeVoucherPageQuery(
        criteria: VoucherSearchCriteria,
        pageable: Pageable
    ): Page<VoucherEntity> {
        // 결과 쿼리 실행
        val results = queryFactory
            .selectFrom(voucher)
            .where(*getCommonWhereConditions(criteria))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                voucher.productId.asc(),
                voucher.status.asc(),
                voucher.dateTimeFields.created.desc(),
                voucher.code.asc()
            )
            .fetch()

        // 카운트 쿼리 함수
        val countQuery = {
            queryFactory
                .select(voucher.count())
                .from(voucher)
                .where(*getCommonWhereConditions(criteria))
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
    }

    private fun getCommonWhereConditions(
        criteria: VoucherSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqCode(criteria.code),
        likeRemarks(criteria.remarks),
        eqStatus(criteria.status),
        eqProductId(criteria.productId),
        eqIsRemoved(criteria.isRemoved),
    )

    private fun eqId(voucherId: Long?): BooleanExpression? =
        voucherId?.let { voucher.id.eq(it) }

    private fun eqCode(code: String?): BooleanExpression? =
        code?.let { voucher.code.eq(it) }

    private fun likeRemarks(remarks: String?): BooleanExpression? =
        remarks?.let { voucher.remarks.contains(it) }

    private fun eqStatus(status: VoucherStatus?): BooleanExpression? =
        status?.let { voucher.status.eq(it) }

    private fun eqProductId(productId: Long?): BooleanExpression? =
        productId?.let { voucher.productId.eq(it) }

    private fun eqIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { voucher.removalFields.isRemoved.eq(it) }
}