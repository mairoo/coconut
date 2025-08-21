package kr.pincoin.api.infra.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.QProfileEntity
import kr.pincoin.api.infra.user.entity.QUserEntity
import kr.pincoin.api.infra.user.entity.UserEntity
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.QUserProfileProjection
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserQueryRepository {
    private val user = QUserEntity.userEntity
    private val profile = QProfileEntity.profileEntity

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
            criteria.email,
            criteria.keycloakId,
        )
        require(identifierConditions.size == 1) { "검색 조건은 하나만 지정해야 합니다." }

        return queryFactory
            .selectFrom(user)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()
    }

    override fun findUserWithProfile(
        userId: Int,
        criteria: UserSearchCriteria,
    ): UserProfileProjection? =
        queryFactory
            .select(createUserProfileProjection())
            .from(user)
            .innerJoin(profile).on(user.id.eq(profile.userId))
            .where(
                eqId(userId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findUserWithProfile(
        criteria: UserSearchCriteria,
    ): UserProfileProjection? {
        val identifierConditions = listOfNotNull(
            criteria.username,
            criteria.email
        )
        require(identifierConditions.size == 1) { "검색 조건은 하나만 지정해야 합니다." }

        return queryFactory
            .select(createUserProfileProjection())
            .from(user)
            .innerJoin(profile).on(user.id.eq(profile.userId))
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()
    }

    override fun findUsersWithProfile(
        criteria: UserSearchCriteria
    ): List<UserProfileProjection> =
        createUserWithProfileBaseQuery(criteria)
            .select(createUserProfileProjection())
            .orderBy(
                user.dateJoined.desc(),
                user.username.asc()
            )
            .fetch()

    override fun findUsersWithProfile(
        criteria: UserSearchCriteria,
        pageable: Pageable
    ): Page<UserProfileProjection> = executeUserWithProfilePageQuery(
        criteria,
        pageable
    ) { baseQuery -> baseQuery.select(createUserProfileProjection()) }

    private fun createUserProfileProjection() = QUserProfileProjection(
        // User 필드
        user.id,
        user.username,
        user.firstName,
        user.lastName,
        user.email,
        user.isActive,
        user.isStaff,
        user.isSuperuser,
        user.dateJoined,
        user.lastLogin,

        // Profile 필드
        profile.id,
        profile.dateTimeFields.created,
        profile.dateTimeFields.modified,
        profile.address,
        profile.phone,
        profile.phoneVerified,
        profile.phoneVerifiedStatus,
        profile.dateOfBirth,
        profile.domestic,
        profile.gender,
        profile.telecom,
        profile.photoId,
        profile.card,
        profile.documentVerified,
        profile.totalOrderCount,
        profile.firstPurchased,
        profile.lastPurchased,
        profile.maxPrice,
        profile.averagePrice,
        profile.totalListPrice,
        profile.totalSellingPrice,
        profile.notPurchasedMonths,
        profile.repurchased,
        profile.memo,
        profile.mileage,
        profile.allowOrder
    )

    private fun createUserWithProfileBaseQuery(
        criteria: UserSearchCriteria
    ): JPAQuery<*> =
        queryFactory
            .from(user)
            .innerJoin(profile).on(user.id.eq(profile.userId))
            .where(*getCommonWhereConditions(criteria))

    private fun <T> executeUserWithProfilePageQuery(
        criteria: UserSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        // 결과 쿼리 실행
        val results = selectClause(createUserWithProfileBaseQuery(criteria))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                user.dateJoined.desc(),
                user.username.asc()
            )
            .fetch()

        // 카운트 쿼리 함수
        val countQuery = {
            createUserWithProfileBaseQuery(criteria)
                .select(user.count())
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
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
        eqKeycloakId(criteria.keycloakId),
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
        email?.let { user.email.eq(it) }

    private fun eqIsActive(isActive: Boolean?): BooleanExpression? =
        isActive?.let { user.isActive.eq(it) }

    private fun eqIsSuperuser(isSuperuser: Boolean?): BooleanExpression? =
        isSuperuser?.let { user.isSuperuser.eq(it) }

    private fun eqKeycloakId(keycloakId: UUID?): BooleanExpression? =
        keycloakId?.let { user.keycloakId.eq(it) }
}