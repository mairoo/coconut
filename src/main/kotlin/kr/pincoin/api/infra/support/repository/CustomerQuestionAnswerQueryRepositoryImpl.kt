package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.CustomerQuestionAnswerEntity
import kr.pincoin.api.infra.support.entity.QCustomerQuestionAnswerEntity
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionAnswerQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CustomerQuestionAnswerQueryRepository {
    private val customerQuestionAnswer = QCustomerQuestionAnswerEntity.customerQuestionAnswerEntity

    override fun findById(
        id: Long,
    ): CustomerQuestionAnswerEntity? =
        queryFactory
            .selectFrom(customerQuestionAnswer)
            .where(customerQuestionAnswer.id.eq(id))
            .fetchOne()
}