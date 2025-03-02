package kr.co.pincoin.api.infra.order.entity

import jakarta.persistence.*

@Entity
@Table(name = "shop_cart")
class CartEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "cart_data")
    val cartData: String,

    @Column(name = "version")
    @Version
    val version: Long,
) {
    companion object {
        fun of(
            id: Long?,
            userId: Int,
            cartData: String,
            version: Long = 0L,
        ) = CartEntity(
            id = id,
            userId = userId,
            cartData = cartData,
            version = version,
        )
    }
}