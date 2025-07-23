package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.SocialAppSitesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialAppSitesJpaRepository : JpaRepository<SocialAppSitesEntity, Int> {
}