package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.ShortMessageRepository
import org.springframework.stereotype.Repository

@Repository
class ShortMessageRepositoryImpl(
    private val jpaRepository: ShortMessageJpaRepository,
) : ShortMessageRepository {
}