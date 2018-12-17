package org.ironworkschurch.analytics.bo

import com.google.common.io.Resources
import com.google.common.util.concurrent.ListeningExecutorService
import mu.KLogging
import org.ironworkschurch.analytics.dao.BaseDao
import org.ironworkschurch.analytics.to.Table
import org.springframework.jdbc.core.JdbcTemplate
import java.util.concurrent.Executors
import javax.inject.Inject

class BaseManager @Inject constructor(private val baseDao: BaseDao,
                                      private val jdbcTemplate: JdbcTemplate,
                                      private val metadataManager: MetadataManager,
                                      private val executorService: ListeningExecutorService) {
  companion object : KLogging()

  fun loadData(data: List<Any>, table: Table) {
    baseDao.loadData(data, table)
  }

  fun createMissingTables() {
    listOf(
      "FAMILY_MEMBER_BASE",
      "GROUP_BASE",
      "PERSON_BASE",
      "PERSON_GROUP_BASE",
      "TRANSACTION_BASE",
      "GIVING_CATEGORY_BASE"
    ).map { "$it.sql" }
      .map { Resources.getResource(it) }
      .map { Resources.toString(it, Charsets.UTF_8) }
      .map { jdbcTemplate.execute(it) }
  }

  fun loadBaseTables(pairs: List<Pair<String, List<Any>>>) {
    val tables = metadataManager.getTables().associateBy { it.tableName }

    pairs.map { (tableName, data) ->
      val table = tables[tableName]!!
      executorService.submit { loadData(data, table) }
    }.map { it.get() }
  }

}
