package kr.pincoin.api.domain.inventory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PurchaseOrderService {
}