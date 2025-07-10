package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.UserEntity
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria

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
}