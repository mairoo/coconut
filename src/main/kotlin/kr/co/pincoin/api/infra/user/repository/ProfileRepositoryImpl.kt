package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.Profile
import kr.co.pincoin.api.domain.user.repository.ProfileRepository
import org.springframework.stereotype.Repository

@Repository
class ProfileRepositoryImpl(
    private val jpaRepository: ProfileJpaRepository,
    private val queryRepository: ProfileQueryRepository,
) : ProfileRepository {
    override fun save(profile: Profile): Profile {
        TODO("Not yet implemented")
    }
}