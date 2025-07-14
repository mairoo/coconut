package kr.pincoin.api.domain.user.service

import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class UserValidationService(
    private val userRepository: UserRepository,
    private val userDeletionService: UserDeletionService,
) {

    /**
     * 회원가입 시 이메일 검증
     * - 이미 존재하는 이메일인지 확인
     * - 최근에 삭제된 이메일인지 확인
     */
    fun validateEmailForSignup(email: String) {
        // 이미 존재하는 이메일인지 확인
        if (userRepository.existsByEmail(email)) {
            throw BusinessException(UserErrorCode.ALREADY_EXISTS)
        }

        // 최근에 삭제된 이메일인지 확인
        if (userDeletionService.isEmailDeleted(email)) {
            throw BusinessException(UserErrorCode.EMAIL_RECENTLY_DELETED)
        }
    }

    /**
     * 휴대폰 번호 검증 (프로필 생성 시)
     */
    fun validatePhoneForSignup(phone: String?) {
        if (phone.isNullOrBlank()) return

        // 최근에 삭제된 휴대폰 번호인지 확인
        if (userDeletionService.isPhoneDeleted(phone)) {
            throw BusinessException(UserErrorCode.PHONE_RECENTLY_DELETED)
        }
    }
}