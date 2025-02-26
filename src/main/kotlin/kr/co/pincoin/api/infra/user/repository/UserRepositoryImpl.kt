package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.domain.user.repository.UserRepository
import kr.co.pincoin.api.infra.user.mapper.toEntity
import kr.co.pincoin.api.infra.user.mapper.toModel
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
}