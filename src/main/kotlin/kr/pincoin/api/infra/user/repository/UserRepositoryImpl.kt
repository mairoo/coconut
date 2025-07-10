package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
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

    override fun findById(id: Long): User? =
        queryRepository.findById(id)?.toModel()

    override fun existsByEmail(email: String): Boolean =
        jpaRepository.existsByEmail(email)
}