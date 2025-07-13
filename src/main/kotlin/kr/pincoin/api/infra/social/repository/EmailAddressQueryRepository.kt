package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.EmailAddressEntity

interface EmailAddressQueryRepository {
    fun findById(
        id: Int,
    ): EmailAddressEntity?
}