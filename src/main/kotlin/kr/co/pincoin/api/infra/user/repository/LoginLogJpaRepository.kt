package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.infra.user.entity.LoginLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoginLogJpaRepository : JpaRepository<LoginLogEntity, Long> {
}