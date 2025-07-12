package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.FaqMessageRepository
import org.springframework.stereotype.Repository

@Repository
class FaqMessageRepositoryImpl(
    private val jpaRepository: FaqMessageJpaRepository,
    private val queryRepository: FaqMessageQueryRepository,
) : FaqMessageRepository {
}