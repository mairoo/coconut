package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.EmailAddress

interface EmailAddressRepository {
    fun save(
        emailAddress: EmailAddress,
    ): EmailAddress
}