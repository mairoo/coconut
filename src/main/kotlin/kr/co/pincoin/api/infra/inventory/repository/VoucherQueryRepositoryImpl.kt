package kr.co.pincoin.api.infra.inventory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.co.pincoin.api.infra.inventory.entity.QVoucherEntity
import kr.co.pincoin.api.infra.inventory.entity.VoucherEntity
import kr.co.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class VoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : VoucherQueryRepository {
    private val voucher = QVoucherEntity.voucherEntity

    override fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): VoucherEntity? =
        queryFactory
            .selectFrom(voucher)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
    ): List<VoucherEntity> =
        queryFactory
            .selectFrom(voucher)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(voucher.dateTimeFields.created.desc(), voucher.id.desc())
            .fetch()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<VoucherEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(voucher) }

    private fun <T> executePageQuery(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(voucher)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(voucher.dateTimeFields.created.desc(), voucher.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(voucher.count())
                .from(voucher)
                .where(*whereConditions)
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
    }

    private fun getCommonWhereConditions(
        criteria: VoucherSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqVoucherId(criteria.id),
        eqVoucherCode(criteria.code),
        eqVoucherRemarks(criteria.remarks),
        eqVoucherProductId(criteria.productId),
        eqVoucherStatus(criteria.status),
        eqVoucherIsRemoved(criteria.isRemoved),
    )

    private fun eqVoucherId(id: Long?): BooleanExpression? =
        id?.let { voucher.id.eq(it) }

    private fun eqVoucherCode(code: String?): BooleanExpression? =
        code?.let { voucher.code.eq(it) }

    private fun eqVoucherRemarks(remarks: String?): BooleanExpression? =
        remarks?.let { voucher.remarks.eq(it) }

    private fun eqVoucherProductId(productId: Long?): BooleanExpression? =
        productId?.let { voucher.productId.eq(it) }

    private fun eqVoucherStatus(status: VoucherStatus?): BooleanExpression? =
        status?.let { voucher.status.eq(it) }

    private fun eqVoucherIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { voucher.removalFields.isRemoved.eq(it) }
}