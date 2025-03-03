package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.infra.order.converter.PaymentBankAccountConverter
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.ZonedDateTime

@Repository
class OrderPaymentJdbcRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val bankAccountConverter = PaymentBankAccountConverter()

    fun batchInsert(orderPayments: List<OrderPayment>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO shop_orderpayment (
                created,
                modified,
                is_removed,
                order_id,
                account,
                amount,
                balance,
                received
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderPayment = orderPayments[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderPayment.isRemoved)
                        setLong(idx++, orderPayment.orderId)
                        setInt(
                            idx++,
                            bankAccountConverter.convertToDatabaseColumn(orderPayment.account)
                        )
                        setBigDecimal(idx++, orderPayment.amount)
                        setBigDecimal(idx++, orderPayment.balance)
                        setTimestamp(
                            idx,
                            Timestamp.valueOf(orderPayment.received.toLocalDateTime())
                        )
                    }
                }

                override fun getBatchSize(): Int = orderPayments.size
            })
    }

    fun batchUpdate(orderPayments: List<OrderPayment>) {
        val now = ZonedDateTime.now()

        jdbcTemplate.batchUpdate(
            """
            UPDATE shop_orderpayment 
            SET modified = ?,
                is_removed = ?,
                account = ?,
                amount = ?,
                balance = ?,
                received = ?
            WHERE id = ?
            """.trimIndent(), object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    val orderPayment = orderPayments[i]
                    with(ps) {
                        var idx = 1
                        setTimestamp(idx++, Timestamp.valueOf(now.toLocalDateTime()))
                        setBoolean(idx++, orderPayment.isRemoved)
                        setInt(
                            idx++,
                            bankAccountConverter.convertToDatabaseColumn(orderPayment.account)
                        )
                        setBigDecimal(idx++, orderPayment.amount)
                        setBigDecimal(idx++, orderPayment.balance)
                        setTimestamp(
                            idx++,
                            Timestamp.valueOf(orderPayment.received.toLocalDateTime())
                        )
                        setLong(idx, orderPayment.id!!)
                    }
                }

                override fun getBatchSize(): Int = orderPayments.size
            })
    }
}