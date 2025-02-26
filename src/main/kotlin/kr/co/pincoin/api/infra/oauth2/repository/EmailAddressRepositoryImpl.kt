package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.repository.EmailAddressRepository
import org.springframework.stereotype.Repository

@Repository
class EmailAddressRepositoryImpl(
    private val jpaRepository: EmailAddressJpaRepository,
) : EmailAddressRepository {
}