package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.PhoneBanned

interface PhoneBannedRepository {
    fun save(
        phoneBanned: PhoneBanned,
    ): PhoneBanned

    fun findById(
        id: Long,
    ): PhoneBanned?
}