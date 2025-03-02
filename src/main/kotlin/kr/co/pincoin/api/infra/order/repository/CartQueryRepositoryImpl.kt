package kr.co.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.order.entity.CartEntity
import kr.co.pincoin.api.infra.order.entity.QCartEntity
import kr.co.pincoin.api.infra.order.repository.criteria.CartSearchCriteria
import org.springframework.stereotype.Repository

@Repository
class CartQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CartQueryRepository {
    private val cart = QCartEntity.cartEntity

    override fun findCart(
        criteria: CartSearchCriteria,
    ): CartEntity? =
        queryFactory
            .select(cart)
            .from(cart)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    private fun getCommonWhereConditions(
        criteria: CartSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqCartId(criteria.id),
        eqCartUserId(criteria.userId)
    )

    private fun eqCartId(id: Long?): BooleanExpression? =
        id?.let { cart.id.eq(it) }

    private fun eqCartUserId(userId: Int?): BooleanExpression? =
        userId?.let { cart.userId.eq(it) }
}