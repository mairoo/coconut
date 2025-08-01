| 항목           | Netty NIO     | Apache Client  | URLConnection Client   |
|--------------|---------------|----------------|------------------------|
| 방식           | 비동기 / 논블로킹    | 동기 / 블로킹       | 동기 / 블로킹               |
| 성능           | 매우 좋음         | 중간             | 낮음                     |
| 외부 의존성       | Netty 필요      | Apache HTTP 필요 | ❌ 없음 (JDK 내장)          |
| 설정 유연성       | 중간 (Netty 설정) | 매우 높음          | 매우 낮음                  |
| 비동기 클라이언트 사용 | ✅ 가능          | ❌ 불가능          | ❌ 불가능                  |
| 커넥션 풀        | ✅ 지원          | ✅ 지원           | ❌ 없음                   |
| 사용 적합성       | 고성능 서버        | 일반적인 앱         | 단순 CLI, Lambda 등 경량 환경 |
| AWS 권장도      | ✅ 공식 권장       | ✅ 지원           | ✅ 최소화된 환경용             |

```yaml
aws.s3:
  region: ap-northeast-2 # 아시아 태평양 서울
  bucket-name: dummy-bucket
  access-key: dummy-access-key
  secret-key: dummy-secret-key
```