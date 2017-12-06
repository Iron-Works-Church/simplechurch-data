package org.ironworkschurch.analytics.dao

import mu.KLogging
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import javax.inject.Inject

class DeltaDao @Inject constructor (private val jdbcTemplate: JdbcTemplate) {
  companion object : KLogging()

  fun insertNewRows(baseTable: Table, deltaTable: Table) {
    val deltaTableName = deltaTable.tableName
    val changeDetectionClause = baseTable.columns.map {
      when {
        it.isNullable -> "COALESCE(D.${it.columnName}, '\$-\$-\$-\$-\$') = COALESCE(B.${it.columnName}, '\$-\$-\$-\$-\$')"
        else -> "D.${it.columnName} = B.${it.columnName}"
      }
    }
      .joinToString(separator = "\n    AND ")

    val insertSql = """INSERT INTO $deltaTableName ( ${baseTable.columns.joinToString(separator = ",\n  ") { it.columnName }}
                            |) SELECT ${baseTable.columns.joinToString(separator = ",\n  ") { it.columnName }}
                            |FROM ${baseTable.tableName} B
                            |WHERE NOT EXISTS (
                            |  SELECT ${baseTable.columns.joinToString(separator = ",\n  ") { it.columnName }}
                            |  FROM $deltaTableName D
                            |  WHERE $changeDetectionClause
                            |)""".trimMargin()

    logger.trace { "Executing $insertSql" }

    val rowsAffected = jdbcTemplate.update(insertSql)
    logger.debug { "Inserted $rowsAffected new rows in $deltaTableName" }
  }

  fun updateExpiredRows(deltaTableName: String, businessKeys: List<String>) {

    val updateSql = """UPDATE $deltaTableName DELTA
                            |INNER JOIN (
                            |    SELECT MAX(ROW_EFF_DTS) AS ROW_EFF_DTS, ${businessKeys.joinToString()}
                            |    FROM $deltaTableName
                            |      GROUP BY ${businessKeys.joinToString()}
                            |    ) CURRENT ON ${businessKeys.map { "CURRENT.$it = DELTA.$it" }.joinToString("\n         AND ")}
                            |SET CURR_ROW_FL = 'N',
                            |    ROW_EXP_DTS = CURRENT.ROW_EFF_DTS,
                            |    LAST_UPDATE_DTS = CURRENT_TIMESTAMP
                            |WHERE
                            |  DELTA.CURR_ROW_FL = 'Y'
                            |  AND DELTA.ROW_EFF_DTS <> CURRENT.ROW_EFF_DTS""".trimMargin()
    logger.trace { "Executing $updateSql" }

    val rowsAffected = jdbcTemplate.update(updateSql)
    logger.debug { "Expired $rowsAffected rows in $deltaTableName" }
  }
}