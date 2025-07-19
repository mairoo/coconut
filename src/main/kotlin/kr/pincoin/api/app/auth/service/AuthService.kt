package kr.pincoin.api.app.auth.service

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.request.UserCreateRequest
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.vo.TokenPair

/**
 * 인증 서비스 인터페이스
 *
 * 다양한 인증 전략을 지원하기 위한 공통 인터페이스
 * - JWT 기반 인증 (레거시)
 * - Keycloak 기반 인증 (신규)
 * - 하이브리드 인증 (이관 중)
 */
interface AuthService {
    /**
     * 사용자 로그인 처리 및 토큰 발급
     */
    fun login(
        request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): TokenPair

    /**
     * 리프레시 토큰을 사용하여 새로운 토큰 쌍 발급
     */
    fun refresh(
        refreshToken: String,
        servletRequest: HttpServletRequest,
    ): TokenPair

    /**
     * 로그아웃 처리
     */
    fun logout(
        refreshToken: String,
    )

    /**
     * 새 사용자 생성
     */
    fun createUser(
        request: UserCreateRequest,
    ): User
}