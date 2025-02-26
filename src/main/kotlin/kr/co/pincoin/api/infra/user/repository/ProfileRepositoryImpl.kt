package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.Profile
import kr.co.pincoin.api.domain.user.repository.ProfileRepository
import kr.co.pincoin.api.infra.user.mapper.toEntity
import kr.co.pincoin.api.infra.user.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class ProfileRepositoryImpl(
    private val jpaRepository: ProfileJpaRepository,
    private val queryRepository: ProfileQueryRepository,
) : ProfileRepository {
    override fun save(profile: Profile): Profile =
        profile.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("프로필 저장 실패")
}