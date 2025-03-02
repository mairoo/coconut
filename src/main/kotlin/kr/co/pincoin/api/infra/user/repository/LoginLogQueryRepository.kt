package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.infra.user.entity.LoginLogEntity
import kr.co.pincoin.api.infra.user.repository.criteria.LoginLogSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LoginLogQueryRepository {
    fun findLoginLog(
        loginLogId: Long,
        criteria: LoginLogSearchCriteria,
    ): LoginLogEntity?

    fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
    ): Page<LoginLogEntity>
}