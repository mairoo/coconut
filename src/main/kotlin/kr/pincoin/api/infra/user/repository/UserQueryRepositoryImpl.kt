package kr.pincoin.api.infra.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.QUserEntity
import kr.pincoin.api.infra.user.entity.UserEntity
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserQueryRepository {
    private val user = QUserEntity.userEntity

    override fun findById(id: Int): UserEntity? =
        queryFactory
            .selectFrom(user)
            .where(user.id.eq(id))
            .fetchOne()

    override fun findUser(
        userId: Int,
        criteria: UserSearchCriteria,
    ): UserEntity? =
        queryFactory
            .selectFrom(user)
            .where(
                eqId(userId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findUser(
        criteria: UserSearchCriteria,
    ): UserEntity? {
        val identifierConditions = listOfNotNull(
            criteria.username,
            criteria.email
        )
        require(identifierConditions.size == 1) { "검색 조건은 하나만 지정해야 합니다." }

        return queryFactory
            .selectFrom(user)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()
    }

    private fun getCommonWhereConditions(
        criteria: UserSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqUsername(criteria.username),
        eqFirstName(criteria.firstName),
        eqLastName(criteria.lastName),
        eqEmail(criteria.email),
        eqIsActive(criteria.isActive),
        eqIsSuperuser(criteria.isSuperuser),
    )

    private fun eqId(userId: Int?): BooleanExpression? =
        userId?.let { user.id.eq(it) }

    private fun eqUsername(username: String?): BooleanExpression? =
        username?.let { user.username.eq(it) }

    private fun eqFirstName(firstName: String?): BooleanExpression? =
        firstName?.let { user.firstName.eq(it) }

    private fun eqLastName(lastName: String?): BooleanExpression? =
        lastName?.let { user.lastName.eq(it) }

    private fun eqEmail(email: String?): BooleanExpression? =
        email?.let { user.lastName.eq(it) }

    private fun eqIsActive(isActive: Boolean?): BooleanExpression? =
        isActive?.let { user.isActive.eq(it) }

    private fun eqIsSuperuser(isSuperuser: Boolean?): BooleanExpression? =
        isSuperuser?.let { user.isSuperuser.eq(it) }
}