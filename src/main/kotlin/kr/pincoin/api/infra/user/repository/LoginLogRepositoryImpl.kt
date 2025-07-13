package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.LoginLog
import kr.pincoin.api.domain.user.repository.LoginLogRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
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

    override fun findById(
        id: Long,
    ): LoginLog? =
        queryRepository.findById(id)?.toModel()
}