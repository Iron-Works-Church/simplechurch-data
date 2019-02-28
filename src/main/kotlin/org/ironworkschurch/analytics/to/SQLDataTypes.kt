package org.ironworkschurch.analytics.to

data class FlatPersonDetails (
  val uid: Int,
  val mail: String,
  val updated: String,
  val fname: String,
  val preferredName: String,
  val lname: String,
  val male: Int,
  val secondaryEmail: String?,
  val rfidtag: String?,
  val rfidtagAlt: String?,
  val phoneHome: String?,
  val phoneCell: String?,
  val phoneWork: String?,
  val address: String?,
  val city: String?,
  val state: String?,
  val zipcode: String?,
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
  val dateBirth: String?,
  val dateBaptism: String?,
  val dateDied: String?,
  val date1: String?,
  val envNum: Int,
  val hasPicture: Boolean,
  val name: String,
  val canEdit: Boolean,
  val canEditPicture: Boolean,
  val canEditAllFields: Boolean,
  val hasUnApprovedChanges: Boolean
) : HasArray {
  override val array: Array<Any?> get() =
    arrayOf(
      uid,
      mail,
      updated,
      fname,
      preferredName,
      lname,
      male,
      secondaryEmail,
      rfidtag,
      rfidtagAlt,
      phoneHome,
      phoneCell,
      phoneWork,
      address,
      city,
      state,
      zipcode,
      country,
      addressStartDate,
      addressEndDate,
      addressLabel,
      address2,
      city2,
      state2,
      zipcode2,
      country2,
      address2StartDate,
      address2EndDate,
      address2Label,
      dateBirth,
      dateBaptism,
      dateDied,
      date1,
      envNum,
      hasPicture,
      name,
      canEdit,
      canEditPicture,
      canEditAllFields,
      hasUnApprovedChanges
    )
}

data class PersonGroup (
  val uid: Int,
  val gid: Int,
  val name: String
) : HasArray {
  override val array: Array<Any?> get() =
  arrayOf(
    uid,
    gid,
    name
  )
}

interface HasArray {
  val array: Array<Any?>
}

data class GroupHeader (
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
  val checkinGuardianLabelCnt: Int
) : HasArray {
  override val array: Array<Any?> get() =
    arrayOf(
      gid,
      name,
      individual,
      selfCheckin,
      childCheckin,
      description,
      address,
      city,
      state,
      zipcode,
      lat,
      lon,
      selfAdd,
      selfRequest,
      meetingDay,
      meetingTime,
      maxSize,
      active,
      visibleMembership,
      checkinChildLabelCnt,
      checkinGuardianLabelCnt
    )
}