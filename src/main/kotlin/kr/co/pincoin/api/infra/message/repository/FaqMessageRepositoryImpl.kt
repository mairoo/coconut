package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.domain.message.repository.FaqMessageRepository
import org.springframework.stereotype.Repository

@Repository
class FaqMessageRepositoryImpl(
    private val jpaRepository: FaqMessageJpaRepository,
    private val queryRepository: FaqMessageQueryRepository,
) : FaqMessageRepository {
}