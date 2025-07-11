package kr.pincoin.api.domain.user.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class Profile private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val userId: Int,
    val address: String,
    val phone: String? = null,
    val phoneVerified: Boolean = false,
    val phoneVerifiedStatus: Int = 0,
    val dateOfBirth: LocalDate? = null,
    val domestic: Int = 0,
    val gender: Int = 0,
    val telecom: String,
    val photoId: String,
    val card: String,
    val documentVerified: Boolean = false,
    val totalOrderCount: Int = 0,
    val firstPurchased: LocalDateTime? = null,
    val lastPurchased: LocalDateTime? = null,
    val maxPrice: BigDecimal = BigDecimal.ZERO,
    val averagePrice: BigDecimal = BigDecimal.ZERO,
    val totalListPrice: BigDecimal = BigDecimal.ZERO,
    val totalSellingPrice: BigDecimal = BigDecimal.ZERO,
    val notPurchasedMonths: Boolean = false,
    val repurchased: LocalDateTime? = null,
    val memo: String,
    val mileage: BigDecimal = BigDecimal.ZERO,
    val allowOrder: Boolean = false,
) {
    fun updatePhone(newPhone: String): Profile =
        copy(phone = newPhone)

    fun updateAddress(newAddress: String): Profile =
        copy(address = newAddress)

    fun verifyPhone(): Profile =
        copy(phoneVerified = true, phoneVerifiedStatus = 1)

    fun verifyDocument(): Profile =
        copy(documentVerified = true)

    fun updateMemo(newMemo: String): Profile =
        copy(memo = newMemo)

    fun addPurchase(orderPrice: BigDecimal, orderListPrice: BigDecimal): Profile {
        val newTotalOrderCount = totalOrderCount + 1
        val newTotalSellingPrice = totalSellingPrice + orderPrice
        val newTotalListPrice = totalListPrice + orderListPrice
        val newAveragePrice = newTotalSellingPrice.divide(BigDecimal(newTotalOrderCount))
        val newMaxPrice = if (orderPrice > maxPrice) orderPrice else maxPrice
        val now = LocalDateTime.now()

        return copy(
            totalOrderCount = newTotalOrderCount,
            totalSellingPrice = newTotalSellingPrice,
            totalListPrice = newTotalListPrice,
            averagePrice = newAveragePrice,
            maxPrice = newMaxPrice,
            lastPurchased = now,
            firstPurchased = firstPurchased ?: now,
            repurchased = if (totalOrderCount > 0) now else repurchased,
        )
    }

    fun addMileage(amount: BigDecimal): Profile =
        copy(mileage = mileage + amount)

    fun useMileage(amount: BigDecimal): Profile {
        require(mileage >= amount) { "보유 마일리지가 부족합니다" }
        return copy(mileage = mileage - amount)
    }

    fun enableOrder(): Profile =
        copy(allowOrder = true)

    fun disableOrder(): Profile =
        copy(allowOrder = false)

    fun updateNotPurchasedMonths(notPurchased: Boolean): Profile =
        copy(notPurchasedMonths = notPurchased)

    fun hasPhoneVerified(): Boolean = phoneVerified

    fun hasDocumentVerified(): Boolean = documentVerified

    fun canOrder(): Boolean = allowOrder

    private fun copy(
        userId: Int = this.userId,
        address: String = this.address,
        phone: String? = this.phone,
        phoneVerified: Boolean = this.phoneVerified,
        phoneVerifiedStatus: Int = this.phoneVerifiedStatus,
        dateOfBirth: LocalDate? = this.dateOfBirth,
        domestic: Int = this.domestic,
        gender: Int = this.gender,
        telecom: String = this.telecom,
        photoId: String = this.photoId,
        card: String = this.card,
        documentVerified: Boolean = this.documentVerified,
        totalOrderCount: Int = this.totalOrderCount,
        firstPurchased: LocalDateTime? = this.firstPurchased,
        lastPurchased: LocalDateTime? = this.lastPurchased,
        maxPrice: BigDecimal = this.maxPrice,
        averagePrice: BigDecimal = this.averagePrice,
        totalListPrice: BigDecimal = this.totalListPrice,
        totalSellingPrice: BigDecimal = this.totalSellingPrice,
        notPurchasedMonths: Boolean = this.notPurchasedMonths,
        repurchased: LocalDateTime? = this.repurchased,
        memo: String = this.memo,
        mileage: BigDecimal = this.mileage,
        allowOrder: Boolean = this.allowOrder,
    ): Profile = Profile(
        id = this.id,
        created = this.created,
        modified = this.modified,
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

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
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
        ): Profile = Profile(
            id = id,
            created = created,
            modified = modified,
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