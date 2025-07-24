package kr.pincoin.api.external.s3.api.response

import java.time.LocalDateTime

data class S3HealthCheckResponse(
    val status: String, // SUCCESS, PARTIAL_FAILURE, FAILURE
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val bucketName: String,
    val region: String,
    val endpoint: String?,
    val checks: List<S3CheckResult>
)