package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.LoginLog

interface LoginLogRepository {
    fun save(loginLog: LoginLog): LoginLog
}