package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.Voucher
import kr.co.pincoin.api.infra.inventory.converter.VoucherStatusConverter
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class VoucherJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val statusConverter = VoucherStatusConverter()

    fun batchInsert(vouchers: List<Voucher>) {
        val now = LocalDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO shop_voucher (
                created,
                modified,
                is_removed,
                code,
                remarks,
                status,
                product_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val voucher = vouchers[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now))
                        setTimestamp(idx++, Timestamp.valueOf(now))
                        setBoolean(idx++, voucher.isRemoved)
                        setString(idx++, voucher.code)
                        setString(idx++, voucher.remarks)
                        setInt(idx++, statusConverter.convertToDatabaseColumn(voucher.status))
                        setLong(idx, voucher.productId)
                    }
                }

                override fun getBatchSize(): Int = vouchers.size
            })
    }

    fun batchUpdate(vouchers: List<Voucher>) {
        val now = LocalDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            UPDATE shop_voucher 
            SET modified = ?,
                is_removed = ?,
                code = ?,
                remarks = ?,
                status = ?
            WHERE id = ?
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val voucher = vouchers[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now))
                        setBoolean(idx++, voucher.isRemoved)
                        setString(idx++, voucher.code)
                        setString(idx++, voucher.remarks)
                        setInt(idx++, statusConverter.convertToDatabaseColumn(voucher.status))
                        setLong(idx, voucher.id!!)
                    }
                }

                override fun getBatchSize(): Int = vouchers.size
            })
    }
}