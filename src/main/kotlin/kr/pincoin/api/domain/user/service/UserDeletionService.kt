package kr.pincoin.api.domain.user.service

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.constant.RedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class UserDeletionService(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    /**
     * 소프트 삭제된 사용자의 이메일과 휴대폰 번호를 Redis에 30일간 저장
     */
    fun markAsDeleted(user: User) {
        val deletedAt = LocalDateTime.now().toString()
        val ttlDays = 90L

        // 삭제된 이메일 저장
        redisTemplate.opsForValue().set(
            "${RedisKey.DELETED_EMAIL_PREFIX}${user.email}",
            deletedAt,
            ttlDays,
            TimeUnit.DAYS
        )

        // 프로필에서 휴대폰 번호가 있다면 저장 (UserProfileProjection 사용 시)
        // 여기서는 User 엔티티에 phone 필드가 없으므로 별도 처리 필요
        // markPhoneAsDeleted(user.phone, deletedAt, ttlDays)
    }

    /**
     * 휴대폰 번호를 삭제 목록에 추가
     */
    fun markPhoneAsDeleted(phone: String?) {
        if (phone.isNullOrBlank()) return

        val deletedAt = LocalDateTime.now().toString()
        val ttlDays = 90L

        redisTemplate.opsForValue().set(
            "${RedisKey.DELETED_PHONE_PREFIX}$phone",
            deletedAt,
            ttlDays,
            TimeUnit.DAYS
        )
    }

    /**
     * 이메일이 삭제된 사용자의 것인지 확인
     */
    fun isEmailDeleted(email: String): Boolean {
        return redisTemplate.hasKey("${RedisKey.DELETED_EMAIL_PREFIX}$email")
    }

    /**
     * 휴대폰 번호가 삭제된 사용자의 것인지 확인
     */
    fun isPhoneDeleted(phone: String): Boolean {
        return redisTemplate.hasKey("${RedisKey.DELETED_PHONE_PREFIX}$phone")
    }

    /**
     * 삭제된 이메일 정보 조회 (언제 삭제되었는지)
     */
    fun getDeletedEmailInfo(email: String): String? {
        return redisTemplate.opsForValue().get("${RedisKey.DELETED_EMAIL_PREFIX}$email")
    }

    /**
     * 삭제된 휴대폰 정보 조회 (언제 삭제되었는지)
     */
    fun getDeletedPhoneInfo(phone: String): String? {
        return redisTemplate.opsForValue().get("${RedisKey.DELETED_PHONE_PREFIX}$phone")
    }
}