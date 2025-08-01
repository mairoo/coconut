# 스프링부트 백엔드

- 도커 기반 배포 환경
- AWS RDS PostgreSQL 연동
- 서드파티
    - AWS S3 연동
    - Mailgun
    - Aligo
    - Telegram

# 무작위 공격 대비

## 이메일 / 비밀번호 무작위 로그인 공격

- Google reCAPTCHA
- 2FA Google OTP
- 계정 잠금 정책: 연속 실패 시 임시 잠금 (5회 실패 → 15분 잠금)
- 진행형 지연: 실패할수록 응답 시간 증가 (1초 → 2초 → 4초...)
- 디바이스 핑거프린팅: 알려지지 않은 디바이스에서의 접근 감지

## 이메일 무작위 회원 가입 공격

- reCAPTCHA
- 이메일 도메인 화이트리스트: 일회용 이메일 서비스 차단
- 가입 빈도 제한: IP당 일일 가입 횟수 제한 (예: 3회/일)

## 모든 엔드포인트 공통 사항

- Cloudflare WAF
- Rate Limit
- IP 블랙 리스트
- 이메일 블랙 리스트
- 휴대폰번호 블랙 리스트
