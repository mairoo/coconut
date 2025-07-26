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

# 회원 가입 예시

```
// ✅ 1. 정상적인 회원가입 (완전한 정보)
{
  "username": "testuser123",
  "password": "SecurePass123!",
  "email": "test@example.com",
  "firstName": "길동",
  "lastName": "홍",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ✅ 2. 정상적인 회원가입 (필수 필드만)
{
  "username": "minimaluser",
  "password": "MinimalPass1@",
  "email": "minimal@naver.com"
}

// ✅ 3. 정상적인 회원가입 (다양한 이메일 도메인)
{
  "username": "kakaouser",
  "password": "KakaoTest123#",
  "email": "kakaouser@kakao.com",
  "firstName": "카카오",
  "lastName": "유저",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ✅ 4. 정상적인 회원가입 (영문 이름)
{
  "username": "johnsmith",
  "password": "JohnSmith2024$",
  "email": "john.smith@outlook.com",
  "firstName": "John",
  "lastName": "Smith",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ❌ 5. 유효성 검사 실패 - 사용자명 너무 짧음
{
  "username": "ab",
  "password": "ValidPass123!",
  "email": "test@gmail.com",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 6. 유효성 검사 실패 - 사용자명 너무 김
{
  "username": "verylongusernamethatexceedsthirtychars",
  "password": "ValidPass123!",
  "email": "test@gmail.com",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 7. 유효성 검사 실패 - 비밀번호 패턴 불일치 (특수문자 없음)
{
  "username": "testuser789",
  "password": "OnlyLettersAndNumbers123",
  "email": "test@gmail.com",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 8. 유효성 검사 실패 - 비밀번호 너무 짧음
{
  "username": "testuser456",
  "password": "Short1!",
  "email": "test@gmail.com",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 9. 유효성 검사 실패 - 잘못된 이메일 형식
{
  "username": "testuser999",
  "password": "ValidPass123!",
  "email": "invalid-email-format",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 10. 유효성 검사 실패 - 필수 필드 누락
{
  "password": "ValidPass123!",
  "email": "test@gmail.com",
  "firstName": "테스트",
  "lastName": "유저"
}

// ❌ 11. 유효성 검사 실패 - 이름 너무 김
{
  "username": "testuser888",
  "password": "ValidPass123!",
  "email": "test@gmail.com",
  "firstName": "매우긴이름매우긴이름매우긴이름매우긴이름매우긴이름매우긴이름",
  "lastName": "유저"
}

// ⚠️ 12. 차단된 이메일 도메인 (일회용 이메일)
{
  "username": "tempuser123",
  "password": "TempEmail123!",
  "email": "temp@tempmail.org",
  "firstName": "임시",
  "lastName": "유저",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ⚠️ 13. reCAPTCHA 토큰 없음
{
  "username": "nocaptcha123",
  "password": "NoCaptcha123!",
  "email": "nocaptcha@gmail.com",
  "firstName": "캡차",
  "lastName": "없음"
}

// ✅ 14. 한글 이름이 포함된 정상 케이스
{
  "username": "koreanuser",
  "password": "Korean123!@",
  "email": "korean@daum.net",
  "firstName": "김철수",
  "lastName": "김",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ✅ 15. 특수문자가 다양한 비밀번호
{
  "username": "specialchar",
  "password": "Special@#$%123",
  "email": "special@yahoo.com",
  "firstName": "특수문자",
  "lastName": "테스트",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ❌ 16. 빈 문자열 테스트
{
  "username": "",
  "password": "",
  "email": "",
  "firstName": "",
  "lastName": ""
}

// ✅ 17. 경계값 테스트 - 최소 길이
{
  "username": "abc",
  "password": "MinLen8!",
  "email": "a@b.co",
  "firstName": "",
  "lastName": "",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ✅ 18. 경계값 테스트 - 최대 길이
{
  "username": "maxlengthusernamethirtychars",
  "password": "MaxLength30CharPassword123!@#",
  "email": "maxlength@hotmail.com",
  "firstName": "최대길이테스트이름스물아홉글자테스트용이름최대",
  "lastName": "최대길이테스트성씨스물아홉글자테스트용성씨최대",
  "recaptchaToken": "03AGdBq27SiYhiOI4Co_4q8hGjrXVBl4b9zJGb5VoVcXSNn9"
}

// ❌ 19. SQL 인젝션 시도
{
  "username": "admin'; DROP TABLE users; --",
  "password": "Injection123!",
  "email": "injection@gmail.com",
  "firstName": "'; DROP TABLE --",
  "lastName": "유저"
}

// ❌ 20. XSS 시도
{
  "username": "<script>alert('xss')</script>",
  "password": "XssTest123!",
  "email": "xss@gmail.com",
  "firstName": "<script>alert('xss')</script>",
  "lastName": "유저"
}

```