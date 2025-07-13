package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.domain.user.repository.ProfileRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
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
            ?: throw IllegalArgumentException("사용자 저장 실패")

    override fun findById(
        id: Long,
    ): Profile? =
        queryRepository.findById(id)?.toModel()
}