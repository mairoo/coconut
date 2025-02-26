package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.infra.inquiry.entity.CustomerQuestionAnswerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerQuestionAnswerJpaRepository : JpaRepository<CustomerQuestionAnswerEntity, Long>