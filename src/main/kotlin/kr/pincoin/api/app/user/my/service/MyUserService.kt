package kr.pincoin.api.app.user.my.service

import kr.pincoin.api.app.user.my.request.MyPasswordChangeRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
class MyUserService(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) {
    fun updatePassword(
        userId: Int,
        request: MyPasswordChangeRequest,
    ): User {
        val user = userService.findUser(userId, UserSearchCriteria(isActive = true))

        if (!passwordEncoder.matches(request.oldPassword, user.password)) {
            throw BusinessException(UserErrorCode.PASSWORD_MISMATCH)
        }

        return userService.updatePassword(user, request.newPassword)
    }
}