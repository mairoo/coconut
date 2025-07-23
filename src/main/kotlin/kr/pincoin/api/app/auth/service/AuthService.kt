package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakAdminService
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTokenService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userResourceCoordinator: UserResourceCoordinator,
    private val keycloakAdminService: KeycloakAdminService,
    private val keycloakTokenService: KeycloakTokenService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 사용자 회원가입
     */
    fun createUser(request: SignUpRequest): User {
        return runBlocking {
            try {
                val adminToken = getAdminToken()
                userResourceCoordinator.createUserWithKeycloak(request, adminToken)
            } catch (e: BusinessException) {
                logger.error { "회원가입 비즈니스 오류: email=${request.email}, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "회원가입 시스템 오류: email=${request.email}, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * Admin 토큰 획득
     */
    private suspend fun getAdminToken(): String {
        return when (val result = keycloakAdminService.getAdminToken()) {
            is KeycloakResponse.Success -> {
                result.data.accessToken
            }

            is KeycloakResponse.Error -> {
                throw BusinessException(KeycloakErrorCode.ADMIN_TOKEN_FAILED)
            }
        }
    }
}