package kr.pincoin.api.app.s3.admin.service

import kr.pincoin.api.app.s3.admin.response.HealthCheckResponse
import kr.pincoin.api.external.s3.service.S3HealthCheckService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminS3HealthCheckController(
    private val s3HealthCheckService: S3HealthCheckService,
) {
    suspend fun quickHealthCheck(): HealthCheckResponse =
        s3HealthCheckService.quickHealthCheck()

    suspend fun performHealthCheck(): HealthCheckResponse =
        s3HealthCheckService.performHealthCheck()
}