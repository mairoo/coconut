package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.infra.review.entity.TestimonialAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestimonialAnswerJpaRepository : JpaRepository<TestimonialAnswerEntity, Long>