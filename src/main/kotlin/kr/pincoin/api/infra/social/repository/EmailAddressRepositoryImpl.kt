package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.EmailAddressRepository
import org.springframework.stereotype.Repository

@Repository
class EmailAddressRepositoryImpl(
    private val jpaRepository: EmailAddressJpaRepository,
    private val queryRepository: EmailAddressQueryRepository,
) : EmailAddressRepository {
}