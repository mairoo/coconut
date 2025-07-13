package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.ProfileEntity
import kr.pincoin.api.infra.user.entity.QProfileEntity
import org.springframework.stereotype.Repository

@Repository
class ProfileQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProfileQueryRepository {
    private val profile = QProfileEntity.profileEntity

    override fun findById(id: Long): ProfileEntity? =
        queryFactory
            .selectFrom(profile)
            .where(profile.id.eq(id))
            .fetchOne()
}