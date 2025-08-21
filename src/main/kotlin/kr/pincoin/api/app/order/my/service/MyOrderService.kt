package kr.pincoin.api.app.order.my.service

import kr.pincoin.api.app.order.my.request.MyOrderSearchRequest
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.service.OrderService
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MyOrderService(
    private val orderService: OrderService,
) {
    fun getOrder(
        orderId: Long,
        request: MyOrderSearchRequest,
    ): Order =
        orderService.findOrder(
            orderId,
            OrderSearchCriteria.from(request),
        )
}