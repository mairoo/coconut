# 구글 Recaptcha 설정

- https://www.google.com/recaptcha/admin/ 접속

## 개발 환경

- 라벨: localhost:3000
- reCAPTCHA 유형:v3
- reCAPTCHA 키
  - 사이트 키 복사 (프론트엔드 공개키)
  - 비밀 키 복사 (백엔드 비밀키)
- 도메인
    - localhost
    - 127.0.0.1
    - www.pincoin.local
    - card.pincoin.local
- reCAPTCHA 정답의 소스를 확인합니다: (체크)
- 이 키가 AMP 페이지와 작동하는 것을 허용합니다: (체크 안 함)
- 소유자에게 알림을 발송합니다: (체크)

## 운영 환경

- 라벨: pincoin.kr
- reCAPTCHA 유형:v3
- reCAPTCHA 키
    - 사이트 키 복사 (프론트엔드 공개키)
    - 비밀 키 복사 (백엔드 비밀키)
- 도메인
    - pincoin.kr
    - www.pincoin.kr
    - card.pincoin.kr

- reCAPTCHA 정답의 소스를 확인합니다: (체크)
- 이 키가 AMP 페이지와 작동하는 것을 허용합니다: (체크 안 함)
- 소유자에게 알림을 발송합니다: (체크 안 함)

# 사용법
```kotlin

// v2 검증
val result = recaptchaService.verifyV2(token)

// v3 검증  
val result = recaptchaService.verifyV3(token, minScore = 0.7)

// 결과 처리
when (result) {
    is RecaptchaResponse.Success -> {
        // 성공 처리
        println("점수: ${result.data.score}")
    }
    is RecaptchaResponse.Error -> {
        // 에러 처리
        println("에러: ${result.errorMessage}")
    }
}
```