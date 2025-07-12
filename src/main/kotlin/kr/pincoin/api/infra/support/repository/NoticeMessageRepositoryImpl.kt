package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.NoticeMessageRepository
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageRepositoryImpl(
    private val jpaRepository: NoticeMessageJpaRepository,
    private val queryRepository: NoticeMessageQueryRepository,
) : NoticeMessageRepository {
}