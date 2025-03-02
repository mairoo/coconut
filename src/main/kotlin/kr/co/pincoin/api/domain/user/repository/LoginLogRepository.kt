package kr.co.pincoin.api.domain.user.repository

import kr.co.pincoin.api.domain.user.model.LoginLog
import kr.co.pincoin.api.infra.user.repository.criteria.LoginLogSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LoginLogRepository {
    fun save(loginLog: LoginLog): LoginLog

    fun findLoginLog(
        loginLogId: Long,
        criteria: LoginLogSearchCriteria,
    ): LoginLog?

    fun findLoginLogs(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
    ): Page<LoginLog>
}