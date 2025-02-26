package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.domain.user.repository.UserRepository
import kr.co.pincoin.api.infra.user.mapper.toEntity
import kr.co.pincoin.api.infra.user.mapper.toModel
import kr.co.pincoin.api.infra.user.mapper.toModelList
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,
    private val queryRepository: UserQueryRepository,
) : UserRepository {
    override fun save(
        user: User,
    ): User =
        user.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("사용자 저장 실패")

    override fun findUser(
        id: Int,
        criteria: UserSearchCriteria,
    ): User? =
        queryRepository.findUser(id, criteria)?.toModel()

    override fun findUser(
        criteria: UserSearchCriteria,
    ): User? =
        queryRepository.findUser(criteria)?.toModel()


    override fun findUsers(
        criteria: UserSearchCriteria,
    ): List<User> =
        queryRepository.findUsers(criteria).toModelList()

    override fun findUsers(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<User> =
        queryRepository.findUsers(criteria, pageable).map { it.toModel() }
}