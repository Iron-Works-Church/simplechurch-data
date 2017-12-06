package org.ironworkschurch.analytics.dao

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import mu.KLogging
import org.ironworkschurch.analytics.to.*

abstract class SimpleChurchDao {
  companion object: KLogging()
  abstract val gson: Gson

  fun getAllPeople(): List<PeoplePerson> {
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


  abstract fun getGivingPayload(id: Int): String

  fun parsePeople(payload: String) = gson.fromJson(payload, PeopleByInitial::class.java)
    .data
    .values
    .flatten()

  fun parsePerson(payload: String) = gson.fromJson(payload, PersonPayload::class.java)
    .data

  fun parseIndividualGiving(payload: String) = gson.fromJson(payload, PersonGiving::class.java)
    .data
}