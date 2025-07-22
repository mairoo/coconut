package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.Profile

interface ProfileRepository {
    fun save(
        profile: Profile,
    ): Profile

    fun findById(
        id: Long,
    ): Profile?
}