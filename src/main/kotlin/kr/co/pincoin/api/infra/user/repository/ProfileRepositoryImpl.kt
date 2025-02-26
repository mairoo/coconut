package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.repository.ProfileRepository
import org.springframework.stereotype.Repository

@Repository
class ProfileRepositoryImpl(
    private val jpaRepository: ProfileJpaRepository,
) : ProfileRepository {
}