package org.ironworkschurch.analytics.dao

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.ironworkschurch.analytics.to.*

abstract class SimpleChurchDao {
  val gson =  Gson()
  val objectMapper = object : ObjectMapper() {
    override fun <T : Any?> readValue(content: String?, valueType: Class<T>?) = gson.fromJson(content, valueType)
  }

  fun getAllPeople(): List<PeoplePerson> {
    return parsePeople(getAllPeoplePayload())
  }

  abstract fun getAllPeoplePayload(): String

  fun getPersonDetails(id: Int): PersonDetails {
    return parsePerson(getPersonDetailsPayload(id))
  }

  abstract fun getPersonDetailsPayload(id: Int): String

  fun getIndividualGiving(id: Int): List<GivingTransaction> {
    return parseIndividualGiving(getGivingPayload(id))
  }


  abstract fun getGivingPayload(id: Int): String

  fun parsePeople(payload: String) = objectMapper.readValue(payload, PeopleByInitial::class.java)
    .data
    .values
    .flatten()

  fun parsePerson(payload: String) = objectMapper.readValue(payload, PersonPayload::class.java)
    .data

  fun parseIndividualGiving(payload: String) = objectMapper.readValue(payload, PersonGiving::class.java)
    .data
}