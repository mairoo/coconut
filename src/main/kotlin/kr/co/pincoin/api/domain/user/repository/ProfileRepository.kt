package kr.co.pincoin.api.domain.user.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.user.model.Profile

interface ProfileRepository {
    fun save(
        profile: Profile,
    ): Profile
}