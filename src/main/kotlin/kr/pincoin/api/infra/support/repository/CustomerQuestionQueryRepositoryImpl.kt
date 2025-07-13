package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.CustomerQuestionEntity
import kr.pincoin.api.infra.support.entity.QCustomerQuestionEntity
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CustomerQuestionQueryRepository {
    private val customerQuestion = QCustomerQuestionEntity.customerQuestionEntity

    override fun findById(
        id: Long,
    ): CustomerQuestionEntity? =
        queryFactory
            .selectFrom(customerQuestion)
            .where(customerQuestion.id.eq(id))
            .fetchOne()
}