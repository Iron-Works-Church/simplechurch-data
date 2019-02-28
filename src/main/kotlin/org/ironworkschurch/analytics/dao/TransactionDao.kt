package org.ironworkschurch.analytics.dao

import com.google.common.io.Resources
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingTransaction
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class TransactionDao @Inject constructor(private val jdbcTemplate: JdbcTemplate) {
  fun rollupTransactions() {
    val ddl = Resources.toString(Resources.getResource("TRANSACTION_ROLLUP.sql"), Charsets.UTF_8)
    jdbcTemplate.execute(ddl)
    val dml = Resources.toString(Resources.getResource("buildTransactionRollup.sql"), Charsets.UTF_8)
    jdbcTemplate.update(dml)
  }


  fun getRollups(lastRollupDate: LocalDate?): List<TransactionRollup> {
    var sql = """SELECT
                |  uid,
                |  date,
                |  amount_last_7,
                |  amount_last_30,
                |  amount_last_90,
                |  amount_last_180,
                |  amount_last_365,
                |  is_member
                |FROM TRANSACTION_ROLLUP""".trimMargin()
    if (lastRollupDate != null) {
      sql += "\n WHERE date > ?"
    }

    val rowMapper: (ResultSet, Int) -> TransactionRollup = { rs, _ ->
      TransactionRollup(
        uid = rs.getInt("uid"),
        date = rs.getDate("date").toLocalDate().format(DateTimeFormatter.ISO_DATE),
        amountPast7 = rs.getDouble("amount_last_7"),
        amountPast30 = rs.getDouble("amount_last_30"),
        amountPast90 = rs.getDouble("amount_last_90"),
        amountPast180 = rs.getDouble("amount_last_180"),
        amountPast365 = rs.getDouble("amount_last_365"),
        isMember = rs.getBoolean("is_member")
      )
    }

    val params = when (lastRollupDate) {
      null -> arrayOf()
      else -> arrayOf(lastRollupDate)
    }

    return jdbcTemplate.query(sql.trimMargin(), params, rowMapper)
  }

  fun save(transactionData: List<FlatGivingTransaction>) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  fun getMaxDate(): Date? {
    return jdbcTemplate.queryForObject("SELECT MAX(date) FROM TRANSACTION_BASE", Date::class.java)
  }

  data class TransactionRollup (
    val uid: Int,
    val date: String,
    val amountPast7: Double,
    val amountPast30: Double,
    val amountPast90: Double,
    val amountPast180: Double,
    val amountPast365: Double,
    val isMember: Boolean
  )
}