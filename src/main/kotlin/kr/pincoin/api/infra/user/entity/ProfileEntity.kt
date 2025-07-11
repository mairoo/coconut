package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
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

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "address")
    val address: String,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "phone_verified")
    val phoneVerified: Boolean,

    @Column(name = "phone_verified_status")
    val phoneVerifiedStatus: Int,

    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate?,

    @Column(name = "domestic")
    val domestic: Int,

    @Column(name = "gender")
    val gender: Int,

    @Column(name = "telecom")
    val telecom: String,

    @Column(name = "photo_id")
    val photoId: String,

    @Column(name = "card")
    val card: String,

    @Column(name = "document_verified")
    val documentVerified: Boolean,

    @Column(name = "total_order_count")
    val totalOrderCount: Int = 0,

    @Column(name = "first_purchased")
    val firstPurchased: LocalDateTime?,

    @Column(name = "last_purchased")
    val lastPurchased: LocalDateTime?,

    @Column(name = "max_price")
    val maxPrice: BigDecimal,

    @Column(name = "average_price")
    val averagePrice: BigDecimal,

    @Column(name = "total_list_price")
    val totalListPrice: BigDecimal,

    @Column(name = "total_selling_price")
    val totalSellingPrice: BigDecimal,

    @Column(name = "not_purchased_months")
    val notPurchasedMonths: Boolean,

    @Column(name = "repurchased")
    val repurchased: LocalDateTime?,

    @Column(name = "memo")
    val memo: String,

    @Column(name = "mileage")
    val mileage: BigDecimal,

    @Column(name = "allow_order")
    val allowOrder: Boolean,
) {
    companion object {
        fun of(
            id: Long? = null,
            userId: Int,
            address: String,
            phone: String? = null,
            phoneVerified: Boolean = false,
            phoneVerifiedStatus: Int = 0,
            dateOfBirth: LocalDate? = null,
            domestic: Int = 0,
            gender: Int = 0,
            telecom: String,
            photoId: String,
            card: String,
            documentVerified: Boolean = false,
            totalOrderCount: Int = 0,
            firstPurchased: LocalDateTime? = null,
            lastPurchased: LocalDateTime? = null,
            maxPrice: BigDecimal = BigDecimal.ZERO,
            averagePrice: BigDecimal = BigDecimal.ZERO,
            totalListPrice: BigDecimal = BigDecimal.ZERO,
            totalSellingPrice: BigDecimal = BigDecimal.ZERO,
            notPurchasedMonths: Boolean = false,
            repurchased: LocalDateTime? = null,
            memo: String,
            mileage: BigDecimal = BigDecimal.ZERO,
            allowOrder: Boolean = false,
        ) = ProfileEntity(
            id = id,
            userId = userId,
            address = address,
            phone = phone,
            phoneVerified = phoneVerified,
            phoneVerifiedStatus = phoneVerifiedStatus,
            dateOfBirth = dateOfBirth,
            domestic = domestic,
            gender = gender,
            telecom = telecom,
            photoId = photoId,
            card = card,
            documentVerified = documentVerified,
            totalOrderCount = totalOrderCount,
            firstPurchased = firstPurchased,
            lastPurchased = lastPurchased,
            maxPrice = maxPrice,
            averagePrice = averagePrice,
            totalListPrice = totalListPrice,
            totalSellingPrice = totalSellingPrice,
            notPurchasedMonths = notPurchasedMonths,
            repurchased = repurchased,
            memo = memo,
            mileage = mileage,
            allowOrder = allowOrder,
        )
    }
}