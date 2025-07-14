package kr.pincoin.api.app.user.my.service

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
class MyUserService(
) {
}