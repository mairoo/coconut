package kr.co.pincoin.api.infra.user.repository

import kr.co.pincoin.api.domain.user.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,
) : UserRepository {
}