package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.LoginLog
import kr.co.pincoin.api.domain.user.repository.LoginLogRepository
import kr.co.pincoin.api.infra.user.mapper.toEntity
import kr.co.pincoin.api.infra.user.mapper.toModel
import kr.co.pincoin.api.infra.user.mapper.toModelList
import kr.co.pincoin.api.infra.user.repository.criteria.LoginLogSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class LoginLogRepositoryImpl(
    private val jpaRepository: LoginLogJpaRepository,
    private val queryRepository: LoginLogQueryRepository,
) : LoginLogRepository {
    override fun save(
        loginLog: LoginLog,
    ): LoginLog =
        loginLog.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("로그인 로그 저장 실패")

    override fun findLoginLog(
        loginLogId: Long,
        criteria: LoginLogSearchCriteria,
    ): LoginLog? =
        queryRepository.findLoginLog(loginLogId, criteria)?.toModel()

    override fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
    ): List<LoginLog> =
        queryRepository.findLoginLogs(criteria).toModelList()


    override fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
    ): Page<LoginLog> =
        queryRepository.findLoginLogs(criteria, pageable).map { it.toModel()!! }
}