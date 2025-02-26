package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.infra.user.entity.UserEntity
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserQueryRepository {
    fun findUser(
        id: Int,
        criteria: UserSearchCriteria,
    ): UserEntity?

    fun findUser(
        criteria: UserSearchCriteria,
    ): UserEntity?

    fun findUsers(
        criteria: UserSearchCriteria,
    ): List<UserEntity>

    fun findUsers(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserEntity>
}