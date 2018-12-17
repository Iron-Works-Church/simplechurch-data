package org.ironworkschurch.analytics.dao

import com.google.gson.Gson
import mu.KLogging
import org.ironworkschurch.analytics.to.*

abstract class SimpleChurchDao {
  companion object: KLogging()
  abstract val gson: Gson

  fun getAllPeople(): List<PersonSearchEntry> {
    return parsePeople(getAllPeoplePayload())
  }

  abstract fun getAllPeoplePayload(): String

  fun getPersonDetails(id: Int): PersonDetails {
    logger.trace { "Retrieving person details for $id" }
    return parsePerson(getPersonDetailsPayload(id))
  }

  abstract fun getPersonDetailsPayload(id: Int): String

  fun getIndividualGiving(id: Int): List<GivingTransaction> {
    logger.trace { "Retrieving transactions for $id" }
    return parseIndividualGiving(getGivingPayload(id))
  }

  fun getGivingCategories(): List<GivingCategoryDetail> {
    logger.trace { "Retrieving giving categories" }
    return parseGivingCategories(getGivingCategoriesPayload())
  }

  abstract fun getGivingPayload(id: Int): String

  abstract fun getGivingCategoriesPayload(): String

  fun parsePeople(payload: String) = gson.fromJson(payload, PersonSearchPayload::class.java)
    .data

  fun parsePerson(payload: String) = gson.fromJson(payload, PersonPayload::class.java)
    .data

  fun parseIndividualGiving(payload: String) = gson.fromJson(payload, PersonGiving::class.java)
    .data

  fun parseGivingCategories(payload: String) = gson.fromJson(payload, GivingCategoriesPayload::class.java)
    .data
}