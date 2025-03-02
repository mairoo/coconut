package kr.co.pincoin.api.domain.user.model

import kr.co.pincoin.api.domain.user.enums.ProfileDomestic
import kr.co.pincoin.api.domain.user.enums.ProfileGender
import kr.co.pincoin.api.domain.user.enums.ProfilePhoneVerifiedStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class Profile private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 도메인 로직 불변 필드
    val userId: Int,

    // 3. 도메인 로직 가변 필드
    phone: String?,
    address: String,
    phoneVerified: Boolean,
    documentVerified: Boolean,
    photoId: String,
    card: String,
    totalOrderCount: Int,
    lastPurchased: ZonedDateTime?,
    maxPrice: BigDecimal,
    averagePrice: BigDecimal,
    memo: String,
    phoneVerifiedStatus: ProfilePhoneVerifiedStatus,
    dateOfBirth: LocalDate?,
    firstPurchased: ZonedDateTime?,
    totalListPrice: BigDecimal,
    totalSellingPrice: BigDecimal,
    domestic: ProfileDomestic,
    gender: ProfileGender,
    telecom: String,
    notPurchasedMonths: Boolean,
    repurchased: ZonedDateTime?,
    mileage: BigDecimal,
    allowOrder: Boolean,
) {
    var phone: String? = phone
        private set

    var address: String = address
        private set

    var phoneVerified: Boolean = phoneVerified
        private set

    var documentVerified: Boolean = documentVerified
        private set

    var photoId: String = photoId
        private set

    var card: String = card
        private set

    var totalOrderCount: Int = totalOrderCount
        private set

    var lastPurchased: ZonedDateTime? = lastPurchased
        private set

    var maxPrice: BigDecimal = maxPrice
        private set

    var averagePrice: BigDecimal = averagePrice
        private set

    var memo: String = memo
        private set

    var phoneVerifiedStatus: ProfilePhoneVerifiedStatus = phoneVerifiedStatus
        private set

    var dateOfBirth: LocalDate? = dateOfBirth
        private set

    var firstPurchased: ZonedDateTime? = firstPurchased
        private set

    var totalListPrice: BigDecimal = totalListPrice
        private set

    var totalSellingPrice: BigDecimal = totalSellingPrice
        private set

    var domestic: ProfileDomestic = domestic
        private set

    var gender: ProfileGender = gender
        private set

    var telecom: String = telecom
        private set

    var notPurchasedMonths: Boolean = notPurchasedMonths
        private set

    var repurchased: ZonedDateTime? = repurchased
        private set

    var mileage: BigDecimal = mileage
        private set

    var allowOrder: Boolean = allowOrder
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
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
            phoneVerifiedStatus: ProfilePhoneVerifiedStatus,
            dateOfBirth: LocalDate? = null,
            firstPurchased: ZonedDateTime? = null,
            totalListPrice: BigDecimal,
            totalSellingPrice: BigDecimal,
            domestic: ProfileDomestic,
            gender: ProfileGender,
            telecom: String,
            notPurchasedMonths: Boolean,
            repurchased: ZonedDateTime? = null,
            mileage: BigDecimal,
            allowOrder: Boolean
        ): Profile =
            Profile(
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
                allowOrder = allowOrder,
            )
    }
}