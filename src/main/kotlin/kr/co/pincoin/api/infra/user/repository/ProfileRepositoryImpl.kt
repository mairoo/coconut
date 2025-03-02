package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.Profile
import kr.co.pincoin.api.domain.user.repository.ProfileRepository
import kr.co.pincoin.api.infra.user.mapper.toEntity
import kr.co.pincoin.api.infra.user.mapper.toModel
import kr.co.pincoin.api.infra.user.mapper.toModelList
import kr.co.pincoin.api.infra.user.repository.criteria.ProfileSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    override fun findProfile(
        criteria: ProfileSearchCriteria,
    ): Profile? =
        queryRepository.findProfile(criteria)?.toModel()

    override fun findProfiles(
        criteria: ProfileSearchCriteria,
    ): List<Profile> =
        queryRepository.findProfiles(criteria).toModelList()

    override fun findProfiles(
        criteria: ProfileSearchCriteria,
        pageable: Pageable,
    ): Page<Profile> =
        queryRepository.findProfiles(criteria, pageable).map { it.toModel() }
}