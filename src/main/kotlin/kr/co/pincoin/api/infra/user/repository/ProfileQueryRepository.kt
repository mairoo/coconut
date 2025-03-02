package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.infra.user.entity.ProfileEntity
import kr.co.pincoin.api.infra.user.repository.criteria.ProfileSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProfileQueryRepository {
    fun findProfile(
        criteria: ProfileSearchCriteria,
    ): ProfileEntity?

    fun findProfiles(
        criteria: ProfileSearchCriteria,
    ): List<ProfileEntity>

    fun findProfiles(
        criteria: ProfileSearchCriteria,
        pageable: Pageable,
    ): Page<ProfileEntity>
}