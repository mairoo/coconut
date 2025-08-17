package kr.pincoin.api.global.security.resolver

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.global.security.annotation.ClientInfo
import kr.pincoin.api.global.utils.ClientUtils
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ClientInfoArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(ClientInfo::class.java) &&
                parameter.parameterType == ClientUtils.ClientInfo::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val httpServletRequest = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest를 찾을 수 없습니다")

        return ClientUtils.getClientInfo(httpServletRequest)
    }
}