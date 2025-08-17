package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.app.auth.vo.EmailContent
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.notification.mailgun.api.request.MailgunRequest
import kr.pincoin.api.external.notification.mailgun.service.MailgunApiClient
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import org.springframework.stereotype.Component

/**
 * 이메일 발송 전담 서비스
 *
 * 회원가입 프로세스에서 필요한 모든 이메일 발송을 담당합니다.
 * Mailgun API를 활용하여 안정적이고 확장 가능한 이메일 서비스를 제공합니다.
 *
 * **주요 책임:**
 * 1. 인증 이메일 발송
 *    - 이메일 인증 링크가 포함된 HTML/텍스트 이메일
 *    - 동적 도메인 기반 URL 생성
 *    - TTL 기반 만료 시간 안내
 *
 * 2. 환영 이메일 발송
 *    - 회원가입 완료 축하 메시지
 *    - 개인화된 인사말
 *    - 서비스 이용 안내
 *
 * **이메일 콘텐츠 특징:**
 * - HTML과 텍스트 버전 동시 제공 (호환성 보장)
 * - 반응형 디자인 (모바일 친화적)
 * - 보안 고려사항 (피싱 방지 안내 포함)
 * - 브랜드 일관성 유지
 *
 * **오류 처리:**
 * - 인증 이메일 실패: 회원가입 프로세스 중단
 * - 환영 이메일 실패: 회원가입 완료는 유지 (로그만 기록)
 */
