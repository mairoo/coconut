package kr.pincoin.api.app.s3.admin.service

import kr.pincoin.api.external.s3.api.response.S3FileInfoResponse
import kr.pincoin.api.external.s3.service.S3FileService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminS3FileService(
    private val s3FileService: S3FileService,
) {
    suspend fun getFileInfo(
        fileKey: String,
    ): S3FileInfoResponse =
        s3FileService.getFileInfo(fileKey)
}