package org.ironworkschurch.analytics.bo

import org.ironworkschurch.analytics.dao.MetadataDao
import org.ironworkschurch.analytics.to.Table
import javax.inject.Inject

class MetadataManager @Inject constructor (private val metadataDao: MetadataDao) {
  fun getTables(): List<Table> {
    val tables = metadataDao.getTableMetadata()
    val constraintsByTable = metadataDao.getConstraints().groupBy { it.tableName }
    return tables.map {  it.copy(constraints = constraintsByTable[it.tableName] ?: listOf()) }
  }

  fun getTable(tableName: String): Table {
    val tableMetadata = metadataDao.getTableMetadata(tableName)
    val constraints = metadataDao.getConstraints(tableName)
    return tableMetadata.copy(constraints = constraints)
  }

  fun tableExists(tableName: String): Boolean {
    return metadataDao.getTableMetadata(tableName).columns.isNotEmpty()
  }
}