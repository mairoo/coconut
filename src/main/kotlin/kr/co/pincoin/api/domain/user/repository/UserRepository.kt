package kr.co.pincoin.api.domain.user.repository

import kr.co.pincoin.api.domain.user.model.User

interface UserRepository {
    fun save(
        user: User,
    ): User
}