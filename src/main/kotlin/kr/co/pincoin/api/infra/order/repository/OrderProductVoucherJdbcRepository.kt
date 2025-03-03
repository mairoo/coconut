package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.ZonedDateTime

@Repository
class OrderProductVoucherJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun batchInsert(orderProductVouchers: List<OrderProductVoucher>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO shop_orderproductvoucher (
                created,
                modified,
                is_removed,
                order_product_id,
                voucher_id,
                code,
                revoked,
                remarks
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderProductVoucher = orderProductVouchers[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderProductVoucher.isRemoved)
                        setLong(idx++, orderProductVoucher.orderProductId)
                        if (orderProductVoucher.voucherId != null) {
                            setLong(idx++, orderProductVoucher.voucherId)
                        } else {
                            setNull(idx++, java.sql.Types.BIGINT)
                        }
                        setString(idx++, orderProductVoucher.code)
                        setBoolean(idx++, orderProductVoucher.revoked)
                        setString(idx, orderProductVoucher.remarks)
                    }
                }

                override fun getBatchSize(): Int = orderProductVouchers.size
            })
    }

    fun batchUpdate(orderProductVouchers: List<OrderProductVoucher>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            UPDATE shop_orderproductvoucher 
            SET modified = ?,
                is_removed = ?,
                voucher_id = ?,
                code = ?,
                revoked = ?,
                remarks = ?
            WHERE id = ?
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderProductVoucher = orderProductVouchers[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderProductVoucher.isRemoved)
                        if (orderProductVoucher.voucherId != null) {
                            setLong(idx++, orderProductVoucher.voucherId)
                        } else {
                            setNull(idx++, java.sql.Types.BIGINT)
                        }
                        setString(idx++, orderProductVoucher.code)
                        setBoolean(idx++, orderProductVoucher.revoked)
                        setString(idx++, orderProductVoucher.remarks)
                        setLong(idx, orderProductVoucher.id!!)
                    }
                }

                override fun getBatchSize(): Int = orderProductVouchers.size
            })
    }
}