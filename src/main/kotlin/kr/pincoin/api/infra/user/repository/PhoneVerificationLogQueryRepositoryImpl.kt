package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.PhoneVerificationLogEntity
import kr.pincoin.api.infra.user.entity.QPhoneVerificationLogEntity
import org.springframework.stereotype.Repository

@Repository
class PhoneVerificationLogQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PhoneVerificationLogQueryRepository {
    private val phoneVerificationLog = QPhoneVerificationLogEntity.phoneVerificationLogEntity

    override fun findById(
        id: Long,
    ): PhoneVerificationLogEntity? =
        queryFactory
            .selectFrom(phoneVerificationLog)
            .where(phoneVerificationLog.id.eq(id))
            .fetchOne()
}