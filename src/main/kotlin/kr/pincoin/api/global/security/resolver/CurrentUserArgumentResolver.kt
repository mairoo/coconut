package kr.pincoin.api.global.security.resolver

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.security.annotation.CurrentUser
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.time.LocalDateTime
import java.util.*

@Component
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java) &&
                parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("인증되지 않은 사용자입니다.")

        val jwt = authentication.principal as Jwt
        val roles = jwt.getClaimAsStringList("realm_access.roles") ?: emptyList()

        return User.of(
            keycloakId = UUID.fromString(jwt.getClaimAsString("sub")!!),
            email = jwt.getClaimAsString("email")!!,
            username = jwt.getClaimAsString("preferred_username") ?: "",
            firstName = jwt.getClaimAsString("given_name") ?: "",
            lastName = jwt.getClaimAsString("family_name") ?: "",
            isSuperuser = roles.contains("admin") || roles.contains("ADMIN"),
            isStaff = roles.contains("staff") || roles.contains("STAFF"),
            password = "", // JWT에서는 패스워드 정보 없음
            dateJoined = LocalDateTime.now(), // JWT에서는 가입일 정보 없음
            isActive = true // JWT 토큰이 있다는 것은 활성 사용자
        )
    }
}