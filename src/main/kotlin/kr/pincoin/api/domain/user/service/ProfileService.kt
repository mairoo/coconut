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
    fun createProfile(userId: Int): Profile {
        return profileRepository.save(
            Profile.of(
                userId = userId,
                phone = null,
                address = "",
                phoneVerified = false,
                documentVerified = false,
                photoId = "",
                card = "",
                totalOrderCount = 0,
                lastPurchased = null,
                maxPrice = BigDecimal.ZERO,
                averagePrice = BigDecimal.ZERO,
                memo = "",
                phoneVerifiedStatus = 0,
                dateOfBirth = null,
                firstPurchased = null,
                totalListPrice = BigDecimal.ZERO,
                totalSellingPrice = BigDecimal.ZERO,
                domestic = 0,
                gender = 0,
                telecom = "",
                notPurchasedMonths = false,
                repurchased = null,
                mileage = BigDecimal.ZERO,
                allowOrder = true,
            )
        )
    }
}