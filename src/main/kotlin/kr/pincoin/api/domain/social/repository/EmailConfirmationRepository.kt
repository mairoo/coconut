package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.EmailConfirmation

interface EmailConfirmationRepository {
    fun save(
        emailConfirmation: EmailConfirmation,
    ): EmailConfirmation

    fun findById(
        id: Int,
    ): EmailConfirmation?
}