@Component
class SignUpEmailService(
    private val mailgunApiClient: MailgunApiClient,
    private val authProperties: AuthProperties,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 2-2. 인증 이메일 발송
     *
     * 회원가입 1단계에서 이메일 인증을 위한 메일을 발송합니다.
     * 사용자가 클릭할 수 있는 인증 링크와 만료 시간 안내를 포함합니다.
     *
     * **이메일 콘텐츠:**
     * - 인증 링크: 도메인/auth/verify-email/{token}
     * - 만료 시간: 설정 가능한 TTL (기본 24시간)
     * - 보안 안내: 본인이 요청하지 않은 경우 무시 안내
     * - HTML + 텍스트 버전 제공
     *
     * **URL 생성 로직:**
     * - HTTP/HTTPS 자동 감지
     * - 요청 도메인 기반 동적 URL 생성
     * - 멀티 도메인 지원 (개발/스테이징/프로덕션)
     */
    suspend fun sendVerificationEmail(
        email: String,
        token: String,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        val verificationUrl = buildVerificationUrl(token, clientInfo)
        val emailContent = buildVerificationEmailContent(verificationUrl)

        val mailgunRequest = MailgunRequest(
            to = email,
            subject = "이메일 인증을 완료해주세요",
            text = emailContent.text,
            html = emailContent.html,
        )

        try {
            mailgunApiClient.sendEmail(mailgunRequest).block()
        } catch (e: Exception) {
            logger.error { "인증 이메일 발송 실패: email=$email, error=${e.message}" }
            throw BusinessException(UserErrorCode.EMAIL_SEND_FAILED)
        }
    }

    /**
     * 회원가입 완료 환영 이메일 발송
     *
     * 회원가입 2단계 완료 후 사용자에게 환영 메시지를 발송합니다.
     * 개인화된 인사말과 서비스 이용 안내를 포함합니다.
     *
     * **이메일 콘텐츠:**
     * - 개인화된 환영 메시지 (이름 포함)
     * - 회원가입 완료 안내
     * - 서비스 이용 가능 안내
     * - 문의 연락처 정보
     * - HTML + 텍스트 버전 제공
     *
     * **오류 처리:**
     * - 환영 이메일 실패는 회원가입 완료를 방해하지 않음
     * - 실패 시 경고 로그만 기록하고 계속 진행
     * - 향후 재발송 로직 구현 가능
     */
    suspend fun sendWelcomeEmail(email: String, firstName: String) {
        val emailContent = buildWelcomeEmailContent(firstName)

        val mailgunRequest = MailgunRequest(
            to = email,
            subject = "회원가입을 축하합니다!",
            text = emailContent.text,
            html = emailContent.html,
        )

        try {
            mailgunApiClient.sendEmail(mailgunRequest).block()
        } catch (e: Exception) {
            logger.warn { "환영 이메일 발송 실패: email=$email, error=${e.message}" }
            // 8. 회원 가입 완료 안내 이메일 발송
            // 환영 이메일 실패는 회원가입 완료를 방해하지 않음
        }
    }

    /**
     * 인증 URL 생성
     *
     * 클라이언트 정보를 기반으로 이메일 인증 URL을 동적으로 생성합니다.
     *
     * **URL 생성 우선순위:**
     * 1. 실제 요청 도메인 기반 URL (동적 생성)
     * 2. 환경별 기본 URL (fallback)
     *
     * **URL 형식:**
     * - 개발: http://localhost:8080/auth/verify-email/{token}
     * - 프로덕션: https://api.example.com/auth/verify-email/{token}
     */
    private fun buildVerificationUrl(
        token: String,
        clientInfo: ClientUtils.ClientInfo,
    ): String {
        val baseUrl = when {
            // 1. 실제 요청 도메인 기반 URL 생성 (가장 실용적)
            clientInfo.requestDomain.isNotBlank() -> {
                val dynamicUrl = clientInfo.getBaseUrl()
                dynamicUrl
            }

            // 2. 최종 기본값 (프로덕션)
            else -> {
                "https://api.example.com"
            }
        }

        return "$baseUrl/auth/verify-email/$token"
    }

    /**
     * 인증 이메일 콘텐츠 생성
     *
     * 이메일 인증을 위한 HTML과 텍스트 콘텐츠를 생성합니다.
     * 사용자 친화적이면서도 보안을 고려한 메시지를 작성합니다.
     *
     * **콘텐츠 특징:**
     * - 명확한 행동 유도 (인증 버튼)
     * - 만료 시간 명시
     * - 대체 링크 제공 (버튼 작동 안 할 경우)
     * - 보안 안내 (본인이 요청하지 않은 경우)
     * - 모바일 친화적 HTML 디자인
     */
    private fun buildVerificationEmailContent(
        verificationUrl: String,
    ): EmailContent {
        val ttlHours = authProperties.signup.limits.verificationTtl.toHours()

        val text = """
            안녕하세요!
            
            회원가입을 완료하려면 아래 링크를 클릭해주세요:
            $verificationUrl
            
            이 링크는 ${ttlHours}시간 후에 만료됩니다.
            
            만약 회원가입을 신청하지 않으셨다면 이 이메일을 무시해주세요.
        """.trimIndent()

        val html = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                    <h2 style="color: #007bff; margin-top: 0;">이메일 인증</h2>
                    <p>안녕하세요!</p>
                    <p>회원가입을 완료하려면 아래 버튼을 클릭해주세요:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="$verificationUrl" 
                           style="background-color: #007bff; color: white; padding: 12px 30px; 
                                  text-decoration: none; border-radius: 5px; display: inline-block;
                                  font-weight: bold; font-size: 16px;">
                            이메일 인증하기
                        </a>
                    </div>
                    
                    <p>버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저에 붙여넣기 하세요:</p>
                    <p style="word-break: break-all; background-color: #f1f1f1; padding: 10px; border-radius: 4px;">
                        <a href="$verificationUrl" style="color: #007bff;">$verificationUrl</a>
                    </p>
                </div>
                
                <div style="border-top: 1px solid #dee2e6; padding-top: 20px; font-size: 14px; color: #6c757d;">
                    <p><strong>중요:</strong> 이 링크는 <strong>${ttlHours}시간</strong> 후에 만료됩니다.</p>
                    <p><strong>보안 안내:</strong> 만약 회원가입을 신청하지 않으셨다면 이 이메일을 무시해주세요.</p>
                </div>
            </body>
            </html>
        """.trimIndent()

        return EmailContent(text, html)
    }

    /**
     * 환영 이메일 콘텐츠 생성
     *
     * 회원가입 완료를 축하하는 개인화된 환영 메시지를 생성합니다.
     * 친근하면서도 전문적인 톤으로 서비스 이용을 안내합니다.
     *
     * **콘텐츠 특징:**
     * - 개인화된 인사말 (이름 포함)
     * - 회원가입 완료 축하
     * - 서비스 이용 가능 안내
     * - 문의 연락처 제공
     * - 브랜드 일관성 있는 디자인
     */
    private fun buildWelcomeEmailContent(firstName: String): EmailContent {
        val text = """
        안녕하세요 ${firstName}님!
        
        회원가입이 성공적으로 완료되었습니다.
        이제 모든 서비스를 이용하실 수 있습니다.
        
        궁금한 사항이 있으시면 언제든지 문의해 주세요.
        
        감사합니다.
    """.trimIndent()

        val html = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background-color: #f8f9fa; padding: 30px; border-radius: 8px; margin-bottom: 20px; text-align: center;">
                <h2 style="color: #28a745; margin-top: 0; font-size: 28px;">🎉 회원가입 완료!</h2>
                <p style="font-size: 18px; margin: 20px 0;">안녕하세요 <strong style="color: #007bff;">${firstName}</strong>님!</p>
                
                <div style="background-color: white; padding: 20px; border-radius: 6px; margin: 20px 0;">
                    <p style="font-size: 16px; margin: 0;">회원가입이 <strong>성공적으로 완료</strong>되었습니다.</p>
                    <p style="font-size: 16px; margin: 10px 0 0 0;">이제 모든 서비스를 이용하실 수 있습니다.</p>
                </div>
            </div>
            
            <div style="background-color: #e9ecef; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                <h3 style="color: #495057; margin-top: 0;">다음 단계</h3>
                <ul style="color: #6c757d; padding-left: 20px;">
                    <li>프로필 설정 완료하기</li>
                    <li>서비스 기능 둘러보기</li>
                    <li>필요한 설정 확인하기</li>
                </ul>
            </div>
            
            <div style="border-top: 1px solid #dee2e6; padding-top: 20px; font-size: 14px; color: #6c757d; text-align: center;">
                <p>궁금한 사항이 있으시면 언제든지 문의해 주세요.</p>
                <p><strong>감사합니다!</strong></p>
            </div>
        </body>
        </html>
    """.trimIndent()

        return EmailContent(text, html)
    }
}