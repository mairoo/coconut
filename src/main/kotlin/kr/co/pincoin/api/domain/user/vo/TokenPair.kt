package kr.co.pincoin.api.domain.user.vo

import kr.co.pincoin.api.app.auth.response.AccessTokenResponse

data class TokenPair(
    val accessToken: AccessTokenResponse, // 클라이언트에 전달될 JSON 응답
    val refreshToken: String? // 쿠키에 설정될 토큰 (nullable)
)