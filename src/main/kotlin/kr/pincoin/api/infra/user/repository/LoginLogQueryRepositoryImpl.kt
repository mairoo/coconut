package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.LoginLogEntity
import kr.pincoin.api.infra.user.entity.QLoginLogEntity
import org.springframework.stereotype.Repository

@Repository
class LoginLogQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : LoginLogQueryRepository {
    private val loginLog = QLoginLogEntity.loginLogEntity

    override fun findById(
        id: Long,
    ): LoginLogEntity? =
        queryFactory
            .selectFrom(loginLog)
            .where(loginLog.id.eq(id))
            .fetchOne()
}