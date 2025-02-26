package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.domain.user.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,
    private val queryRepository: UserQueryRepository,
) : UserRepository {
    override fun save(user: User): User {
        TODO("Not yet implemented")
    }
}