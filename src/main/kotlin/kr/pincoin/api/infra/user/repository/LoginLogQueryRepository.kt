package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.LoginLogEntity

interface LoginLogQueryRepository {
    fun findById(
        id: Long,
    ): LoginLogEntity?
}