package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.OrderProduct
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class OrderProductJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun batchInsert(orderProducts: List<OrderProduct>) {
        if (orderProducts.isEmpty()) return

        val now = LocalDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO shop_orderproduct(
                created,
                modified,
                is_removed,
                name,
                subtitle,
                code,
                list_price,
                selling_price,
                quantity,
                order_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderProduct = orderProducts[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now))
                        setTimestamp(idx++, Timestamp.valueOf(now))
                        setBoolean(idx++, orderProduct.isRemoved)
                        setString(idx++, orderProduct.name)
                        setString(idx++, orderProduct.subtitle)
                        setString(idx++, orderProduct.code)
                        setBigDecimal(idx++, orderProduct.listPrice)
                        setBigDecimal(idx++, orderProduct.sellingPrice)
                        setInt(idx++, orderProduct.quantity)
                        setLong(idx, orderProduct.orderId)
                    }
                }

                override fun getBatchSize(): Int = orderProducts.size
            }
        )
    }
}