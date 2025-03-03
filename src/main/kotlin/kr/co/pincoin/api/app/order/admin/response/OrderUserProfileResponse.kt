package kr.co.pincoin.api.app.order.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.domain.user.enums.ProfileDomestic
import kr.co.pincoin.api.domain.user.enums.ProfileGender
import kr.co.pincoin.api.domain.user.enums.ProfilePhoneVerifiedStatus
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderUserProfileResponse(
    // 주문 정보
    @JsonProperty("orderId")
    val orderId: Long?,

    @JsonProperty("orderCreated")
    val orderCreated: ZonedDateTime,

    @JsonProperty("orderModified")
    val orderModified: ZonedDateTime,

    @JsonProperty("orderIsRemoved")
    val orderIsRemoved: Boolean,

    @JsonProperty("orderNo")
    val orderNo: UUID,

    @JsonProperty("ipAddress")
    val ipAddress: String,

    @JsonProperty("fullname")
    val fullname: String,

    @JsonProperty("userAgent")
    val userAgent: String,

    @JsonProperty("acceptLanguage")
    val acceptLanguage: String,

    @JsonProperty("paymentMethod")
    val paymentMethod: OrderPaymentMethod,

    @JsonProperty("transactionId")
    val transactionId: String,

    @JsonProperty("status")
    val status: OrderStatus,

    @JsonProperty("visible")
    val visible: OrderVisibility,

    @JsonProperty("totalListPrice")
    val totalListPrice: BigDecimal,

    @JsonProperty("totalSellingPrice")
    val totalSellingPrice: BigDecimal,

    @JsonProperty("currency")
    val currency: OrderCurrency,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("parentId")
    val parentId: Long?,

    @JsonProperty("suspicious")
    val suspicious: Boolean,

    // 사용자 정보
    @JsonProperty("userId")
    val userId: Int?,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("dateJoined")
    val dateJoined: ZonedDateTime,

    @JsonProperty("lastLogin")
    val lastLogin: ZonedDateTime?,

    @JsonProperty("isSuperuser")
    val isSuperuser: Boolean,

    @JsonProperty("firstName")
    val firstName: String,

    @JsonProperty("lastName")
    val lastName: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("isStaff")
    val isStaff: Boolean,

    @JsonProperty("isActive")
    val isActive: Boolean,

    // 프로필 정보
    @JsonProperty("profileId")
    val profileId: Long?,

    @JsonProperty("profileCreated")
    val profileCreated: ZonedDateTime,

    @JsonProperty("profileModified")
    val profileModified: ZonedDateTime,

    @JsonProperty("phone")
    val phone: String?,

    @JsonProperty("address")
    val address: String,

    @JsonProperty("phoneVerified")
    val phoneVerified: Boolean,

    @JsonProperty("documentVerified")
    val documentVerified: Boolean,

    @JsonProperty("photoId")
    val photoId: String,

    @JsonProperty("card")
    val card: String,

    @JsonProperty("totalOrderCount")
    val totalOrderCount: Int,

    @JsonProperty("lastPurchased")
    val lastPurchased: ZonedDateTime?,

    @JsonProperty("maxPrice")
    val maxPrice: BigDecimal,

    @JsonProperty("averagePrice")
    val averagePrice: BigDecimal,

    @JsonProperty("memo")
    val memo: String,

    @JsonProperty("phoneVerifiedStatus")
    val phoneVerifiedStatus: ProfilePhoneVerifiedStatus,

    @JsonProperty("dateOfBirth")
    val dateOfBirth: LocalDate?,

    @JsonProperty("firstPurchased")
    val firstPurchased: ZonedDateTime?,

    @JsonProperty("profileTotalListPrice")
    val profileTotalListPrice: BigDecimal,

    @JsonProperty("profileTotalSellingPrice")
    val profileTotalSellingPrice: BigDecimal,

    @JsonProperty("domestic")
    val domestic: ProfileDomestic,

    @JsonProperty("gender")
    val gender: ProfileGender,

    @JsonProperty("telecom")
    val telecom: String,

    @JsonProperty("notPurchasedMonths")
    val notPurchasedMonths: Boolean,

    @JsonProperty("repurchased")
    val repurchased: ZonedDateTime?,

    @JsonProperty("mileage")
    val mileage: BigDecimal,

    @JsonProperty("allowOrder")
    val allowOrder: Boolean
) {
    companion object {
        fun from(projection: OrderUserProfileProjection): OrderUserProfileResponse =
            with(projection) {
                OrderUserProfileResponse(
                    orderId = orderId,
                    orderCreated = orderCreated,
                    orderModified = orderModified,
                    orderIsRemoved = orderIsRemoved,
                    orderNo = orderNo,
                    ipAddress = ipAddress,
                    fullname = fullname,
                    userAgent = userAgent,
                    acceptLanguage = acceptLanguage,
                    paymentMethod = paymentMethod,
                    transactionId = transactionId,
                    status = status,
                    visible = visible,
                    totalListPrice = totalListPrice,
                    totalSellingPrice = totalSellingPrice,
                    currency = currency,
                    message = message,
                    parentId = parentId,
                    suspicious = suspicious,
                    userId = userId,
                    username = username,
                    dateJoined = dateJoined,
                    lastLogin = lastLogin,
                    isSuperuser = isSuperuser,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    isStaff = isStaff,
                    isActive = isActive,
                    profileId = profileId,
                    profileCreated = profileCreated,
                    profileModified = profileModified,
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
                    memo = memo,
                    phoneVerifiedStatus = phoneVerifiedStatus,
                    dateOfBirth = dateOfBirth,
                    firstPurchased = firstPurchased,
                    profileTotalListPrice = profileTotalListPrice,
                    profileTotalSellingPrice = profileTotalSellingPrice,
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
}