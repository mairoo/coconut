package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.SocialTokenRepository
import org.springframework.stereotype.Repository

@Repository
class SocialTokenRepositoryImpl(
    private val jpaRepository: SocialTokenJpaRepository,
    private val queryRepository: SocialTokenQueryRepository,
) : SocialTokenRepository {
}