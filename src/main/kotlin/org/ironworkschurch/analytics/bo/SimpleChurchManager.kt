package org.ironworkschurch.analytics.bo

import com.google.common.util.concurrent.ListeningExecutorService
import mu.KLogging
import org.ironworkschurch.analytics.dao.SimpleChurchDao
import org.ironworkschurch.analytics.to.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import javax.inject.Inject


class SimpleChurchManager @Inject constructor (private val simpleChurchDao : SimpleChurchDao,
                                               private val executorService: ListeningExecutorService) {

    companion object : KLogging()

  fun getGivingByHousehold(): List<GivingTransaction> {
    val givingUnits = getGivingUnits()
    return getTransactions(givingUnits)
  }

  fun getTransactions(givingUnits: List<Int>): List<GivingTransaction> {
    logger.debug { "Retrieving transactions" }

    return givingUnits
      .map { executorService.submit( Callable<List<GivingTransaction>> { simpleChurchDao.getIndividualGiving(it) }) }
      .flatMap { it.get() as List<*> }
      .map { it as GivingTransaction }
      .distinctBy { it.id }
  }

  fun getGivingCategories(): List<GivingCategoryDetail> {
    logger.debug { "Retrieving giving categories" }
    return simpleChurchDao.getGivingCategories()
  }

  private fun getGivingUnits(): List<Int> {
    val personDetails = getAllPersonDetails()
    return getFamilyMembers(personDetails).map {
      when (it.givesWithFamily) {
        true -> it.primaryUid
        false -> it.uid
      }
    }.distinct()
  }

  fun getAllPersonDetails(): List<PersonDetails> {
    val people = getPersonHeaders()
    return getPersonDetails(people)
  }

  fun getFamilyMembers(personDetails: List<PersonDetails>) = personDetails.flatMap { it.family }.distinctBy { it.uid }

  private fun getPersonDetails(people: List<PersonSearchEntry>): List<PersonDetails> {
    logger.debug { "Retrieving person details" }

    return people
      .map { executorService.submit( Callable<PersonDetails> { simpleChurchDao.getPersonDetails(it.uid) } ) }
      .map { it.get() as PersonDetails }
  }

  fun getPersonHeaders(): List<PersonSearchEntry> {
    logger.debug { "Retrieving people list" }
    val allPeople = simpleChurchDao.getAllPeople()
    logger.trace { "Found ${allPeople.size} results" }
    return allPeople
  }



  fun getFlatPersonDetails(personDetails: List<PersonDetails>): List<FlatPersonDetails> {
    return personDetails.map {
      FlatPersonDetails(
        uid = it.uid,
        mail = it.mail,
        updated = it.updated,
        fname = it.fname,
        preferredName = it.preferredName,
        lname = it.lname,
        male = it.male,
        secondaryEmail = it.secondaryEmail,
        rfidtag = it.rfidtag,
        rfidtagAlt = it.rfidtagAlt,
        phoneHome = it.phoneHome,
        phoneCell = it.phoneCell,
        phoneWork = it.phoneWork,
        address = it.address,
        city = it.city,
        state = it.state,
        zipcode = it.zipcode,
        country = it.country,
        addressStartDate = it.addressStartDate,
        addressEndDate = it.addressEndDate,
        addressLabel = it.addressLabel,
        address2 = it.address2,
        city2 = it.city2,
        state2 = it.state2,
        zipcode2 = it.zipcode2,
        country2 = it.country2,
        address2StartDate = it.address2StartDate,
        address2EndDate = it.address2EndDate,
        address2Label = it.address2Label,
        dateBirth = it.dateBirth,
        dateBaptism = it.dateBaptism,
        dateDied = it.dateDied,
        date1 = it.date1,
        envNum = it.envNum,
        hasPicture = it.hasPicture,
        name = it.name,
        canEdit = it.canEdit,
        canEditPicture = it.canEditPicture,
        canEditAllFields = it.canEditAllFields,
        hasUnApprovedChanges = it.hasUnApprovedChanges
      )
    }
  }

  fun getGroupHeaders(personDetails: List<PersonDetails>): List<GroupHeader> {
    return personDetails.flatMap {
      it.groups.map {
        GroupHeader(
          gid = it.gid,
          name = it.name,
          individual = it.individual,
          selfCheckin = it.selfCheckin,
          childCheckin = it.childCheckin,
          description = it.description,
          address = it.address,
          city = it.city,
          state = it.state,
          zipcode = it.zipcode,
          lat = it.lat,
          lon = it.lon,
          selfAdd = it.selfAdd,
          selfRequest = it.selfRequest,
          meetingDay = it.meetingDay,
          meetingTime = it.meetingTime,
          maxSize = it.maxSize,
          active = it.active,
          visibleMembership = it.visibleMembership,
          checkinChildLabelCnt = it.checkinChildLabelCnt,
          checkinGuardianLabelCnt = it.checkinGuardianLabelCnt
        )
      }
    }.distinctBy { it.gid }
  }

  fun getPersonGroups(personDetails: List<PersonDetails>): List<PersonGroup> {
    return personDetails.flatMap { person ->
      person.groups.map { group ->
        PersonGroup(
          uid = person.uid,
          gid = group.gid,
          name = group.name
        )
      }
    }
  }
}