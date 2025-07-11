package kr.pincoin.api.domain.user.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.model.LoginLog
import kr.pincoin.api.domain.user.repository.LoginLogRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class LoginEventHandler(
    private val loginLogRepository: LoginLogRepository
) {
    private val log = KotlinLogging.logger {}

    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMPLETION, // 트랜잭션 완료 후 처리(커밋 또는 롤백 상태 무관)
        fallbackExecution = true,
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleLoginEvent(event: LoginEvent) {
        try {
            val loginLog = LoginLog.of(
                ipAddress = event.ipAddress,
                userId = event.userId,
            )
            loginLogRepository.save(loginLog)
        } catch (e: Exception) {
            log.error(e) { "로그인 이벤트 처리 중 오류 발생" }
        }
    }
}