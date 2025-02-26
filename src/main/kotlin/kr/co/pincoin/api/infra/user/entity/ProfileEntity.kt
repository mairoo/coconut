package kr.co.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity
@Table(name = "member_profile")
class ProfileEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

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
    val totalOrderCount: Int,

    @Column(name = "last_purchased")
    val lastPurchased: ZonedDateTime? = null,

    @Column(name = "max_price", precision = 11, scale = 2)
    val maxPrice: BigDecimal,

    @Column(name = "average_price", precision = 11, scale = 2)
    val averagePrice: BigDecimal,

    @Column(name = "user_id", unique = true)
    val userId: Int,

    @Column(name = "memo", columnDefinition = "text")
    val memo: String,

    @Column(name = "phone_verified_status")
    val phoneVerifiedStatus: Int,

    @Column(name = "date_of_birth")
    val dateOfBirth: LocalDate? = null,

    @Column(name = "first_purchased")
    val firstPurchased: ZonedDateTime? = null,

    @Column(name = "total_list_price", precision = 11, scale = 2)
    val totalListPrice: BigDecimal,

    @Column(name = "total_selling_price", precision = 11, scale = 2)
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
    val repurchased: ZonedDateTime? = null,

    @Column(name = "mileage", precision = 11, scale = 2)
    val mileage: BigDecimal,

    @Column(name = "allow_order")
    val allowOrder: Boolean,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            phone: String? = null,
            address: String,
            phoneVerified: Boolean,
            documentVerified: Boolean,
            photoId: String,
            card: String,
            totalOrderCount: Int,
            lastPurchased: ZonedDateTime? = null,
            maxPrice: BigDecimal,
            averagePrice: BigDecimal,
            userId: Int,
            memo: String,
            phoneVerifiedStatus: Int,
            dateOfBirth: LocalDate? = null,
            firstPurchased: ZonedDateTime? = null,
            totalListPrice: BigDecimal,
            totalSellingPrice: BigDecimal,
            domestic: Int,
            gender: Int,
            telecom: String,
            notPurchasedMonths: Boolean,
            repurchased: ZonedDateTime? = null,
            mileage: BigDecimal,
            allowOrder: Boolean
        ) = ProfileEntity(
            id = id,
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