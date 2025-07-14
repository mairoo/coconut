package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

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

    fun findUserWithProfile(
        userId: Int,
        criteria: UserSearchCriteria,
    ): UserProfileProjection?

    fun findUserWithProfile(
        criteria: UserSearchCriteria,
    ): UserProfileProjection?

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
    ): List<UserProfileProjection>

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserProfileProjection>

    fun existsByEmail(
        email: String,
    ): Boolean
}