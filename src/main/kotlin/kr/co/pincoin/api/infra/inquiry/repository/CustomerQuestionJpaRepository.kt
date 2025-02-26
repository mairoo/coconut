package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.infra.inquiry.entity.CustomerQuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerQuestionJpaRepository : JpaRepository<CustomerQuestionEntity, Long>