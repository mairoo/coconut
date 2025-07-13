package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.EmailConfirmation
import kr.pincoin.api.domain.social.repository.EmailConfirmationRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationRepositoryImpl(
    private val jpaRepository: EmailConfirmationJpaRepository,
    private val queryRepository: EmailConfirmationQueryRepository,
) : EmailConfirmationRepository {
    override fun save(
        emailConfirmation: EmailConfirmation,
    ): EmailConfirmation =
        emailConfirmation.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이메일확인 저장 실패")

    override fun findById(
        id: Int,
    ): EmailConfirmation? {
        return queryRepository.findById(id)?.toModel()
    }
}