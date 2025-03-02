package kr.co.pincoin.api.infra.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.user.entity.LoginLogEntity
import kr.co.pincoin.api.infra.user.entity.QLoginLogEntity
import kr.co.pincoin.api.infra.user.repository.criteria.LoginLogSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.net.InetAddress
import java.time.ZonedDateTime

@Repository
class LoginLogQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : LoginLogQueryRepository {
    private val loginLog = QLoginLogEntity.loginLogEntity

    override fun findLoginLog(
        loginLogId: Long,
        criteria: LoginLogSearchCriteria,
    ): LoginLogEntity? =
        queryFactory
            .select(loginLog)
            .from(loginLog)
            .where(
                eqLoginLogId(loginLogId),
                *getCommonWhereConditions(criteria)
            )
            .fetchOne()

    override fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
    ): List<LoginLogEntity> =
        queryFactory
            .select(loginLog)
            .from(loginLog)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(loginLog.created.desc(), loginLog.id.desc())
            .fetch()

    override fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
    ): Page<LoginLogEntity> = executePageQuery(
        criteria,
        pageable
    ) { baseQuery ->
        baseQuery.select(loginLog)
    }

    private fun <T> executePageQuery(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .select(loginLog)
            .from(loginLog)
            .where(*whereConditions)

        val query = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(loginLog.created.desc(), loginLog.id.desc())

        val results = query.fetch()

        val countQuery = {
            queryFactory
                .select(loginLog.count())
                .from(loginLog)
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
        criteria: LoginLogSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqLoginLogEmail(criteria.email),
        eqLoginLogUsername(criteria.username),
        eqLoginLogIpAddress(criteria.ipAddress),
        eqLoginLogUserId(criteria.userId),
        eqLoginLogIsSuccessful(criteria.isSuccessful),
        eqLoginLogReason(criteria.reason),
        betweenLoginLogCreated(criteria.createdFrom, criteria.createdTo)
    )

    private fun eqLoginLogId(id: Long): BooleanExpression =
        loginLog.id.eq(id)

    private fun eqLoginLogEmail(email: String?): BooleanExpression? =
        email?.let { loginLog.email.eq(it) }

    private fun eqLoginLogUsername(username: String?): BooleanExpression? =
        username?.let { loginLog.username.eq(it) }

    private fun eqLoginLogIpAddress(ipAddress: InetAddress?): BooleanExpression? =
        ipAddress?.let { loginLog.ipAddress.eq(it) }

    private fun eqLoginLogUserId(userId: Int?): BooleanExpression? =
        userId?.let { loginLog.userId.eq(it) }

    private fun eqLoginLogIsSuccessful(isSuccessful: Boolean?): BooleanExpression? =
        isSuccessful?.let { loginLog.isSuccessful.eq(it) }

    private fun eqLoginLogReason(reason: String?): BooleanExpression? =
        reason?.let { loginLog.reason.eq(it) }

    private fun betweenLoginLogCreated(
        createdFrom: ZonedDateTime?,
        createdTo: ZonedDateTime?
    ): BooleanExpression? {
        return when {
            createdFrom != null && createdTo != null -> loginLog.created.between(
                createdFrom,
                createdTo
            )

            createdFrom != null -> loginLog.created.goe(createdFrom)
            createdTo != null -> loginLog.created.loe(createdTo)
            else -> null
        }
    }
}