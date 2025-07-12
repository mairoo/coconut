package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.SocialAppRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAppRepositoryImpl(
    private val jpaRepository: SocialAppJpaRepository,
    private val queryRepository: SocialAppQueryRepository,
) : SocialAppRepository {
}