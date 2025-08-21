package kr.pincoin.api.domain.coordinator.order

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderResourceCoordinator {
}