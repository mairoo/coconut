package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.CartEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartJpaRepository : JpaRepository<CartEntity, Long> {
}