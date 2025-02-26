package kr.co.pincoin.api.global.security.totp

import org.apache.commons.codec.binary.Base32
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor
import kotlin.math.pow

@Service
class TOTPService {
    /**
     * 새로운 OTP 비밀키를 생성합니다.
     * @return Base32로 인코딩된 비밀키
     */
    fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(SECRET_SIZE)
        random.nextBytes(bytes)
        return Base32().encodeToString(bytes)
    }

    /**
     * 현재 시간 기준으로 OTP 코드를 생성합니다.
     * @param secretKey Base32로 인코딩된 비밀키
     * @return 6자리 OTP 코드
     */
    fun generateTOTP(secretKey: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        return generateTOTPAtTime(secretKey, currentTimeMillis)
    }

    /**
     * 특정 시간에 대한 OTP 코드를 생성합니다.
     * RFC 6238 TOTP 알고리즘 구현
     * @param secretKey Base32로 인코딩된 비밀키
     * @param time 시간 (밀리초)
     * @return 6자리 OTP 코드
     */
    fun generateTOTPAtTime(
        secretKey: String,
        time: Long,
    ): String {
        // Step 1: 현재 시간을 30초 단위의 타임스텝으로 변환
        // (예: 1970년 이후 경과된 30초 단위의 수)
        val timeStep = floor(time / 1000 / TIME_STEP.toDouble()).toLong()

        // Step 2: Base32로 인코딩된 비밀키를 바이트 배열로 디코딩
        val secretBytes = Base32().decode(secretKey)

        // Step 3: 타임스텝을 8바이트 빅엔디안 바이트 배열로 변환
        // (예: timeStep이 1이면 [0,0,0,0,0,0,0,1])
        val timeBytes = ByteArray(8)
        var value = timeStep
        for (i in 7 downTo 0) {
            timeBytes[i] = (value and 0xFF).toByte()
            value = value shr 8
        }

        // Step 4: HMAC-SHA1을 사용하여 timeBytes를 secretBytes로 해싱
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(SecretKeySpec(secretBytes, "HmacSHA1"))
        val hash = hmac.doFinal(timeBytes)

        // Step 5: 동적 절사(Dynamic Truncation)
        // - 해시의 마지막 바이트에서 하위 4비트를 옵셋으로 사용
        val offset = hash[hash.size - 1].toInt() and 0xF

        // Step 6: 옵셋부터 4바이트를 31비트 정수로 추출
        // - 첫 바이트의 최상위 비트는 항상 0으로 설정 (and 0x7F)
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        // Step 7: 정수를 6자리로 만들기 위해 10^6으로 나눈 나머지를 구함
        val otp = binary % 10.0.pow(OTP_LENGTH.toDouble()).toInt()

        // Step 8: 최종적으로 6자리 문자열로 변환 (빈자리는 0으로 패딩)
        return String.format("%0${OTP_LENGTH}d", otp)
    }

    /**
     * OTP 코드의 유효성을 검증합니다.
     * 예시:
     * @PostMapping("/verify-otp")
     * fun verifyOTP(@RequestBody request: OTPRequest): Boolean {
     *     return totpService.verifyTOTP(
     *         secretKey = userSecretKey, // DB에서 조회한 사용자의 secret key
     *         inputCode = request.code   // 사용자가 입력한 6자리 코드
     *     )
     * }
     *
     * @param secretKey Base32로 인코딩된 비밀키
     * @param inputCode 사용자가 입력한 OTP 코드
     * @return 유효성 여부
     */
    fun verifyTOTP(
        secretKey: String,
        inputCode: String,
    ): Boolean {
        val currentTimeMillis = System.currentTimeMillis()

        // 현재 시간 기준 검증
        if (generateTOTPAtTime(secretKey, currentTimeMillis) == inputCode) return true

        // 앞뒤 window 만큼 확인 (-1, +1)
        for (offset in -VALID_WINDOW..VALID_WINDOW) {
            val checkTime = currentTimeMillis + (offset * TIME_STEP * 1000L)
            if (generateTOTPAtTime(secretKey, checkTime) == inputCode) return true
        }
        return false
    }

    /**
     * TOTP(Time-based One-Time Password) 설정을 위한 URI를 생성합니다.
     * 구글이 정의하고 현재는 업계 표준이 된 otpauth:// 프로토콜을 사용합니다.
     *
     * 생성되는 URI 형식: otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}
     *
     * 필수 파라미터:
     * - issuer: 서비스 제공자 이름 (보안상 URL path와 query parameter 모두에 포함)
     * - account: 사용자 식별자 (보통 이메일)
     * - secret: Base32로 인코딩된 비밀키
     *
     * 기본값 사용:
     * - digits: 6 (OTP 자릿수)
     * - period: 30 (갱신 주기, 초)
     *
     * 사용 예시:
     * val uri = totpService.generateTOTPUri(
     *     secretKey = "JBSWY3DPEHPK3PXP",
     *     accountName = "user@example.com",
     *     issuer = "YourService"
     * )
     * // 결과: otpauth://totp/YourService:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=YourService
     *
     * 생성된 URI는 Google Authenticator, Microsoft Authenticator, Authy 등
     * TOTP를 지원하는 모든 인증 앱에서 QR 코드 스캔 또는 직접 입력으로 설정 가능합니다.
     *
     * @param secretKey Base32로 인코딩된 비밀키
     * @param accountName 계정명 (예: "user@example.com")
     * @param issuer 발급자명 (예: "MyService")
     * @return TOTP 설정을 위한 표준 otpauth:// URI
     */
    fun generateTOTPUri(
        secretKey: String,
        accountName: String,
        issuer: String,
    ): String {
        val encodedIssuer = java.net.URLEncoder.encode(issuer, "UTF-8")
        val encodedAccountName = java.net.URLEncoder.encode(accountName, "UTF-8")
        return "otpauth://totp/$encodedIssuer:$encodedAccountName?secret=$secretKey&issuer=$encodedIssuer"
    }

    companion object {
        /**
         * RFC 6238(TOTP: Time-based One-Time Password)과 RFC 4226(HOTP: HMAC-based One-Time Password) 표준 스펙을 따르는 설정값들
         */

        /**
         * TOTP 비밀키의 바이트 길이
         * RFC 4226에서 권장하는 160비트(20바이트)로 충분한 암호학적 강도 제공
         * - 기반 알고리즘: HmacSHA1
         */
        private const val SECRET_SIZE = 20

        /**
         * OTP 갱신 주기(초)
         * RFC 6238에서 권장하고 대부분의 TOTP 구현체가 채택한 표준값
         * - Google Authenticator, Microsoft Authenticator, Authy 등이 모두 사용
         */
        private const val TIME_STEP = 30

        /**
         * OTP 코드의 자릿수
         * RFC 4226에서 권장하는 표준값
         * - 보안성과 사용성의 균형점으로 채택됨
         * - 대부분의 TOTP 구현체가 사용하는 값
         */
        private const val OTP_LENGTH = 6

        /**
         * OTP 검증 시 허용하는 시간 범위
         * 현재 시점 기준 전후 1스텝(30초)를 허용
         * - 서버와 클라이언트 간의 시간 차이를 고려
         * - RFC 6238에서 권장하는 방식
         */
        private const val VALID_WINDOW = 1
    }
}