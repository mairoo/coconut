package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.infra.message.entity.NoticeMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeMessageRepository : JpaRepository<NoticeMessageEntity, Long>