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
import org.ironworkschurch.analytics.to.FlatPersonDetails
import org.ironworkschurch.analytics.to.PersonDetails
import org.ironworkschurch.analytics.to.PersonSearchEntry
import javax.inject.Inject

class UpdatePeople @Inject constructor(
  private val simpleChurchManager: SimpleChurchManager,
  private val deltaManager: DeltaManager,
  private val baseManager: BaseManager,
  private val metadataManager: MetadataManager) {

  companion object {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule(), AppModule(), ElasticSearchModule())
        .getInstance(UpdatePeople::class.java).run()
    }
  }

  fun run() {
    val personDetails = fetchData()
    val personSearchEntries = simpleChurchManager.getFlatPersonDetails(personDetails)

    baseManager.createMissingTables()
    baseManager.loadData(personSearchEntries, metadataManager.getTable("PERSON_BASE"))

    val pairs = listOf(
      "PERSON_BASE" to simpleChurchManager.getFlatPersonDetails(personDetails),
      "FAMILY_MEMBER_BASE" to simpleChurchManager.getFamilyMembers(personDetails),
      "PERSON_GROUP_BASE" to simpleChurchManager.getPersonGroups(personDetails)/*,
      "GROUP_BASE" to simpleChurchManager.getGroupHeaders(personDetails)*/
    )

    baseManager.loadBaseTables(pairs)
  }

    private fun fetchData(): List<PersonDetails> {
      logger.debug { "Retrieving user data" }
      val stopwatch = Stopwatch.createStarted()
      return simpleChurchManager.getAllPersonDetails()
  }
}
