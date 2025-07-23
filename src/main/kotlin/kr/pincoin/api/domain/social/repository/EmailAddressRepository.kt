package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.EmailAddress

interface EmailAddressRepository {
    fun save(
        emailAddress: EmailAddress,
    ): EmailAddress

    fun findById(
        id: Int,
    ): EmailAddress?
}