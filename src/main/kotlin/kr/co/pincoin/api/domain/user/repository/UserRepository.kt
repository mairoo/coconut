package kr.co.pincoin.api.domain.user.repository

import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository {
    fun save(
        user: User,
    ): User

    fun findUser(
        id: Int,
        criteria: UserSearchCriteria,
    ): User?

    fun findUser(
        criteria: UserSearchCriteria,
    ): User?

    fun findUsers(
        criteria: UserSearchCriteria,
    ): List<User>

    fun findUsers(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<User>
}