package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.EmailConfirmationRepository
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationRepositoryImpl(
    private val jpaRepository: EmailConfirmationJpaRepository,
    private val queryRepository: EmailConfirmationQueryRepository,
) : EmailConfirmationRepository {
}