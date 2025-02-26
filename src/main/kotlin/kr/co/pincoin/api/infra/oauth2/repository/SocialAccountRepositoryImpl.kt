package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.repository.SocialAccountRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAccountRepositoryImpl(
    private val jpaRepository: SocialAccountJpaRepository,
) : SocialAccountRepository {
}