package org.ironworkschurch.analytics.dao

import org.ironworkschurch.analytics.to.Column
import org.ironworkschurch.analytics.to.Constraint
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import javax.inject.Inject

class MetadataDao @Inject constructor(private val jdbcTemplate: JdbcTemplate) {

  fun getConstraints(): List<Constraint> {
    val sql = """SELECT
                      |  TC.TABLE_NAME,
                      |  TC.CONSTRAINT_NAME,
                      |  TC.CONSTRAINT_TYPE,
                      |  KCU.COLUMN_NAME
                      |FROM information_schema.TABLE_CONSTRAINTS TC
                      |  INNER JOIN information_schema.KEY_COLUMN_USAGE KCU
                      |     ON TC.TABLE_NAME = KCU.TABLE_NAME
                      |    AND TC.TABLE_SCHEMA = KCU.TABLE_SCHEMA
                      |    AND TC.CONSTRAINT_NAME = KCU.CONSTRAINT_NAME
                      |WHERE TC.TABLE_SCHEMA = '$tableSchemaName'
                      |ORDER BY TABLE_NAME, TC.CONSTRAINT_NAME, KCU.ORDINAL_POSITION""".trimMargin()
    val columns = jdbcTemplate.query(sql) { rs, _ ->
      ConstraintColumn (
        tableName = rs.getString("TABLE_NAME"),
        columnName = rs.getString("COLUMN_NAME"),
        constraintName = rs.getString("CONSTRAINT_NAME"),
        constraintType = rs.getString("CONSTRAINT_TYPE")
      )
    }

    return columns
      .groupBy { Constraint(it.tableName, it.constraintName, it.constraintType, listOf()) }
      .map { it.key.copy(columns = it.value.map { it.columnName }) }
  }


  fun getConstraints(tableName: String): List<Constraint> {

    val sql = """SELECT
                      |  TC.TABLE_NAME,
                      |  TC.CONSTRAINT_NAME,
                      |  TC.CONSTRAINT_TYPE,
                      |  KCU.COLUMN_NAME
                      |FROM information_schema.TABLE_CONSTRAINTS TC
                      |  INNER JOIN information_schema.KEY_COLUMN_USAGE KCU
                      |     ON TC.TABLE_NAME = KCU.TABLE_NAME
                      |    AND TC.TABLE_SCHEMA = KCU.TABLE_SCHEMA
                      |    AND TC.CONSTRAINT_NAME = KCU.CONSTRAINT_NAME
                      |WHERE TC.TABLE_SCHEMA = '$tableSchemaName'
                      |  AND TC.TABLE_NAME = ?
                      |ORDER BY TABLE_NAME, TC.CONSTRAINT_NAME, KCU.ORDINAL_POSITION""".trimMargin()
    val columns = jdbcTemplate.query(sql, arrayOf(tableName)) { rs, _ ->
      ConstraintColumn (
        tableName = rs.getString("TABLE_NAME"),
        columnName = rs.getString("COLUMN_NAME"),
        constraintName = rs.getString("CONSTRAINT_NAME"),
        constraintType = rs.getString("CONSTRAINT_TYPE")
      )
    }

    return columns
      .groupBy { Constraint(it.tableName, it.constraintName, it.constraintType, listOf()) }
      .map { it.key.copy(columns = it.value.map { it.columnName }) }
  }

  private data class ConstraintColumn(val tableName: String,
              val constraintName: String,
              val constraintType: String,
              val columnName: String)

  fun getTableMetadata(): List<Table> {
    val sql = """SELECT TABLE_NAME,
      |  COLUMN_NAME,
      |  DATA_TYPE,
      |  IS_NULLABLE
      |FROM information_schema.COLUMNS
      |WHERE TABLE_SCHEMA = '$tableSchemaName'""".trimMargin()
    val columns = jdbcTemplate.query(sql) { rs, _ ->
      Column (
        tableName = rs.getString("TABLE_NAME"),
        columnName = rs.getString("COLUMN_NAME"),
        dataType = rs.getString("DATA_TYPE"),
        isNullable = rs.getString("IS_NULLABLE") == "YES"
      )
    }

    return columns
      .groupBy { it.tableName }
      .map { Table(it.key, it.value, listOf()) }
  }

  fun getTableMetadata(tableName: String): Table {
    val sql = """SELECT TABLE_NAME,
      |  COLUMN_NAME,
      |  DATA_TYPE,
      |  IS_NULLABLE
      |FROM information_schema.COLUMNS
      |WHERE TABLE_SCHEMA = '$tableSchemaName'
      |  AND TABLE_NAME = ?""".trimMargin()
    val columns = jdbcTemplate.query(sql, arrayOf(tableName)) { rs, _ ->
      Column (
        tableName = rs.getString("TABLE_NAME"),
        columnName = rs.getString("COLUMN_NAME"),
        dataType = rs.getString("DATA_TYPE"),
        isNullable = rs.getString("IS_NULLABLE") == "YES"
      )
    }

    return Table(tableName, columns, listOf())
  }

  private val tableSchemaName = """iwc_members"""
}