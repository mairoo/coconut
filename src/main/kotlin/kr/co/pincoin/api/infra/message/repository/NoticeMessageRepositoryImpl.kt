package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.domain.message.repository.NoticessMessageRepository
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageRepositoryImpl(
    private val jpaRepository: NoticeMessageJpaRepository,
    private val queryRepository: NoticeMessageQueryRepository,
) : NoticessMessageRepository {
}