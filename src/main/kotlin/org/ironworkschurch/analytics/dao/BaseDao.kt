package org.ironworkschurch.analytics.dao

import mu.KLogging
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import javax.inject.Inject
import kotlin.reflect.full.declaredMemberProperties

class BaseDao @Inject constructor(private val jdbcTemplate: JdbcTemplate) {
  companion object : KLogging()

  fun loadData(data: List<Any>, table: Table): Int {
    jdbcTemplate.update("TRUNCATE TABLE ${table.tableName}")
    val props = data.first()::class
      .declaredMemberProperties
      .filterNot { it.name == "array" }

    val sql = """INSERT INTO ${table.tableName} (
      |  ${props.joinToString(",\n  ") { it.name }}
      |) VALUES (
      |  ${table.columns.joinToString(",\n  ") { "?" }}
      |)""".trimMargin()


    val paramArrayList = data.map { record -> props.map { it.call(record) }.toTypedArray() }
    val rowsAffected = jdbcTemplate.batchUpdate(sql, paramArrayList).sum()

    logger.debug { "Inserted $rowsAffected new rows in ${table.tableName}" }
    return rowsAffected
  }
}