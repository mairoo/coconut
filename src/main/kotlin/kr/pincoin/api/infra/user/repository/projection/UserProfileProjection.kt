package kr.pincoin.api.infra.user.repository.projection

import com.querydsl.core.annotations.QueryProjection
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class UserProfileProjection @QueryProjection constructor(
    // User 필드
    val userId: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isActive: Boolean,
    val isStaff: Boolean,
    val isSuperuser: Boolean,
    val dateJoined: LocalDateTime,
    val lastLogin: LocalDateTime?,

    // Profile 필드
    val profileId: Long,
    val profileCreated: LocalDateTime,
    val profileModified: LocalDateTime,
    val address: String,
    val phone: String?,
    val phoneVerified: Boolean,
    val phoneVerifiedStatus: Int,
    val dateOfBirth: LocalDate?,
    val domestic: Int,
    val gender: Int,
    val telecom: String,
    val photoId: String,
    val card: String,
    val documentVerified: Boolean,
    val totalOrderCount: Int,
    val firstPurchased: LocalDateTime?,
    val lastPurchased: LocalDateTime?,
    val maxPrice: BigDecimal,
    val averagePrice: BigDecimal,
    val totalListPrice: BigDecimal,
    val totalSellingPrice: BigDecimal,
    val notPurchasedMonths: Boolean,
    val repurchased: LocalDateTime?,
    val memo: String,
    val mileage: BigDecimal,
    val allowOrder: Boolean,
)