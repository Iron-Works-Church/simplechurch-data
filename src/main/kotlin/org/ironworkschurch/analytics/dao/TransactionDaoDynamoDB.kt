package org.ironworkschurch.analytics.dao

import com.google.common.io.Resources
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


/*
class TransactionDaoDynamoDB @Inject constructor(private val dynamoDbClient: DynamoDbClient) {
  fun rollupTransactions() {
    val ddl = Resources.toString(Resources.getResource("TRANSACTION_ROLLUP.sql"), Charsets.UTF_8)
    jdbcTemplate.execute(ddl)
    val dml = Resources.toString(Resources.getResource("buildTransactionRollup.sql"), Charsets.UTF_8)
    jdbcTemplate.update(dml)
  }


  fun getRollups(lastRollupDate: LocalDate?): List<TransactionRollup> {
    val builder = QueryRequest.builder()
      .tableName("TransactionRollup")
    lastRollupDate?.let {
      builder
        .keyConditionExpression("date > ${lastRollupDate?.format(DateTimeFormatter.ISO_DATE)}")
    }

    dynamoDbClient.query(builder.build())

    var sql = """SELECT
                |  uid,
                |  date,
                |  amount_last_7,
                |  amount_last_30,
                |  amount_last_90,
                |  amount_last_180,
                |
                |
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
    val request = GetItemRequest.builder()
      .tableName("Transaction")
      .projectionExpression("MAX(date) as date")
      .build()
    return dynamoDbClient.getItem(request)
      .item()
      ?.get("date")
      ?.s()
      ?.parseDate()
      ?.toDate()
  }

  fun LocalDate.toDate(): Date = java.sql.Date.valueOf(this)

  private fun String.parseDate() = LocalDate.parse(this)

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
}*/
