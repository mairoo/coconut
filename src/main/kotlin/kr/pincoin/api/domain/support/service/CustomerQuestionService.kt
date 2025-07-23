package kr.pincoin.api.domain.support.service

import kr.pincoin.api.domain.support.repository.CustomerQuestionAnswerRepository
import kr.pincoin.api.domain.support.repository.CustomerQuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CustomerQuestionService(
    private val customerQuestionRepository: CustomerQuestionRepository,
    private val customerQuestionAnswerRepository: CustomerQuestionAnswerRepository,
) {
}