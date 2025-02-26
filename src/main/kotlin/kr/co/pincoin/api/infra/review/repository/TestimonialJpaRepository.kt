package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.infra.review.entity.TestimonialEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestimonialJpaRepository : JpaRepository<TestimonialEntity, Long>