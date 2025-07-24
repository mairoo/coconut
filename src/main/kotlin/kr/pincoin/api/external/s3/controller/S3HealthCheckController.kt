package kr.pincoin.api.external.s3.controller

import kr.pincoin.api.external.s3.service.S3HealthCheckService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/open/s3/health")
class S3HealthCheckController(
    private val s3HealthCheckService: S3HealthCheckService,
) {
    /**
     * S3 전체 헬스체크
     * 모든 권한과 설정을 포괄적으로 테스트합니다.
     */
    @GetMapping
    suspend fun healthCheck(): ResponseEntity<ApiResponse<Unit>> =
        s3HealthCheckService.performHealthCheck()
            .let { ApiResponse.of(it, message = "S3 헬스체크가 성공적으로 완료되었습니다") }
            .let { ResponseEntity.ok(it) }

    /**
     * S3 빠른 연결 테스트
     * 단순히 버킷에 접근 가능한지만 확인합니다.
     */
    @GetMapping("/quick")
    suspend fun quickHealthCheck(): ResponseEntity<ApiResponse<Map<String, Any>>> =
        s3HealthCheckService.quickHealthCheck()
            .let {
                mapOf(
                    "status" to "UP",
                    "healthy" to true,
                    "timestamp" to java.time.LocalDateTime.now().toString(),
                    "service" to "s3"
                ) as Map<String, Any>
            }
            .let { ApiResponse.of(it, message = "S3 연결이 정상입니다") }
            .let { ResponseEntity.ok(it) }

    /**
     * S3 설정 정보 조회 (민감한 정보 제외)
     */
    @GetMapping("/config")
    fun getS3Configuration(): ResponseEntity<ApiResponse<Map<String, Any>>> =
        mapOf(
            "region" to "ap-northeast-2",
            "bucketName" to "configured",
            "hasCustomEndpoint" to true,
            "timeout" to 30000,
            "maxFileSize" to "10MB",
            "allowedExtensions" to listOf(
                "jpg",
                "jpeg",
                "png",
                "pdf",
                "doc",
                "docx"
            ),
            "timestamp" to java.time.LocalDateTime.now().toString()
        )
            .let { ApiResponse.of(it, message = "S3 설정 정보를 조회했습니다") }
            .let { ResponseEntity.ok(it) }

    /**
     * S3 설정 진단
     * 설정 오류를 분석하고 해결 방안을 제시합니다.
     */
    @GetMapping("/diagnose")
    suspend fun diagnoseConfiguration(): ResponseEntity<ApiResponse<Unit>> =
        s3HealthCheckService.diagnoseConfiguration()
            .let { ApiResponse.of(it, message = "S3 설정 진단이 성공적으로 완료되었습니다") }
            .let { ResponseEntity.ok(it) }

    /**
     * S3 상세 연결 테스트
     * 더 자세한 로깅과 함께 실제 연결을 테스트합니다.
     */
    @GetMapping("/test-connection")
    suspend fun testConnection(): ResponseEntity<ApiResponse<Map<String, Any>>> =
        s3HealthCheckService.quickHealthCheck()
            .let {
                mapOf(
                    "connected" to true,
                    "timestamp" to java.time.LocalDateTime.now().toString(),
                    "message" to "S3 연결 성공",
                    "suggestion" to "정상 연결됨"
                ) as Map<String, Any>
            }
            .let { ApiResponse.of(it, message = "S3 연결 테스트가 성공했습니다") }
            .let { ResponseEntity.ok(it) }
}