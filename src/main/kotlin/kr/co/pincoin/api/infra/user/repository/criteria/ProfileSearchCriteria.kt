package kr.co.pincoin.api.infra.user.repository.criteria

import kr.co.pincoin.api.domain.user.enums.ProfileDomestic
import kr.co.pincoin.api.domain.user.enums.ProfileGender
import kr.co.pincoin.api.domain.user.enums.ProfilePhoneVerifiedStatus

data class ProfileSearchCriteria(
    val id: Long? = null,
    val userId: Int? = null,
    val phone: String? = null,
    val phoneVerified: Boolean? = null,
    val documentVerified: Boolean? = null,
    val phoneVerifiedStatus: ProfilePhoneVerifiedStatus? = null,
    val domestic: ProfileDomestic? = null,
    val gender: ProfileGender? = null,
    val telecom: String? = null,
    val memo: String? = null,
    val notPurchasedMonths: Boolean? = null,
    val allowOrder: Boolean? = null,
)