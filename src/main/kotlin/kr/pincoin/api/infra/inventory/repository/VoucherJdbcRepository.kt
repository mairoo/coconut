package kr.pincoin.api.infra.inventory.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class VoucherJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
}