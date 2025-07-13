package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.PhoneBannedEntity
import kr.pincoin.api.infra.user.entity.QPhoneBannedEntity
import org.springframework.stereotype.Repository

@Repository
class PhoneBannedQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PhoneBannedQueryRepository {
    private val phoneBanned = QPhoneBannedEntity.phoneBannedEntity

    override fun findById(id: Long): PhoneBannedEntity? =
        queryFactory
            .selectFrom(phoneBanned)
            .where(phoneBanned.id.eq(id))
            .fetchOne()
}