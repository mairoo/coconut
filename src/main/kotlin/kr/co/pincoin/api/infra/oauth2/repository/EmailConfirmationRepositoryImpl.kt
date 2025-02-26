package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.EmailConfirmation
import kr.co.pincoin.api.domain.oauth2.repository.EmailConfirmationRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationRepositoryImpl(
    private val jpaRepository: EmailConfirmationJpaRepository,
) : EmailConfirmationRepository {
    override fun save(
        emailConfirmation: EmailConfirmation,
    ): EmailConfirmation =
        emailConfirmation.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이메일 확인 저장 실패")
}