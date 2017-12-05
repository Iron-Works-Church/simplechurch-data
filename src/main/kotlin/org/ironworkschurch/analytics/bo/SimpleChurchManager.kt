package org.ironworkschurch.analytics.bo

import com.fasterxml.jackson.databind.ObjectMapper
import org.ironworkschurch.analytics.dao.SimpleChurchDao
import org.ironworkschurch.analytics.dao.SimpleChurchDaoImpl
import org.ironworkschurch.analytics.to.GivingTransaction
import java.io.File

class SimpleChurchManager (val peopleDao:SimpleChurchDao = SimpleChurchDaoImpl()) {


  fun getGivingByHousehold(): List<GivingTransaction> {
    val givingUnits = getGivingUnits()
    println(givingUnits)
    val transactions = givingUnits.flatMap { peopleDao.getIndividualGiving(it) }.distinctBy { it.id }
    println(ObjectMapper().writeValue(File("transactions.json"), transactions))
    return transactions
  }

  private fun getGivingUnits(): List<Int> {
    val people = peopleDao.getAllPeople()
    val personDetails = people.map { peopleDao.getPersonDetails(it.uid) }
    return personDetails.flatMap { it.family }.map {
      when (it.givesWithFamily) {
        true -> it.primaryUid
        false -> it.uid
      }
    }.distinct()
  }
}