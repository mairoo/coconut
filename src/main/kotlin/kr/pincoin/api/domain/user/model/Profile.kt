package kr.pincoin.api.domain.user.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class Profile private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val phone: String? = null,
    val address: String,
    val phoneVerified: Boolean = false,
    val documentVerified: Boolean = false,
    val photoId: String,
    val card: String,
    val totalOrderCount: Int = 0,
    val lastPurchased: LocalDateTime? = null,
    val maxPrice: BigDecimal = BigDecimal.ZERO,
    val averagePrice: BigDecimal = BigDecimal.ZERO,
    val userId: Int,
    val memo: String,
    val phoneVerifiedStatus: Int = 0,
    val dateOfBirth: LocalDate? = null,
    val firstPurchased: LocalDateTime? = null,
    val totalListPrice: BigDecimal = BigDecimal.ZERO,
    val totalSellingPrice: BigDecimal = BigDecimal.ZERO,
    val domestic: Int = 0,
    val gender: Int = 0,
    val telecom: String,
    val notPurchasedMonths: Boolean = false,
    val repurchased: LocalDateTime? = null,
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
        phone: String? = this.phone,
        address: String = this.address,
        phoneVerified: Boolean = this.phoneVerified,
        documentVerified: Boolean = this.documentVerified,
        photoId: String = this.photoId,
        card: String = this.card,
        totalOrderCount: Int = this.totalOrderCount,
        lastPurchased: LocalDateTime? = this.lastPurchased,
        maxPrice: BigDecimal = this.maxPrice,
        averagePrice: BigDecimal = this.averagePrice,
        memo: String = this.memo,
        phoneVerifiedStatus: Int = this.phoneVerifiedStatus,
        dateOfBirth: LocalDate? = this.dateOfBirth,
        firstPurchased: LocalDateTime? = this.firstPurchased,
        totalListPrice: BigDecimal = this.totalListPrice,
        totalSellingPrice: BigDecimal = this.totalSellingPrice,
        domestic: Int = this.domestic,
        gender: Int = this.gender,
        telecom: String = this.telecom,
        notPurchasedMonths: Boolean = this.notPurchasedMonths,
        repurchased: LocalDateTime? = this.repurchased,
        mileage: BigDecimal = this.mileage,
        allowOrder: Boolean = this.allowOrder,
    ): Profile = Profile(
        id = this.id,
        created = this.created,
        modified = this.modified,
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
        userId = this.userId,
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

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            phone: String? = null,
            address: String,
            phoneVerified: Boolean = false,
            documentVerified: Boolean = false,
            photoId: String,
            card: String,
            totalOrderCount: Int = 0,
            lastPurchased: LocalDateTime? = null,
            maxPrice: BigDecimal = BigDecimal.ZERO,
            averagePrice: BigDecimal = BigDecimal.ZERO,
            userId: Int,
            memo: String,
            phoneVerifiedStatus: Int = 0,
            dateOfBirth: LocalDate? = null,
            firstPurchased: LocalDateTime? = null,
            totalListPrice: BigDecimal = BigDecimal.ZERO,
            totalSellingPrice: BigDecimal = BigDecimal.ZERO,
            domestic: Int = 0,
            gender: Int = 0,
            telecom: String,
            notPurchasedMonths: Boolean = false,
            repurchased: LocalDateTime? = null,
            mileage: BigDecimal = BigDecimal.ZERO,
            allowOrder: Boolean = false,
        ): Profile = Profile(
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