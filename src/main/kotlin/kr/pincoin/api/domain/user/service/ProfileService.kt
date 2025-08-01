package kr.pincoin.api.domain.user.service

import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.domain.user.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProfileService(
    private val profileRepository: ProfileRepository,
) {
    @Transactional
    fun createProfile(userId: Int): Profile =
        profileRepository.save(
            Profile.of(
                userId = userId,
                address = "",
                phone = null,
                phoneVerified = false,
                phoneVerifiedStatus = 0,
                dateOfBirth = null,
                domestic = 0,
                gender = 0,
                telecom = "",
                photoId = "",
                card = "",
                documentVerified = false,
                totalOrderCount = 0,
                firstPurchased = null,
                lastPurchased = null,
                maxPrice = BigDecimal.ZERO,
                averagePrice = BigDecimal.ZERO,
                totalListPrice = BigDecimal.ZERO,
                totalSellingPrice = BigDecimal.ZERO,
                notPurchasedMonths = false,
                repurchased = null,
                memo = "",
                mileage = BigDecimal.ZERO,
                allowOrder = true,
            )
        )
}