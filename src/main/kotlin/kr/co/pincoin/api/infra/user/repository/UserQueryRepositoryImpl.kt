package kr.co.pincoin.api.infra.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.user.entity.QUserEntity
import kr.co.pincoin.api.infra.user.entity.UserEntity
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserQueryRepository {
    private val user = QUserEntity.userEntity

    override fun findUser(
        id: Int,
        criteria: UserSearchCriteria,
    ): UserEntity? =
        queryFactory
            .selectFrom(user)
            .where(
                eqUserId(id),
                *getCommonWhereConditions(criteria)
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

    override fun findUsers(
        criteria: UserSearchCriteria,
    ): List<UserEntity> =
        queryFactory
            .selectFrom(user)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(user.id.desc())
            .fetch()

    override fun findUsers(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(user) }

    private fun <T> executePageQuery(
        criteria: UserSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(user)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(user.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(user.count())
                .from(user)
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
        criteria: UserSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqUserEmail(criteria.email),
        eqUsername(criteria.username),
        eqUserIsActive(criteria.isActive),
        eqUserIsSuperuser(criteria.isSuperuser)
    )

    private fun eqUserId(id: Int): BooleanExpression =
        user.id.eq(id)

    private fun eqUserEmail(email: String?): BooleanExpression? =
        email?.let { user.email.eq(it) }

    private fun eqUsername(username: String?): BooleanExpression? =
        username?.let { user.username.eq(it) }

    private fun eqUserIsActive(isActive: Boolean?): BooleanExpression? =
        isActive?.let { user.isActive.eq(it) }

    private fun eqUserIsSuperuser(isSuperuser: Boolean?): BooleanExpression? =
        isSuperuser?.let { user.isSuperuser.eq(it) }
}