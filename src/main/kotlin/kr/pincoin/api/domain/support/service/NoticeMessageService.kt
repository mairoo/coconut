package kr.pincoin.api.domain.support.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NoticeMessageService {
}