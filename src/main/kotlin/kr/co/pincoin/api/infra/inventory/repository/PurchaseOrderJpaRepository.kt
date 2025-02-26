package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.infra.inventory.entity.PurchaseOrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseOrderJpaRepository : JpaRepository<PurchaseOrderEntity, Long>