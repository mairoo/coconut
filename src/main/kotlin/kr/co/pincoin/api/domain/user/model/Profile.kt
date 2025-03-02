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
    val totalListPrice: BigDecimal,
    val totalSellingPrice: BigDecimal,
    val domestic: ProfileDomestic,
    val gender: ProfileGender,
    val telecom: String,
    val notPurchasedMonths: Boolean,
    val repurchased: ZonedDateTime?,
    val mileage: BigDecimal,
    val allowOrder: Boolean,
) {
    fun updatePhone(newPhone: String?): Profile = copy(
        phone = newPhone
    )

    fun updateAddress(newAddress: String): Profile = copy(
        address = newAddress
    )

    fun updatePhoneVerification(newPhoneVerified: Boolean): Profile = copy(
        phoneVerified = newPhoneVerified
    )

    fun updateDocumentVerification(newDocumentVerified: Boolean): Profile = copy(
        documentVerified = newDocumentVerified
    )

    fun updatePhotoId(newPhotoId: String): Profile = copy(
        photoId = newPhotoId
    )

    fun updateCard(newCard: String): Profile = copy(
        card = newCard
    )

    fun updateOrderStats(
        newTotalOrderCount: Int? = null,
        newLastPurchased: ZonedDateTime? = null,
        newMaxPrice: BigDecimal? = null,
        newAveragePrice: BigDecimal? = null
    ): Profile = copy(
        totalOrderCount = newTotalOrderCount ?: totalOrderCount,
        lastPurchased = newLastPurchased,
        maxPrice = newMaxPrice ?: maxPrice,
        averagePrice = newAveragePrice ?: averagePrice
    )

    fun updateMemo(newMemo: String): Profile = copy(
        memo = newMemo
    )

    fun updatePhoneVerifiedStatus(newStatus: ProfilePhoneVerifiedStatus): Profile = copy(
        phoneVerifiedStatus = newStatus
    )

    fun updateDateOfBirth(newDateOfBirth: LocalDate?): Profile = copy(
        dateOfBirth = newDateOfBirth
    )

    fun updatePurchaseHistory(
        newFirstPurchased: ZonedDateTime? = null,
        newTotalListPrice: BigDecimal? = null,
        newTotalSellingPrice: BigDecimal? = null,
        newNotPurchasedMonths: Boolean? = null,
        newRepurchased: ZonedDateTime? = null
    ): Profile = copy(
        firstPurchased = newFirstPurchased,
        totalListPrice = newTotalListPrice ?: totalListPrice,
        totalSellingPrice = newTotalSellingPrice ?: totalSellingPrice,
        notPurchasedMonths = newNotPurchasedMonths ?: notPurchasedMonths,
        repurchased = newRepurchased
    )

    fun updateDomestic(newDomestic: ProfileDomestic): Profile = copy(
        domestic = newDomestic
    )

    fun updateGender(newGender: ProfileGender): Profile = copy(
        gender = newGender
    )

    fun updateTelecom(newTelecom: String): Profile = copy(
        telecom = newTelecom
    )

    fun updateMileage(newMileage: BigDecimal): Profile = copy(
        mileage = newMileage
    )

    fun updateAllowOrder(newAllowOrder: Boolean): Profile = copy(
        allowOrder = newAllowOrder
    )

    private fun copy(
        phone: String? = this.phone,
        address: String = this.address,
        phoneVerified: Boolean = this.phoneVerified,
        documentVerified: Boolean = this.documentVerified,
        photoId: String = this.photoId,
        card: String = this.card,
        totalOrderCount: Int = this.totalOrderCount,
        lastPurchased: ZonedDateTime? = this.lastPurchased,
        maxPrice: BigDecimal = this.maxPrice,
        averagePrice: BigDecimal = this.averagePrice,
        memo: String = this.memo,
        phoneVerifiedStatus: ProfilePhoneVerifiedStatus = this.phoneVerifiedStatus,
        dateOfBirth: LocalDate? = this.dateOfBirth,
        firstPurchased: ZonedDateTime? = this.firstPurchased,
        totalListPrice: BigDecimal = this.totalListPrice,
        totalSellingPrice: BigDecimal = this.totalSellingPrice,
        domestic: ProfileDomestic = this.domestic,
        gender: ProfileGender = this.gender,
        telecom: String = this.telecom,
        notPurchasedMonths: Boolean = this.notPurchasedMonths,
        repurchased: ZonedDateTime? = this.repurchased,
        mileage: BigDecimal = this.mileage,
        allowOrder: Boolean = this.allowOrder
    ): Profile = Profile(
        id = this.id,
        created = this.created,
        modified = this.modified,
        userId = this.userId,
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