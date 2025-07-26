package kr.pincoin.api.app.auth.vo

import kr.pincoin.api.app.auth.response.AccessTokenResponse

/**
 * 로그인 결과 데이터 클래스
 * - AccessTokenResponse
 * - refreshToken
 */
data class SignInResult(
    val accessTokenResponse: AccessTokenResponse,
    val refreshToken: String?,
    val refreshExpiresIn: Long?,
)