package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.EmailAddress
import kr.co.pincoin.api.domain.oauth2.repository.EmailAddressRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class EmailAddressRepositoryImpl(
    private val jpaRepository: EmailAddressJpaRepository,
) : EmailAddressRepository {
    override fun save(
        emailAddress: EmailAddress,
    ): EmailAddress =
        emailAddress.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이메일 저장 실패")
}