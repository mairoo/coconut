package kr.co.pincoin.api.infra.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.domain.user.enums.ProfileDomestic
import kr.co.pincoin.api.domain.user.enums.ProfileGender
import kr.co.pincoin.api.domain.user.enums.ProfilePhoneVerifiedStatus
import kr.co.pincoin.api.infra.user.entity.ProfileEntity
import kr.co.pincoin.api.infra.user.entity.QProfileEntity
import kr.co.pincoin.api.infra.user.repository.criteria.ProfileSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProfileQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProfileQueryRepository {
    private val profile = QProfileEntity.profileEntity

    override fun findProfile(
        criteria: ProfileSearchCriteria,
    ): ProfileEntity? {
        val identifierConditions = listOfNotNull(
            criteria.id,
            criteria.userId,
            criteria.phone
        )
        require(identifierConditions.size == 1) { "검색 조건은 하나만 지정해야 합니다." }

        return queryFactory
            .selectFrom(profile)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()
    }

    override fun findProfiles(
        criteria: ProfileSearchCriteria,
    ): List<ProfileEntity> =
        queryFactory
            .selectFrom(profile)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(profile.id.desc())
            .fetch()

    override fun findProfiles(
        criteria: ProfileSearchCriteria,
        pageable: Pageable,
    ): Page<ProfileEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(profile) }

    private fun <T> executePageQuery(
        criteria: ProfileSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(profile)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(profile.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(profile.count())
                .from(profile)
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
        criteria: ProfileSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqProfileId(criteria.id),
        eqUserId(criteria.userId),
        eqPhone(criteria.phone),
        eqPhoneVerified(criteria.phoneVerified),
        eqDocumentVerified(criteria.documentVerified),
        eqPhoneVerifiedStatus(criteria.phoneVerifiedStatus),
        eqDomestic(criteria.domestic),
        eqGender(criteria.gender),
        eqTelecom(criteria.telecom),
        containsMemo(criteria.memo),
        eqNotPurchasedMonths(criteria.notPurchasedMonths),
        eqAllowOrder(criteria.allowOrder)
    )

    private fun eqProfileId(id: Long?): BooleanExpression? =
        id?.let { profile.id.eq(it) }

    private fun eqUserId(userId: Int?): BooleanExpression? =
        userId?.let { profile.userId.eq(it) }

    private fun eqPhone(phone: String?): BooleanExpression? =
        phone?.let { profile.phone.eq(it) }

    private fun eqPhoneVerified(phoneVerified: Boolean?): BooleanExpression? =
        phoneVerified?.let { profile.phoneVerified.eq(it) }

    private fun eqDocumentVerified(documentVerified: Boolean?): BooleanExpression? =
        documentVerified?.let { profile.documentVerified.eq(it) }

    private fun eqPhoneVerifiedStatus(phoneVerifiedStatus: ProfilePhoneVerifiedStatus?): BooleanExpression? =
        phoneVerifiedStatus?.let { profile.phoneVerifiedStatus.eq(it) }

    private fun eqDomestic(domestic: ProfileDomestic?): BooleanExpression? =
        domestic?.let { profile.domestic.eq(it) }

    private fun eqGender(gender: ProfileGender?): BooleanExpression? =
        gender?.let { profile.gender.eq(it) }

    private fun eqTelecom(telecom: String?): BooleanExpression? =
        telecom?.let { profile.telecom.eq(it) }

    private fun containsMemo(memo: String?): BooleanExpression? =
        memo?.let { profile.memo.contains(it) }

    private fun eqNotPurchasedMonths(notPurchasedMonths: Boolean?): BooleanExpression? =
        notPurchasedMonths?.let { profile.notPurchasedMonths.eq(it) }

    private fun eqAllowOrder(allowOrder: Boolean?): BooleanExpression? =
        allowOrder?.let { profile.allowOrder.eq(it) }
}