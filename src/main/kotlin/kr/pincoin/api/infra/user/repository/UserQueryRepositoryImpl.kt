package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.QUserEntity
import kr.pincoin.api.infra.user.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserQueryRepository {
    private val user = QUserEntity.userEntity

    override fun findById(id: Long): UserEntity? =
        queryFactory
            .selectFrom(user)
            .where(user.id.eq(id))
            .fetchOne()
}