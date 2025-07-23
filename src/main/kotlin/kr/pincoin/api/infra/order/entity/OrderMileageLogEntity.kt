package kr.pincoin.api.infra.order.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "shop_mileagelog")
class OrderMileageLogEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "modified")
    val modified: LocalDateTime,

    @Column(name = "is_removed")
    val isRemoved: Boolean,

    @Column(name = "mileage", precision = 11, scale = 2)
    val mileage: BigDecimal,

    @Column(name = "order_id")
    val orderId: Long?,

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "memo", columnDefinition = "text")
    val memo: String,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
            isRemoved: Boolean = false,
            mileage: BigDecimal,
            orderId: Long? = null,
            userId: Int,
            memo: String,
        ) = OrderMileageLogEntity(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            mileage = mileage,
            orderId = orderId,
            userId = userId,
            memo = memo,
        )
    }
}