package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.SocialAppSitesRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesRepositoryImpl(
    private val jpaRepository: SocialAppSitesJpaRepository,
    private val queryRepository: SocialAppSitesQueryRepository,
) : SocialAppSitesRepository {
}