package kr.pincoin.api.app.user.my.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MyUserProfileResponse(
    // User 필드
    @JsonProperty("userId")
    val userId: Int,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("firstName")
    val firstName: String,

    @JsonProperty("lastName")
    val lastName: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("isActive")
    val isActive: Boolean,

    @JsonProperty("dateJoined")
    val dateJoined: LocalDateTime,

    @JsonProperty("lastLogin")
    val lastLogin: LocalDateTime?,

    // Profile 필드
    @JsonProperty("address")
    val address: String,

    @JsonProperty("phone")
    val phone: String?,

    @JsonProperty("phoneVerified")
    val phoneVerified: Boolean,

    @JsonProperty("phoneVerifiedStatus")
    val phoneVerifiedStatus: Int,

    @JsonProperty("dateOfBirth")
    val dateOfBirth: LocalDate?,

    @JsonProperty("domestic")
    val domestic: Int,

    @JsonProperty("gender")
    val gender: Int,

    @JsonProperty("telecom")
    val telecom: String,

    @JsonProperty("documentVerified")
    val documentVerified: Boolean,

    @JsonProperty("totalOrderCount")
    val totalOrderCount: Int,

    @JsonProperty("firstPurchased")
    val firstPurchased: LocalDateTime?,

    @JsonProperty("lastPurchased")
    val lastPurchased: LocalDateTime?,

    @JsonProperty("maxPrice")
    val maxPrice: BigDecimal,

    @JsonProperty("averagePrice")
    val averagePrice: BigDecimal,

    @JsonProperty("totalListPrice")
    val totalListPrice: BigDecimal,

    @JsonProperty("totalSellingPrice")
    val totalSellingPrice: BigDecimal,

    @JsonProperty("notPurchasedMonths")
    val notPurchasedMonths: Boolean,

    @JsonProperty("repurchased")
    val repurchased: LocalDateTime?,

    @JsonProperty("memo")
    val memo: String,

    @JsonProperty("mileage")
    val mileage: BigDecimal,

    @JsonProperty("allowOrder")
    val allowOrder: Boolean,
) {
    companion object {
        fun from(projection: UserProfileProjection) = with(projection) {
            MyUserProfileResponse(
                userId = userId,
                username = username,
                firstName = firstName,
                lastName = lastName,
                email = email,
                isActive = isActive,
                dateJoined = dateJoined,
                lastLogin = lastLogin,

                address = address,
                phone = phone,
                phoneVerified = phoneVerified,
                phoneVerifiedStatus = phoneVerifiedStatus,
                dateOfBirth = dateOfBirth,
                domestic = domestic,
                gender = gender,
                telecom = telecom,
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
}