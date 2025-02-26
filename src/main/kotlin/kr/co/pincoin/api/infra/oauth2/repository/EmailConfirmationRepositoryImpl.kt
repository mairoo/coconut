package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.EmailConfirmation
import kr.co.pincoin.api.domain.oauth2.repository.EmailConfirmationRepository
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationRepositoryImpl(
    private val jpaRepository: EmailConfirmationJpaRepository,
) : EmailConfirmationRepository {
    override fun save(emailConfirmation: EmailConfirmation): EmailConfirmation {
        TODO("Not yet implemented")
    }
}