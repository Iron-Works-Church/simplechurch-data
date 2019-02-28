package org.ironworkschurch.analytics.job

import com.google.common.base.Stopwatch
import com.google.inject.Guice
import mu.KotlinLogging
import org.ironworkschurch.analytics.bo.BaseManager
import org.ironworkschurch.analytics.bo.DeltaManager
import org.ironworkschurch.analytics.bo.ElasticManager
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.AppModule
import org.ironworkschurch.analytics.config.ElasticSearchModule
import org.ironworkschurch.analytics.config.EtlModule
import org.ironworkschurch.analytics.dao.TransactionDao
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingCategoryDetail
import org.ironworkschurch.analytics.to.GivingTransaction
import org.ironworkschurch.analytics.to.PersonDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter.*
import javax.inject.Inject

class SimpleChurchToMySql @Inject constructor (
  private val simpleChurchManager: SimpleChurchManager,
  private val deltaManager: DeltaManager,
  private val baseManager: BaseManager,
  private val transactionDao: TransactionDao,
  private val elasticManager: ElasticManager
) {
  companion object {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule(), AppModule(), ElasticSearchModule())
        .getInstance(SimpleChurchToMySql::class.java).run()
    }
  }

  private fun run() {
    extractAndLoad()
    //elasticManager.loadRollupsToElastic()
  }

  private fun extractAndLoad() {
    logger.info { "Beginning extract and load from SimpleChurch API to MySQL database" }
    val data = fetchData()
    loadBaseTables(data)
    loadDeltaTables()
    loadTransactionRollup()
    logger.info { "Completed extract and load process" }
  }

  private fun loadTransactionRollup() {
    transactionDao.rollupTransactions()
  }

  private fun fetchData(): Data {
    logger.debug { "Retrieving user data" }
    val stopwatch = Stopwatch.createStarted()
    val personDetails = simpleChurchManager.getAllPersonDetails()
    val transactions = simpleChurchManager.getTransactions(personDetails.map { it.uid })
    val givingCategories = simpleChurchManager.getGivingCategories()

    stopwatch.stop()
    logger.debug { "Fetched data in $stopwatch" }
    return Data(personDetails, transactions, givingCategories)
  }

  data class Data(
    val personDetails: List<PersonDetails>,
    val transactions: List<GivingTransaction>,
    val givingCategories: List<GivingCategoryDetail>
  )

  private fun loadDeltaTables() {
    logger.debug { "Copying data to history" }
    val stopwatch = Stopwatch.createStarted()
    deltaManager.loadDeltaTables()
    stopwatch.stop()
    logger.debug { "Completed copying to history in $stopwatch" }
  }

  private fun loadBaseTables(data: Data) {
    logger.debug { "Writing data to MySQL database" }
    val stopwatch = Stopwatch.createStarted()
    baseManager.createMissingTables()

    val (personDetails, transactions, givingCategories) = data

    val flatTransactions = transactions.map {
      FlatGivingTransaction(
        id = it.id,
        uid = it.uid,
        amount = it.amount.replace("$", "").replace(",", "").toDouble(),
        date = LocalDate.parse(it.date, ISO_DATE),
        time = it.time,
        method = it.method,
        transactionId = it.transactionId,
        subscriptionId = it.subscriptionId,
        fee = it.fee,
        note = it.note,
        checkId = it.checkId,
        batchId = it.batchId,
        pledgeId = it.pledgeId,
        checkNumber = it.checkNumber,
        oldNote = it.oldNote,
        sfoSynced = it.sfoSynced,
        qboSynced = it.qboSynced,
        categoryId = it.category.id
      )
    }


    val pairs = listOf(
      "PERSON_BASE" to simpleChurchManager.getFlatPersonDetails(personDetails),
      "FAMILY_MEMBER_BASE" to simpleChurchManager.getFamilyMembers(personDetails),
      "PERSON_GROUP_BASE" to simpleChurchManager.getPersonGroups(personDetails),
      "GROUP_BASE" to simpleChurchManager.getGroupHeaders(personDetails),
      "TRANSACTION_BASE" to flatTransactions,
      "GIVING_CATEGORY_BASE" to givingCategories
    )

    baseManager.loadBaseTables(pairs)
    stopwatch.stop()
    logger.debug { "Wrote data in $stopwatch" }

  }
}