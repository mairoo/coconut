package kr.pincoin.api.domain.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BannedPhoneService(
) {
}