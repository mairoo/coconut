package kr.pincoin.api.external.s3.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kr.pincoin.api.external.s3.error.S3ErrorCode
import kr.pincoin.api.external.s3.properties.S3Properties
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.time.LocalDateTime
import java.util.*

@Service
class S3HealthCheckService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties,
) {
    private val logger = KotlinLogging.logger {}
    private val testFileName = "health-check-test.txt"
    private val testContent = "S3 Health Check Test - ${UUID.randomUUID()}"

    /**
     * S3 연결 및 기본 권한 테스트
     */
    suspend fun performHealthCheck(): Unit =
        withContext(Dispatchers.IO) {
            try {
                logger.info { "S3 헬스체크 시작" }

                // 1. 버킷 존재 여부 및 접근 권한 확인
                checkBucketAccess()

                // 2. 파일 업로드 권한 확인
                checkUploadPermission()

                // 3. 파일 읽기 권한 확인
                checkReadPermission()

                // 4. 파일 삭제 권한 확인
                checkDeletePermission()

                // 5. 버킷 정책 및 설정 확인
                checkBucketConfiguration()

                logger.info { "S3 헬스체크 성공" }

            } catch (e: BusinessException) {
                throw e
            } catch (e: Exception) {
                logger.error(e) { "헬스체크 중 예상치 못한 오류 발생" }
                throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
            }
        }

    /**
     * 버킷 존재 여부 및 접근 권한 확인
     */
    private suspend fun checkBucketAccess() {
        try {
            withTimeout(s3Properties.timeout) {
                val startTime = System.currentTimeMillis()

                logger.info { "버킷 접근 테스트 시작 - 버킷: ${s3Properties.bucketName}, 지역: ${s3Properties.region}" }

                val headBucketRequest = HeadBucketRequest.builder()
                    .bucket(s3Properties.bucketName)
                    .build()

                s3Client.headBucket(headBucketRequest)

                val duration = System.currentTimeMillis() - startTime
                logger.info { "버킷 접근 성공 - 소요 시간: ${duration}ms" }
            }
        } catch (_: NoSuchBucketException) {
            logger.error { "버킷이 존재하지 않음: ${s3Properties.bucketName}" }
            throw BusinessException(S3ErrorCode.BUCKET_NOT_FOUND)
        } catch (e: S3Exception) {
            val errorMessage = e.awsErrorDetails()?.errorMessage() ?: e.message
            val errorCode = e.awsErrorDetails()?.errorCode()
            val statusCode = e.statusCode()

            logger.error {
                "S3 오류 발생 - 상태코드: $statusCode, 오류코드: $errorCode, 메시지: $errorMessage, " +
                        "Access Key: ${s3Properties.accessKey.take(8)}..., 지역: ${s3Properties.region}, " +
                        "버킷명: ${s3Properties.bucketName}, AWS 요청 ID: ${e.requestId()}"
            }

            val businessErrorCode = when (statusCode) {
                400 -> S3ErrorCode.CONNECTION_FAILED
                403 -> S3ErrorCode.ACCESS_DENIED
                404 -> S3ErrorCode.BUCKET_NOT_FOUND
                else -> S3ErrorCode.CONNECTION_FAILED
            }
            throw BusinessException(businessErrorCode)
        } catch (_: TimeoutCancellationException) {
            logger.error { "버킷 접근 시간 초과" }
            throw BusinessException(S3ErrorCode.TIMEOUT)
        } catch (e: Exception) {
            logger.error(e) { "버킷 접근 중 예상치 못한 오류 발생" }
            throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 파일 업로드 권한 확인
     */
    private suspend fun checkUploadPermission() {
        try {
            withTimeout(s3Properties.timeout) {
                val startTime = System.currentTimeMillis()

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucketName)
                    .key("health-check/$testFileName")
                    .contentType("text/plain")
                    .metadata(
                        mapOf(
                            "health-check" to "true",
                            "timestamp" to LocalDateTime.now().toString()
                        )
                    )
                    .build()

                val requestBody = RequestBody.fromString(testContent)
                s3Client.putObject(putObjectRequest, requestBody)

                val duration = System.currentTimeMillis() - startTime
                logger.info { "파일 업로드 권한 테스트 성공 - 소요 시간: ${duration}ms" }
            }
        } catch (e: S3Exception) {
            logger.error { "파일 업로드 실패 - 상태코드: ${e.statusCode()}, 메시지: ${e.awsErrorDetails()?.errorMessage() ?: e.message}" }
            val businessErrorCode = when (e.statusCode()) {
                403 -> S3ErrorCode.ACCESS_DENIED
                413 -> S3ErrorCode.FILE_UPLOAD_FAILED
                else -> S3ErrorCode.FILE_UPLOAD_FAILED
            }
            throw BusinessException(businessErrorCode)
        } catch (_: TimeoutCancellationException) {
            logger.error { "파일 업로드 시간 초과" }
            throw BusinessException(S3ErrorCode.TIMEOUT)
        } catch (e: Exception) {
            logger.error(e) { "파일 업로드 중 예상치 못한 오류 발생" }
            throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 파일 읽기 권한 확인
     */
    private suspend fun checkReadPermission() {
        try {
            withTimeout(s3Properties.timeout) {
                val startTime = System.currentTimeMillis()

                val getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.bucketName)
                    .key("health-check/$testFileName")
                    .build()

                s3Client.getObject(getObjectRequest).use { response ->
                    val content = response.readAllBytes().toString(Charsets.UTF_8)
                    val duration = System.currentTimeMillis() - startTime

                    if (content == testContent) {
                        logger.info { "파일 읽기 권한 테스트 성공 - 소요 시간: ${duration}ms" }
                    } else {
                        logger.error { "파일 내용 불일치 - 예상: $testContent, 실제: $content" }
                        throw BusinessException(S3ErrorCode.FILE_READ_FAILED)
                    }
                }
            }
        } catch (_: NoSuchKeyException) {
            logger.error { "테스트 파일을 찾을 수 없음 (업로드가 실패했을 수 있음)" }
            throw BusinessException(S3ErrorCode.FILE_NOT_FOUND)
        } catch (e: S3Exception) {
            logger.error { "파일 읽기 실패 - 상태코드: ${e.statusCode()}, 메시지: ${e.awsErrorDetails()?.errorMessage() ?: e.message}" }
            throw BusinessException(S3ErrorCode.FILE_READ_FAILED)
        } catch (_: TimeoutCancellationException) {
            logger.error { "파일 읽기 시간 초과" }
            throw BusinessException(S3ErrorCode.TIMEOUT)
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "파일 읽기 중 예상치 못한 오류 발생" }
            throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 파일 삭제 권한 확인
     */
    private suspend fun checkDeletePermission() {
        try {
            withTimeout(s3Properties.timeout) {
                val startTime = System.currentTimeMillis()

                val deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucketName)
                    .key("health-check/$testFileName")
                    .build()

                s3Client.deleteObject(deleteObjectRequest)

                val duration = System.currentTimeMillis() - startTime
                logger.info { "파일 삭제 권한 테스트 성공 - 소요 시간: ${duration}ms" }
            }
        } catch (e: S3Exception) {
            logger.error { "파일 삭제 실패 - 상태코드: ${e.statusCode()}, 메시지: ${e.awsErrorDetails()?.errorMessage() ?: e.message}" }
            throw BusinessException(S3ErrorCode.FILE_DELETE_FAILED)
        } catch (_: TimeoutCancellationException) {
            logger.error { "파일 삭제 시간 초과" }
            throw BusinessException(S3ErrorCode.TIMEOUT)
        } catch (e: Exception) {
            logger.error(e) { "파일 삭제 중 예상치 못한 오류 발생" }
            throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 버킷 설정 확인
     */
    private suspend fun checkBucketConfiguration() {
        try {
            withTimeout(s3Properties.timeout) {
                val startTime = System.currentTimeMillis()

                // 버킷 위치 확인
                val getBucketLocationRequest = GetBucketLocationRequest.builder()
                    .bucket(s3Properties.bucketName)
                    .build()

                val locationResponse = s3Client.getBucketLocation(getBucketLocationRequest)
                val bucketRegion = locationResponse.locationConstraint()?.toString() ?: "us-east-1"

                val duration = System.currentTimeMillis() - startTime

                if (bucketRegion == s3Properties.region ||
                    (bucketRegion == "us-east-1" && s3Properties.region == "us-east-1")
                ) {
                    logger.info { "버킷 설정 확인 성공 - 지역: $bucketRegion, 소요 시간: ${duration}ms" }
                } else {
                    logger.error { "버킷 지역 불일치 - 설정: ${s3Properties.region}, 실제: $bucketRegion" }
                    throw BusinessException(S3ErrorCode.CONNECTION_FAILED)
                }
            }
        } catch (e: S3Exception) {
            logger.error { "버킷 설정 확인 실패 - 상태코드: ${e.statusCode()}, 메시지: ${e.awsErrorDetails()?.errorMessage() ?: e.message}" }
            throw BusinessException(S3ErrorCode.CONNECTION_FAILED)
        } catch (_: TimeoutCancellationException) {
            logger.error { "버킷 설정 확인 시간 초과" }
            throw BusinessException(S3ErrorCode.TIMEOUT)
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "버킷 설정 확인 중 예상치 못한 오류 발생" }
            throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 간단한 연결 테스트 (빠른 확인용)
     */
    suspend fun quickHealthCheck(): Unit =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(5000) { // 5초 타임아웃
                    val headBucketRequest = HeadBucketRequest.builder()
                        .bucket(s3Properties.bucketName)
                        .build()

                    s3Client.headBucket(headBucketRequest)
                    logger.info { "Quick 헬스체크 성공" }
                }
            } catch (_: TimeoutCancellationException) {
                logger.error { "Quick 헬스체크 시간 초과" }
                throw BusinessException(S3ErrorCode.TIMEOUT)
            } catch (e: Exception) {
                logger.error(e) { "Quick 헬스체크 실패" }
                throw BusinessException(S3ErrorCode.CONNECTION_FAILED)
            }
        }

    /**
     * S3 설정 진단
     */
    suspend fun diagnoseConfiguration(): Unit =
        withContext(Dispatchers.IO) {
            try {
                logger.info { "S3 설정 진단 시작" }

                // 1. Credentials 형식 검증
                if (s3Properties.accessKey.isBlank() || s3Properties.accessKey == "dummy-access-key") {
                    logger.error { "CRITICAL - Access Key가 설정되지 않았거나 기본값입니다. 올바른 AWS Access Key를 설정하세요" }
                    throw BusinessException(S3ErrorCode.ACCESS_DENIED)
                }

                if (s3Properties.secretKey.isBlank() || s3Properties.secretKey == "dummy-secret-key") {
                    logger.error { "CRITICAL - Secret Key가 설정되지 않았거나 기본값입니다. 올바른 AWS Secret Key를 설정하세요" }
                    throw BusinessException(S3ErrorCode.ACCESS_DENIED)
                }

                // 2. 버킷명 검증
                if (s3Properties.bucketName.isBlank() || s3Properties.bucketName == "dummy-bucket") {
                    logger.error { "CRITICAL - 버킷명이 설정되지 않았거나 기본값입니다. 실제 S3 버킷명을 설정하세요" }
                    throw BusinessException(S3ErrorCode.BUCKET_NOT_FOUND)
                } else if (!isValidBucketName(s3Properties.bucketName)) {
                    logger.error { "HIGH - 버킷명 형식이 올바르지 않습니다. S3 버킷 명명 규칙을 따르세요 (소문자, 숫자, 하이픈만 사용)" }
                    throw BusinessException(S3ErrorCode.BUCKET_NOT_FOUND)
                }

                // 3. 지역 설정 검증
                if (!isValidRegion(s3Properties.region)) {
                    logger.error { "HIGH - 올바르지 않은 AWS 지역입니다. 유효한 AWS 지역 코드를 사용하세요 (예: ap-northeast-2)" }
                    throw BusinessException(S3ErrorCode.CONNECTION_FAILED)
                }

                // 4. 엔드포인트 설정 검증
                s3Properties.endpoint?.let { endpoint ->
                    if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                        logger.error { "MEDIUM - 엔드포인트 URL 형식이 올바르지 않습니다. http:// 또는 https://로 시작하는 완전한 URL을 사용하세요" }
                        throw BusinessException(S3ErrorCode.CONNECTION_FAILED)
                    }
                }

                // 5. 타임아웃 설정 검증
                if (s3Properties.timeout < 1000 || s3Properties.timeout > 300000) {
                    logger.warn { "LOW - 타임아웃 설정이 권장 범위를 벗어났습니다. 1초(1000ms)에서 5분(300000ms) 사이의 값을 사용하세요" }
                }

                // 설정 요약 로그
                logger.info {
                    "S3 설정 진단 성공 - " +
                            "버킷: ${s3Properties.bucketName}, " +
                            "지역: ${s3Properties.region}, " +
                            "엔드포인트: ${s3Properties.endpoint ?: "AWS Default"}, " +
                            "타임아웃: ${s3Properties.timeout}ms, " +
                            "Access Key: ${s3Properties.accessKey.take(4)}****"
                }

                logger.info { "S3 설정 진단 완료" }

            } catch (e: BusinessException) {
                throw e
            } catch (e: Exception) {
                logger.error(e) { "설정 진단 중 오류 발생" }
                throw BusinessException(S3ErrorCode.SYSTEM_ERROR)
            }
        }

    private fun isValidBucketName(bucketName: String): Boolean {
        return bucketName.matches(Regex("^[a-z0-9][a-z0-9\\-]*[a-z0-9]$")) &&
                bucketName.length in 3..63 &&
                !bucketName.contains("..") &&
                !bucketName.startsWith("xn--") &&
                !bucketName.endsWith("-s3alias")
    }

    private fun isValidRegion(region: String): Boolean {
        val validRegions = setOf(
            "us-east-1", "us-east-2", "us-west-1", "us-west-2",
            "ap-south-1", "ap-northeast-1", "ap-northeast-2", "ap-northeast-3",
            "ap-southeast-1", "ap-southeast-2", "ap-southeast-3",
            "ca-central-1", "eu-central-1", "eu-west-1", "eu-west-2", "eu-west-3",
            "eu-north-1", "eu-south-1", "sa-east-1", "af-south-1", "me-south-1"
        )
        return validRegions.contains(region)
    }
}