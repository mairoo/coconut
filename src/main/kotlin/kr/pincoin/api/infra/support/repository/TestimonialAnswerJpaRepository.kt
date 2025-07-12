package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.TestimonialAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestimonialAnswerJpaRepository : JpaRepository<TestimonialAnswerEntity, Long> {
}