package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.User

interface UserRepository {
    fun save(user: User): User

    fun findById(id: Long): User?

    fun existsByEmail(email: String): Boolean
}