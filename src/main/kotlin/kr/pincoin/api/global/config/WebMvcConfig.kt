package kr.pincoin.api.global.config

import kr.pincoin.api.global.security.resolver.ClientInfoArgumentResolver
import kr.pincoin.api.global.security.resolver.CurrentUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val currentUserArgumentResolver: CurrentUserArgumentResolver,
    private val clientInfoArgumentResolver: ClientInfoArgumentResolver,
) : WebMvcConfigurer {
    /**
     * 커스텀 메소드 파라미터 해석기 등록
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {

        // @CurrentUser 애노테이션이 붙은 파라미터에 현재 인증된 사용자 정보를 자동으로 주입
        // 컨트롤러에서 SecurityContextHolder에서 직접 인증 정보를 가져오는 보일러플레이트 코드 제거
        resolvers.add(currentUserArgumentResolver)

        // @ClientInfo 애노테이션이 붙은 파라미터에 클라이언트 정보를 자동으로 주입
        resolvers.add(clientInfoArgumentResolver)
    }
}