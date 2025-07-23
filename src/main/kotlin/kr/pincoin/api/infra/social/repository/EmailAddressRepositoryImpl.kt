package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.EmailAddress
import kr.pincoin.api.domain.social.repository.EmailAddressRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class EmailAddressRepositoryImpl(
    private val jpaRepository: EmailAddressJpaRepository,
    private val queryRepository: EmailAddressQueryRepository,
) : EmailAddressRepository {
    override fun save(
        emailAddress: EmailAddress,
    ): EmailAddress =
        emailAddress.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이메일주소 저장 실패")

    override fun findById(
        id: Int,
    ): EmailAddress? =
        queryRepository.findById(id)?.toModel()
}