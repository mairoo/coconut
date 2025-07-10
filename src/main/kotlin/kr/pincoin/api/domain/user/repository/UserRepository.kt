package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria

interface UserRepository {
    fun save(
        user: User,
    ): User

    fun findById(
        id: Int,
    ): User?

    fun findUser(
        userId: Int,
        criteria: UserSearchCriteria,
    ): User?

    fun findUser(
        criteria: UserSearchCriteria,
    ): User?

    fun existsByEmail(
        email: String,
    ): Boolean
}