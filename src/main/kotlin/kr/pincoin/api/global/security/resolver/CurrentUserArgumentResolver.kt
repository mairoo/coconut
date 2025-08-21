package kr.pincoin.api.global.security.resolver

import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.global.security.annotation.CurrentUser
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.*

@Component
class CurrentUserArgumentResolver(
    private val userService: UserService,
) : HandlerMethodArgumentResolver {

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
        val keycloakId = UUID.fromString(
            jwt.getClaimAsString("sub")
                ?: throw IllegalStateException("JWT에 sub 클레임이 없습니다.")
        )

        // 데이터베이스에서 실제 User 엔티티 조회
        return userService.findUser(
            UserSearchCriteria(
                keycloakId = keycloakId,
                isActive = true,
            )
        )
    }
}