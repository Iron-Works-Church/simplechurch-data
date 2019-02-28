package org.ironworkschurch.analytics.bo

import com.google.common.io.Resources
import com.google.common.util.concurrent.ListeningExecutorService
import org.ironworkschurch.analytics.dao.DeltaDao
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import java.util.concurrent.Executors
import javax.inject.Inject

class DeltaManager @Inject constructor (private val metadataManager: MetadataManager,
                                        private val deltaDao: DeltaDao,
                                        private val jdbcTemplate: JdbcTemplate,
                                        private val executorService: ListeningExecutorService) {

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
      "PERSON_DELTA",
      "PERSON_GROUP_DELTA",
      "TRANSACTION_DELTA"
    )
      .map { "$it.sql" }
      .map { Resources.getResource(it) }
      .map { Resources.toString(it, Charsets.UTF_8) }
      .map { jdbcTemplate.execute(it) }

    val tables = metadataManager.getTables().associateBy { it.tableName }

    val pairs = listOf(
      "PERSON_BASE" to "PERSON_DELTA",
      "GROUP_BASE" to "GROUP_DELTA",
      "FAMILY_MEMBER_BASE" to "FAMILY_MEMBER_DELTA",
      "PERSON_GROUP_BASE" to "PERSON_GROUP_DELTA",
      "TRANSACTION_BASE" to "TRANSACTION_DELTA"
    )

    pairs.map {
      tables[it.first]!! to tables[it.second]!!
    }
      .map { executorService.submit { loadDeltaTable(it.first, it.second) } }
      .map { it.get() }
  }
}