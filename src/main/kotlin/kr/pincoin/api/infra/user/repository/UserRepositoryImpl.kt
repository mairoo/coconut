package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,
    private val queryRepository: UserQueryRepository,
) : UserRepository {
    override fun save(user: User): User =
        user.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("사용자 저장 실패")

    override fun findById(
        id: Int,
    ): User? =
        queryRepository.findById(id)?.toModel()

    override fun findUser(
        userId: Int,
        criteria: UserSearchCriteria,
    ): User? =
        queryRepository.findUser(userId, criteria)?.toModel()

    override fun findUser(
        criteria: UserSearchCriteria,
    ): User? =
        queryRepository.findUser(criteria)?.toModel()

    override fun existsByEmail(email: String): Boolean =
        jpaRepository.existsByEmail(email)
}