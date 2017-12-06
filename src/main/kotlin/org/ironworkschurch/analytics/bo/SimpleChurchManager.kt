package org.ironworkschurch.analytics.bo

import mu.KLogging
import org.ironworkschurch.analytics.dao.SimpleChurchDao
import org.ironworkschurch.analytics.to.*
import javax.inject.Inject


class SimpleChurchManager @Inject constructor (private val simpleChurchDao : SimpleChurchDao) {
  companion object : KLogging()

  fun getGivingByHousehold(): List<GivingTransaction> {
    val givingUnits = getGivingUnits()
    return getTransactions(givingUnits)
  }

  private fun getTransactions(givingUnits: List<Int>): List<GivingTransaction> {
    logger.debug { "Retrieving transactions" }
    return givingUnits
      .flatMap { simpleChurchDao.getIndividualGiving(it) }
      .distinctBy { it.id }
  }

  private fun getGivingUnits(): List<Int> {
    val people = getPersonHeaders()
    val personDetails = getPersonDetails(people)
    return getFamilyMembers(personDetails).map {
      when (it.givesWithFamily) {
        true -> it.primaryUid
        false -> it.uid
      }
    }.distinct()
  }

  fun getFamilyMembers(personDetails: List<PersonDetails>) = personDetails.flatMap { it.family }.distinctBy { it.uid }

  fun getPersonDetails(people: List<PeoplePerson>): List<PersonDetails> {
    logger.debug { "Retrieving person details" }
    return people.map { simpleChurchDao.getPersonDetails(it.uid) }
  }

  fun getPersonHeaders(): List<PeoplePerson> {
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