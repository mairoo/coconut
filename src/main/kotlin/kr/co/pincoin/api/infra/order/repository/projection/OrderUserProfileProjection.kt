package kr.co.pincoin.api.infra.order.repository.projection

import com.querydsl.core.annotations.QueryProjection
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.domain.user.enums.ProfileDomestic
import kr.co.pincoin.api.domain.user.enums.ProfileGender
import kr.co.pincoin.api.domain.user.enums.ProfilePhoneVerifiedStatus
import java.math.BigDecimal
import java.net.InetAddress
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

data class OrderUserProfileProjection @QueryProjection constructor(
    // Order 정보
    val orderId: Long?,
    val orderCreated: ZonedDateTime,
    val orderModified: ZonedDateTime,
    val orderIsRemoved: Boolean,
    val orderNo: UUID,
    val ipAddress: InetAddress,
    val fullname: String,
    val userAgent: String,
    val acceptLanguage: String,
    val paymentMethod: OrderPaymentMethod,
    val transactionId: String,
    val status: OrderStatus,
    val visible: OrderVisibility,
    val totalListPrice: BigDecimal,
    val totalSellingPrice: BigDecimal,
    val currency: OrderCurrency,
    val message: String,
    val parentId: Long?,
    val suspicious: Boolean,

    // User 정보
    val userId: Int?,
    val username: String,
    val dateJoined: ZonedDateTime,
    val lastLogin: ZonedDateTime?,
    val isSuperuser: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isStaff: Boolean,
    val isActive: Boolean,

    // Profile 정보
    val profileId: Long?,
    val profileCreated: ZonedDateTime,
    val profileModified: ZonedDateTime,
    val phone: String?,
    val address: String,
    val phoneVerified: Boolean,
    val documentVerified: Boolean,
    val photoId: String,
    val card: String,
    val totalOrderCount: Int,
    val lastPurchased: ZonedDateTime?,
    val maxPrice: BigDecimal,
    val averagePrice: BigDecimal,
    val memo: String,
    val phoneVerifiedStatus: ProfilePhoneVerifiedStatus,
    val dateOfBirth: LocalDate?,
    val firstPurchased: ZonedDateTime?,
    val profileTotalListPrice: BigDecimal,
    val profileTotalSellingPrice: BigDecimal,
    val domestic: ProfileDomestic,
    val gender: ProfileGender,
    val telecom: String,
    val notPurchasedMonths: Boolean,
    val repurchased: ZonedDateTime?,
    val mileage: BigDecimal,
    val allowOrder: Boolean
)