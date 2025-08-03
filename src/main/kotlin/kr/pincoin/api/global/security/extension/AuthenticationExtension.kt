package kr.pincoin.api.global.security.extension

import kr.pincoin.api.global.security.model.CurrentUserInfo
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt

fun Authentication.getCurrentUser(): CurrentUserInfo {
    val jwt = this.principal as Jwt
    return CurrentUserInfo(
        keycloakId = jwt.getClaimAsString("sub")!!,
        email = jwt.getClaimAsString("email")!!,
        username = jwt.getClaimAsString("preferred_username"),
        roles = jwt.getClaimAsStringList("realm_access.roles") ?: emptyList()
    )
}

fun Authentication.getCurrentKeycloakId(): String {
    val jwt = this.principal as Jwt
    return jwt.getClaimAsString("sub")!!
}

fun Authentication.getCurrentEmail(): String {
    val jwt = this.principal as Jwt
    return jwt.getClaimAsString("email")!!
}