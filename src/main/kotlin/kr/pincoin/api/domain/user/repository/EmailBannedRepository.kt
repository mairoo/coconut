package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.EmailBanned

interface EmailBannedRepository {
    fun save(
        emailBanned: EmailBanned,
    ): EmailBanned

    fun findById(
        id: Long,
    ): EmailBanned?
}