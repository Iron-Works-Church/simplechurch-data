package org.ironworkschurch.analytics.dao

import mu.KLogging
import org.springframework.jdbc.core.JdbcTemplate
import javax.inject.Inject

class PersonDao @Inject constructor(private val jdbcTemplate: JdbcTemplate) {
  companion object : KLogging()

  fun getAllPersonIDs(): List<Int> {
    val sql = "SELECT uid FROM PERSON_BASE"
    return jdbcTemplate.queryForList(sql, Int::class.java)
  }
}