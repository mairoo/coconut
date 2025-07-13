package kr.pincoin.api.domain.support.service

import kr.pincoin.api.domain.support.repository.TestimonialAnswerRepository
import kr.pincoin.api.domain.support.repository.TestimonialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TestimonialService(
    private val testimonialRepository: TestimonialRepository,
    private val testimonialAnswerRepository: TestimonialAnswerRepository,
) {
}