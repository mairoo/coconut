package kr.pincoin.api.app.user.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AdminUserProfileResponse(
    // User 필드
    @field:JsonProperty("userId")
    val userId: Int,

    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("firstName")
    val firstName: String,

    @field:JsonProperty("lastName")
    val lastName: String,

    @field:JsonProperty("email")
    val email: String,

    @field:JsonProperty("isActive")
    val isActive: Boolean,

    @field:JsonProperty("isStaff")
    val isStaff: Boolean,

    @field:JsonProperty("isSuperuser")
    val isSuperuser: Boolean,

    @field:JsonProperty("dateJoined")
    val dateJoined: LocalDateTime,

    @field:JsonProperty("lastLogin")
    val lastLogin: LocalDateTime?,

    // Profile 필드
    @field:JsonProperty("profileId")
    val profileId: Long,

    @field:JsonProperty("profileCreated")
    val profileCreated: LocalDateTime,

    @field:JsonProperty("profileModified")
    val profileModified: LocalDateTime,

    @field:JsonProperty("address")
    val address: String,

    @field:JsonProperty("phone")
    val phone: String?,

    @field:JsonProperty("phoneVerified")
    val phoneVerified: Boolean,

    @field:JsonProperty("phoneVerifiedStatus")
    val phoneVerifiedStatus: Int,

    @field:JsonProperty("dateOfBirth")
    val dateOfBirth: LocalDate?,

    @field:JsonProperty("domestic")
    val domestic: Int,

    @field:JsonProperty("gender")
    val gender: Int,

    @field:JsonProperty("telecom")
    val telecom: String,

    @field:JsonProperty("photoId")
    val photoId: String,

    @field:JsonProperty("card")
    val card: String,

    @field:JsonProperty("documentVerified")
    val documentVerified: Boolean,

    @field:JsonProperty("totalOrderCount")
    val totalOrderCount: Int,

    @field:JsonProperty("firstPurchased")
    val firstPurchased: LocalDateTime?,

    @field:JsonProperty("lastPurchased")
    val lastPurchased: LocalDateTime?,

    @field:JsonProperty("maxPrice")
    val maxPrice: BigDecimal,

    @field:JsonProperty("averagePrice")
    val averagePrice: BigDecimal,

    @field:JsonProperty("totalListPrice")
    val totalListPrice: BigDecimal,

    @field:JsonProperty("totalSellingPrice")
    val totalSellingPrice: BigDecimal,

    @field:JsonProperty("notPurchasedMonths")
    val notPurchasedMonths: Boolean,

    @field:JsonProperty("repurchased")
    val repurchased: LocalDateTime?,

    @field:JsonProperty("memo")
    val memo: String,

    @field:JsonProperty("mileage")
    val mileage: BigDecimal,

    @field:JsonProperty("allowOrder")
    val allowOrder: Boolean,
) {
    companion object {
        fun from(projection: UserProfileProjection) = with(projection) {
            AdminUserProfileResponse(
                userId = userId,
                username = username,
                firstName = firstName,
                lastName = lastName,
                email = email,
                isActive = isActive,
                isStaff = isStaff,
                isSuperuser = isSuperuser,
                dateJoined = dateJoined,
                lastLogin = lastLogin,

                profileId = profileId,
                profileCreated = profileCreated,
                profileModified = profileModified,
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
}