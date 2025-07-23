# SSO 전략

## 📌 백엔드(AuthController)의 역할

* 사용자 인증 처리 (`/sign-in`, `/sign-out`, `/refresh`)
* 토큰 발급 (`AccessToken`, `RefreshToken`)
* `Set-Cookie` 헤더를 통해 **`HttpOnly Secure` 쿠키로 refreshToken 저장**
* `.example.com` 도메인 하위에서만 접근 가능하도록 쿠키 도메인 설정
* 보안 헤더까지 추가 → **완성된 응답을 프론트로 전송**

> ✅ 즉, 백엔드는 여기까지 하면 **역할 종료**.

`cookieDomain`을 `.example.com`처럼 공유 가능한 도메인으로 설정하면, **백엔드에서의 토큰 저장 책임은 거기서 끝**입니다. 더 이상 프론트 간의 쿠키 공유나 로그인 상태 유지 같은 건 **프론트엔드 애플리케이션들의 책임**이 됩니다.

---

## ✅ 이후 책임은 프론트의 몫

### 프론트는 다음을 해야 합니다:

1. **액세스 토큰 저장 및 관리**

    * 서버 응답 body에 포함된 AccessToken을 `memory` 또는 `localStorage` 등 클라이언트 측에 저장
    * 필요한 API 호출 시 `Authorization: Bearer` 헤더에 포함해서 사용

2. **쿠키로 저장된 리프레시 토큰은 자동 전송**

    * 동일한 `.example.com` 하위 도메인에서는 쿠키 자동 전송
    * 백엔드 `/refresh` 요청 시 `@CookieValue`로 잘 수신됨

3. **탭 간 로그인/로그아웃 동기화**

    * BroadcastChannel 또는 Storage Event 사용
    * 또는 `/refresh` 실패 시 자동 로그아웃 처리

---

* `.example.com` 쿠키 도메인 설정 → 프론트 도메인 간 공유 OK
* `HttpOnly`, `Secure`, `SameSite` 등 보안 옵션도 잘 처리됨
* 토큰 발급과 만료, 쿠키 설정 로직도 깔끔하게 추상화됨

👉 이제 프론트가 이 쿠키를 바탕으로 **SSO 상태 유지**와 **토큰 갱신 시점 제어**만 잘 하면 됩니다.