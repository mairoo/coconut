package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.TestimonialAnswer
import kr.pincoin.api.domain.support.repository.TestimonialAnswerRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
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
            ?: throw IllegalArgumentException("고객리뷰답변 저장 실패")

    override fun findById(id: Long): TestimonialAnswer? =
        queryRepository.findById(id)?.toModel()
}