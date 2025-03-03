package kr.co.pincoin.api.domain.order.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderProductVoucherService {
}