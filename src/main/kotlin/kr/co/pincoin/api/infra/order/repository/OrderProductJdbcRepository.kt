package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProduct
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.ZonedDateTime

@Repository
class OrderProductJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun batchInsert(orderProducts: List<OrderProduct>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO shop_orderproduct (
                created,
                modified,
                is_removed,
                order_id,
                name,
                subtitle,
                code,
                list_price,
                selling_price,
                quantity
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderProduct = orderProducts[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderProduct.isRemoved)
                        setLong(idx++, orderProduct.orderId)
                        setString(idx++, orderProduct.name)
                        setString(idx++, orderProduct.subtitle)
                        setString(idx++, orderProduct.code)
                        setBigDecimal(idx++, orderProduct.listPrice)
                        setBigDecimal(idx++, orderProduct.sellingPrice)
                        setInt(idx, orderProduct.quantity)
                    }
                }

                override fun getBatchSize(): Int = orderProducts.size
            })
    }

    fun batchUpdate(orderProducts: List<OrderProduct>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            UPDATE shop_orderproduct 
            SET modified = ?,
                is_removed = ?,
                name = ?,
                subtitle = ?,
                code = ?,
                list_price = ?,
                selling_price = ?,
                quantity = ?
            WHERE id = ?
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderProduct = orderProducts[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderProduct.isRemoved)
                        setString(idx++, orderProduct.name)
                        setString(idx++, orderProduct.subtitle)
                        setString(idx++, orderProduct.code)
                        setBigDecimal(idx++, orderProduct.listPrice)
                        setBigDecimal(idx++, orderProduct.sellingPrice)
                        setInt(idx++, orderProduct.quantity)
                        setLong(idx, orderProduct.id!!)
                    }
                }

                override fun getBatchSize(): Int = orderProducts.size
            })
    }
}