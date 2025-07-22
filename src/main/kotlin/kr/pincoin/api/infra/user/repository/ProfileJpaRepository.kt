package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.ProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileJpaRepository : JpaRepository<ProfileEntity, Int>