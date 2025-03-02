package kr.co.pincoin.api.domain.user.repository

import kr.co.pincoin.api.domain.user.model.Profile
import kr.co.pincoin.api.infra.user.repository.criteria.ProfileSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProfileRepository {
    fun save(
        profile: Profile,
    ): Profile

    fun findProfile(
        criteria: ProfileSearchCriteria,
    ): Profile?

    fun findProfiles(
        criteria: ProfileSearchCriteria,
    ): List<Profile>

    fun findProfiles(
        criteria: ProfileSearchCriteria,
        pageable: Pageable,
    ): Page<Profile>
}