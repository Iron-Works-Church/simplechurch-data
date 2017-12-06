package org.ironworkschurch.analytics.bo

import com.google.common.io.Resources
import org.ironworkschurch.analytics.dao.DeltaDao
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import javax.inject.Inject

class DeltaManager @Inject constructor (private val metadataManager: MetadataManager,
                                        private val deltaDao: DeltaDao,
                                        private val jdbcTemplate: JdbcTemplate) {
  private fun insertNewRows(baseTable: Table, deltaTable: Table) {
    deltaDao.insertNewRows(baseTable, deltaTable)

  }

  private fun updateDelta(deltaTable: Table) {
    val businessKeys = deltaTable.businessKeys
      ?: throw RuntimeException("No business keys found for $deltaTable")

    deltaDao.updateExpiredRows(deltaTable.tableName, businessKeys)
  }

  private fun loadDeltaTable(baseTable: Table, deltaTable: Table) {
    insertNewRows(baseTable, deltaTable)
    updateDelta(deltaTable)
  }

  fun loadDeltaTables() {
    listOf(
      "FAMILY_MEMBER_DELTA",
      "GROUP_DELTA",
      "PERSON_HEADER_DELTA",
      "PERSON_DETAIL_DELTA",
      "PERSON_GROUP_DELTA"
    )
      .map { "$it.sql" }
      .map { Resources.getResource(it) }
      .map { Resources.toString(it, Charsets.UTF_8) }
      .map { jdbcTemplate.execute(it) }

    val tables = metadataManager.getTables().associateBy { it.tableName }
    loadDeltaTable(tables["FAMILY_MEMBER_BASE"]!!, tables["FAMILY_MEMBER_DELTA"]!!)
    loadDeltaTable(tables["GROUP_BASE"]!!, tables["GROUP_DELTA"]!!)
    loadDeltaTable(tables["PERSON_HEADER_BASE"]!!, tables["PERSON_HEADER_DELTA"]!!)
    loadDeltaTable(tables["PERSON_DETAIL_BASE"]!!, tables["PERSON_DETAIL_DELTA"]!!)
    loadDeltaTable(tables["PERSON_GROUP_BASE"]!!, tables["PERSON_GROUP_DELTA"]!!)
  }
}