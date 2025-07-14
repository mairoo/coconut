package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.repository.AuthTokenRepository
import org.springframework.stereotype.Repository

@Repository
class AuthTokenRepositoryImpl(
    private val jpaRepository: AuthTokenJpaRepository,
) : AuthTokenRepository {
}