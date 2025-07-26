package kr.pincoin.api.app.auth.service

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import org.springframework.stereotype.Service

/**
 * 인증 관련 메인 서비스 (Facade)
 *
 * 복잡한 회원가입 프로세스를 단순한 인터페이스로 제공합니다.
 * 실제 비즈니스 로직은 하위 시스템들에 위임하고, 전체 플로우만 조율합니다.
 *
 * **주요 책임:**
 * - 클라이언트에게 단순한 인터페이스 제공
 * - 하위 시스템들 간의 협업 조율
 * - 전체 프로세스의 트랜잭션 경계 관리
 *
 * **하위 시스템:**
 * - SignUpFacade: 회원가입 프로세스 전체 관리
 * - SignInFacade: 로그인 프로세스 관리 (향후 구현)
 * - TokenFacade: 토큰 관리 (향후 구현)
 */
@Service
class AuthService(
    private val signUpFacade: SignUpFacade,
    // 향후 추가될 다른 인증 관련 퍼사드들
    // private val signInFacade: SignInFacade,
    // private val tokenFacade: TokenFacade,
    // private val passwordResetFacade: PasswordResetFacade,
) {

    /**
     * 회원가입 요청 처리
     *
     * 이메일 인증이 필요한 2단계 회원가입의 첫 번째 단계입니다.
     * - 입력값 검증 (reCAPTCHA, 이메일 도메인, IP 제한 등)
     * - 인증 이메일 발송
     * - 임시 데이터 저장
     *
     * @param request 회원가입 요청 정보
     * @param httpServletRequest HTTP 요청 정보 (IP, User-Agent 등)
     * @return 이메일 인증 안내 응답
     */
    fun signUp(
        request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ): SignUpRequestedResponse =
        signUpFacade.processSignUpRequest(request, httpServletRequest)

    /**
     * 이메일 인증 완료 및 회원가입 완료 처리
     *
     * 이메일 인증이 필요한 2단계 회원가입의 두 번째 단계입니다.
     * - 인증 토큰 검증
     * - Keycloak 사용자 생성
     * - 데이터베이스 사용자 정보 저장
     * - 임시 데이터 정리
     * - 환영 이메일 발송
     *
     * @param token 이메일 인증 토큰
     * @return 회원가입 완료 응답
     */
    fun verifyEmailAndCompleteSignup(
        token: String,
    ): SignUpCompletedResponse =
        signUpFacade.completeSignUp(token)

    // 향후 추가될 메서드들

    /**
     * 사용자 로그인
     *
     * TODO: SignInFacade로 위임 예정
     * - Keycloak 우선 인증
     * - 레거시 사용자 마이그레이션
     * - JWT 토큰 발급
     */
    // fun signIn(request: SignInRequest): SignInResponse = SignInFacade.processLogin(request)

    /**
     * JWT 토큰 갱신
     *
     * TODO: TokenFacade로 위임 예정
     */
    // fun refreshToken(request: RefreshTokenRequest): TokenResponse = tokenFacade.refreshToken(request)

    /**
     * 사용자 로그아웃
     *
     * TODO: TokenFacade로 위임 예정
     */
    // fun signOut(request: SignOuttRequest): SignOutResponse = tokenFacade.signOut(request)

    /**
     * 비밀번호 재설정 요청
     *
     * TODO: PasswordResetFacade로 위임 예정
     */
    // fun requestPasswordReset(request: PasswordResetRequest): PasswordResetResponse =
    //     passwordResetFacade.requestPasswordReset(request)
}