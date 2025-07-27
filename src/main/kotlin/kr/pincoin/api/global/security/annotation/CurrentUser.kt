package kr.pincoin.api.global.security.annotation

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal(expression = "user")
annotation class CurrentUser

// @AuthenticationPrincipal(expression = "user") User user
// UserDetailsAdapter, UserDetailsServiceAdapter 어댑터 패턴 사용에 따라 AuthenticationPrincipal 사용 불가
//
// @CurrentUser User user 컨트롤러 메소드의 파라미터에서 user 객체 주입