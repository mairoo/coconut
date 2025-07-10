package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "member_profile")
class ProfileEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "modified")
    val modified: LocalDateTime,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "address")
    val address: String,

    @Column(name = "phone_verified")
    val phoneVerified: Boolean,

    @Column(name = "document_verified")
    val documentVerified: Boolean,

    @Column(name = "photo_id")
    val photoId: String,

    @Column(name = "card")
    val card: String,

    @Column(name = "total_order_count")
    val totalOrderCount: Int = 0,

    @Column(name = "last_purchased")
    val lastPurchased: LocalDateTime?,

    @Column(name = "max_price")
    val maxPrice: BigDecimal,

    @Column(name = "average_price")
    val averagePrice: BigDecimal,

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "memo")
    val memo: String,

    @Column(name = "phone_verified_status")
    val phoneVerifiedStatus: Int,

    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate?,

    @Column(name = "first_purchased")
    val firstPurchased: LocalDateTime?,

    @Column(name = "total_list_price")
    val totalListPrice: BigDecimal,

    @Column(name = "total_selling_price")
    val totalSellingPrice: BigDecimal,

    @Column(name = "domestic")
    val domestic: Int,

    @Column(name = "gender")
    val gender: Int,

    @Column(name = "telecom")
    val telecom: String,

    @Column(name = "not_purchased_months")
    val notPurchasedMonths: Boolean,

    @Column(name = "repurchased")
    val repurchased: LocalDateTime?,

    @Column(name = "mileage")
    val mileage: BigDecimal,

    @Column(name = "allow_order")
    val allowOrder: Boolean,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
            phone: String? = null,
            address: String,
            phoneVerified: Boolean = false,
            documentVerified: Boolean = false,
            photoId: String,
            card: String,
            totalOrderCount: Int = 0,
            lastPurchased: LocalDateTime? = null,
            maxPrice: BigDecimal = BigDecimal.ZERO,
            averagePrice: BigDecimal = BigDecimal.ZERO,
            userId: Int,
            memo: String,
            phoneVerifiedStatus: Int = 0,
            dateOfBirth: LocalDate? = null,
            firstPurchased: LocalDateTime? = null,
            totalListPrice: BigDecimal = BigDecimal.ZERO,
            totalSellingPrice: BigDecimal = BigDecimal.ZERO,
            domestic: Int = 0,
            gender: Int = 0,
            telecom: String,
            notPurchasedMonths: Boolean = false,
            repurchased: LocalDateTime? = null,
            mileage: BigDecimal = BigDecimal.ZERO,
            allowOrder: Boolean = true
        ) = ProfileEntity(
            id = id,
            created = created,
            modified = modified,
            phone = phone,
            address = address,
            phoneVerified = phoneVerified,
            documentVerified = documentVerified,
            photoId = photoId,
            card = card,
            totalOrderCount = totalOrderCount,
            lastPurchased = lastPurchased,
            maxPrice = maxPrice,
            averagePrice = averagePrice,
            userId = userId,
            memo = memo,
            phoneVerifiedStatus = phoneVerifiedStatus,
            dateOfBirth = dateOfBirth,
            firstPurchased = firstPurchased,
            totalListPrice = totalListPrice,
            totalSellingPrice = totalSellingPrice,
            domestic = domestic,
            gender = gender,
            telecom = telecom,
            notPurchasedMonths = notPurchasedMonths,
            repurchased = repurchased,
            mileage = mileage,
            allowOrder = allowOrder
        )
    }
}