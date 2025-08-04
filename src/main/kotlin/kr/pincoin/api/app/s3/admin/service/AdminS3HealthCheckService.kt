package kr.pincoin.api.app.s3.admin.service

import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.s3.admin.response.HealthCheckResponse
import kr.pincoin.api.external.s3.service.S3HealthCheckService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminS3HealthCheckService(
    private val s3HealthCheckService: S3HealthCheckService,
) {
    fun quickHealthCheck(): HealthCheckResponse =
        runBlocking {
            s3HealthCheckService.quickHealthCheck()
        }

    fun performHealthCheck(): HealthCheckResponse =
        runBlocking {
            s3HealthCheckService.performHealthCheck()
        }
}