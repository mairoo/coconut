package kr.pincoin.api.global.constant

object RedisKey {
    const val EMAIL = "email"
    const val IP_ADDRESS = "ipAddress"

    // 소프트 삭제 관련
    const val DELETED_EMAIL_PREFIX = "deleted:email:"
    const val DELETED_PHONE_PREFIX = "deleted:phone:"
}