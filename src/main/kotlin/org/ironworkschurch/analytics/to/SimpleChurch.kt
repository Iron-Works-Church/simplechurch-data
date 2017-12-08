package org.ironworkschurch.analytics.to

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class PeopleByInitial(
  val success: Boolean,
  val statusCode: String,
  val data: Map<String, List<PeoplePerson>>)

data class PeoplePerson (
  val joinDate: String,
  val inactiveDate: String,
  val uid: Int,
  val fname: String,
  val preferredName: String,
  val lname: String,
  val male: Int,
  val secondaryEmail: String,
  val rfidtag: String,
  val rfidtagAlt: String,
  val phoneHome: String,
  val phoneCell: String,
  val phoneWork: String,
  val active: Boolean,
  val address: String,
  val city: String,
  val state: String,
  val zipcode: String,
  val country: String?,
  val addressStartDate: String?,
  val addressEndDate: String?,
  val addressLabel: String?,
  val address2: String?,
  val city2: String?,
  val state2: String?,
  val zipcode2: String?,
  val country2: String?,
  val address2StartDate: String?,
  val address2EndDate: String?,
  val address2Label: String?,
  val dateBirth: String,
  val dateBaptism: String,
  val dateDied: String,
  val dateJoined: String,
  val timeLastAttended: String,
  val timeLastTouched: String,
  val date1: String?,
  val date2: String?,
  val date3: String?,
  val date4: String?,
  val date5: String?,
  val date6: String?,
  val date7: String?,
  val date8: String?,
  val date9: String?,
  val date10: String?,
  val text1: String?,
  val text2: String?,
  val text3: String?,
  val text4: String?,
  val text5: String?,
  val text6: String?,
  val text7: String?,
  val text8: String?,
  val text9: String?,
  val text10: String?,
  val text11: String?,
  val text12: String?,
  val text13: String?,
  val text14: String?,
  val text15: String?,
  val envNum: Int,
  val hasPicture: Boolean,
  val checkRoutingNumberHash: String,
  val checkAccountNumberHash: String,
  val raddress: String,
  val rcity: String,
  val rstate: String,
  val rzipcode: String,
  val rcountry: String?,
  val maddress: String,
  val mcity: String,
  val mstate: String,
  val mzipcode: String,
  val mcountry: String?,
  val pname: String,
  val mail: String,
  val checkinNote: String
)

data class PersonPayload(
  val success: Boolean,
  val statusCode: String,
  val data: PersonDetails)

data class PersonDetails(
  val uid: Int,
  val mail: String,
  val updated: String,
  val fname: String,
  val preferredName: String,
  val lname: String,
  val male: Int,
  val secondaryEmail: String,
  val rfidtag: String,
  val rfidtagAlt: String,
  val phoneHome: String,
  val phoneCell: String,
  val phoneWork: String,
  val address: String,
  val city: String,
  val state: String,
  val zipcode: String,
  val country: String?,
  val addressStartDate: String?,
  val addressEndDate: String?,
  val addressLabel: String?,
  val address2: String?,
  val city2: String?,
  val state2: String?,
  val zipcode2: String?,
  val country2: String?,
  val address2StartDate: String?,
  val address2EndDate: String?,
  val address2Label: String?,
  val dateBirth: String,
  val dateBaptism: String,
  val dateDied: String,
  val date1: String,
  val envNum: Int,
  val hasPicture: Boolean,
  val name: String,
  val family: List<FamilyMember>,
  val canEdit: Boolean,
  val canEditPicture: Boolean,
  val canEditAllFields: Boolean,
  val groups: List<Group>,
  val hasUnApprovedChanges: Boolean
)

data class FamilyMember (
  val updated: String,
  val primaryUid: Int,
  val uid: Int,
  val fid: Int,
  val relationship: String,
  val givesWithFamily: Boolean,
  val fname: String,
  val preferredName: String,
  val lname: String,
  val dateBirth: String,
  val dateDied: String,
  val hasPicture: Boolean,
  val male: String,
  val canView: Boolean
) : HasArray {
  override val array: Array<Any?> get() =
    arrayOf(
      updated,
      primaryUid,
      uid,
      fid,
      relationship,
      givesWithFamily,
      fname,
      preferredName,
      lname,
      dateBirth,
      dateDied,
      hasPicture,
      male,
      canView
    )
}

data class Group (
  val gid: Int,
  val name: String,
  val individual: Boolean,
  val selfCheckin: Boolean,
  val childCheckin: Boolean,
  val description: String,
  val address: String,
  val city: String,
  val state: String,
  val zipcode: String,
  val lat: Int,
  val lon: Int,
  val selfAdd: Boolean,
  val selfRequest: Boolean,
  val meetingDay: String,
  val meetingTime: String,
  val maxSize: Int,
  val active: Boolean,
  val visibleMembership: Boolean,
  val checkinChildLabelCnt: Int,
  val checkinGuardianLabelCnt: Int,
  val joinDate: String
)

data class GivingGroups (val gid: Int)

data class PersonGiving (
  val success: Boolean,
  val statusCode: String,
  val data: List<GivingTransaction>)

data class GivingTransaction (
  val id: Int,
  val uid: Int,
  val amount: String,
  val date: String,
  val time: String,
  val method: String,
  val transactionId: String,
  val subscriptionId: String,
  val fee: String,
  val note: String,
  val checkId: String,
  val batchId: String,
  val pledgeId: String,
  val checkNumber: String,
  val oldNote: String,
  val sfoSynced: String,
  val qboSynced: String,
  val category: GivingCategory
)

data class GivingCategory (
  val id: Int,
  val name: String)


data class LoginPayload (
  val success: Boolean,
  val statusCode: String,
  val data: Login
)

data class Login (
  val fname: String,
  val lname: String,
  val uid: Int,
  val session_id: String,
  val org_name: String
)

data class PersonSearchPayload (
  val success: Boolean,
  val statusCode: String,
  val data: List<PersonSearchEntry>
)

data class PersonSearchEntry (
  val fname: String,
  val lname: String,
  val uid: Int
)

data class GivingCategoriesPayload (
  val success: Boolean,
  val statusCode: String,
  val data: List<GivingCategoryDetail>
)

data class GivingCategoryDetail (
  val id: Int,
  val name: String,
  val sortOrder: Int,
  val active: Boolean,
  val taxDeductible: Boolean,
  val onlineGivingEnabled: Boolean
)