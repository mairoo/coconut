package kr.co.pincoin.api.domain.user.service

import kr.co.pincoin.api.domain.user.model.LoginLog
import kr.co.pincoin.api.domain.user.repository.LoginLogRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.UserErrorCode
import kr.co.pincoin.api.infra.user.repository.criteria.LoginLogSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LoginLogService(
    private val loginLogRepository: LoginLogRepository,
) {
    @Transactional
    fun save(loginLog: LoginLog): LoginLog {
        return loginLogRepository.save(loginLog)
    }

    fun findLoginLog(
        loginLogId: Long,
        criteria: LoginLogSearchCriteria,
    ): LoginLog =
        loginLogRepository.findLoginLog(
            loginLogId,
            criteria = criteria,
        )
            ?: throw BusinessException(UserErrorCode.LOGIN_LOG_NOT_FOUND)

    fun findLoginLogList(
        criteria: LoginLogSearchCriteria,
        pageable: Pageable,
    ): Page<LoginLog> =
        loginLogRepository.findLoginLogs(
            criteria = criteria,
            pageable = pageable,
        )
}