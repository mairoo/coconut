package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.EmailConfirmation

interface EmailConfirmationRepository {
    fun save(
        emailConfirmation: EmailConfirmation,
    ): EmailConfirmation
}