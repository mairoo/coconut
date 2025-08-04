package kr.pincoin.api.app.s3.admin.service

import kotlinx.coroutines.runBlocking
import kr.pincoin.api.external.s3.api.response.S3FileInfoResponse
import kr.pincoin.api.external.s3.service.S3FileService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminS3FileService(
    private val s3FileService: S3FileService,
) {
    fun getFileInfo(fileKey: String): S3FileInfoResponse =
        runBlocking {
            s3FileService.getFileInfo(fileKey)
        }
}