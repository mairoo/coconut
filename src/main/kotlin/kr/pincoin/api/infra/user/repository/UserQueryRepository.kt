package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.UserEntity
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserQueryRepository {
    fun findById(
        id: Int,
    ): UserEntity?

    fun findUser(
        userId: Int,
        criteria: UserSearchCriteria,
    ): UserEntity?

    fun findUser(
        criteria: UserSearchCriteria,
    ): UserEntity?

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

}