package org.ironworkschurch.analytics.job

import com.google.common.base.Stopwatch
import com.google.inject.Guice
import mu.KotlinLogging
import org.ironworkschurch.analytics.bo.BaseManager
import org.ironworkschurch.analytics.bo.DeltaManager
import org.ironworkschurch.analytics.bo.MetadataManager
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.AppModule
import org.ironworkschurch.analytics.config.ElasticSearchModule
import org.ironworkschurch.analytics.config.EtlModule
import org.ironworkschurch.analytics.dao.PersonDao
import org.ironworkschurch.analytics.dao.TransactionDao
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingTransaction
import org.ironworkschurch.analytics.to.PersonSearchEntry
import javax.inject.Inject
import org.ironworkschurch.analytics.to.flatten
import java.time.ZoneId


class UpdateTransactions @Inject constructor(
  private val simpleChurchManager: SimpleChurchManager,
  private val deltaManager: DeltaManager,
  private val baseManager: BaseManager,
  private val personDao: PersonDao,
  private val transactionDao: TransactionDao,
  private val metadataManager: MetadataManager
) {
  companion object {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule(), AppModule(), ElasticSearchModule())
        .getInstance(UpdateTransactions::class.java).run()
    }
  }

  fun run() {
    logger.info { "Updating transaction data and rollups" }
    val stopwatch = Stopwatch.createStarted()
    getNewTransactions()
    rollupTransactions()
    logger.info { "Updated transaction data and rollups in $stopwatch" }
  }

  private fun rollupTransactions() {
    logger.info { "Rolling up transactions" }
    val stopwatch = Stopwatch.createStarted()
    transactionDao.rollupTransactions()
    logger.info { "Rolled up transactions in $stopwatch" }
  }

  private fun getNewTransactions() {
    logger.info { "Loading new transactions from SimpleChurch API to MySQL database" }
    val stopwatch = Stopwatch.createStarted()

    val transactionData = fetchData()
    baseManager.loadData(transactionData, metadataManager.getTable("TRANSACTION_BASE"))

    logger.info { "Completed loading new transactions from SimpleChurch API to MySQL database in $stopwatch" }
  }

  private fun fetchData(): List<FlatGivingTransaction> {
    logger.debug { "Retrieving user data" }
    val stopwatch = Stopwatch.createStarted()

    val personIDs = personDao.getAllPersonIDs()

    logger.debug { "Retrieving transactions for ${personIDs.size} individuals" }

    val transactions = simpleChurchManager.getTransactions(personIDs)
    val flatTransactions = transactions.map { it.flatten() }

    logger.debug { "Found ${flatTransactions.size} transactions" }

    val maxDate = transactionDao.getMaxDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

    logger.debug { "Filtering transactions after $maxDate" }

    val newTransactions = when (maxDate) {
      null -> flatTransactions
      else -> flatTransactions.filterNot { it.date.isBefore(maxDate) }
    }

    logger.debug { "Retrieved ${newTransactions.size} new transactions in $stopwatch" }
    return newTransactions
  }
}
