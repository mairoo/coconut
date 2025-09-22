package kr.pincoin.api.domain.user.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.domain.user.error.ProfileErrorCode
import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.domain.user.repository.ProfileRepository
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProfileService(
    private val profileRepository: ProfileRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun save(
        profile: Profile,
    ): Profile {
        try {
            return profileRepository.save(profile)
        } catch (_: DataIntegrityViolationException) {
            logger.error { "프로필 중복: userId=${profile.userId}" }
            throw BusinessException(ProfileErrorCode.ALREADY_EXISTS)
        }
    }
}