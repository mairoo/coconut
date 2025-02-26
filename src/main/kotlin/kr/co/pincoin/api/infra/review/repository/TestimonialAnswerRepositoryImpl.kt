package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.model.TestimonialAnswer
import kr.co.pincoin.api.domain.review.repository.TestimonialAnswerRepository
import kr.co.pincoin.api.infra.review.mapper.toEntity
import kr.co.pincoin.api.infra.review.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerRepositoryImpl(
    private val jpaRepository: TestimonialAnswerJpaRepository,
    private val queryRepository: TestimonialAnswerQueryRepository,
) : TestimonialAnswerRepository {
    override fun save(
        testimonialAnswer: TestimonialAnswer,
    ): TestimonialAnswer =
        testimonialAnswer.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이용후기답변 저장 실패")
}