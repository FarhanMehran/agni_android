package com.agnidating.agni.model.home


import android.os.Parcelable
import com.agnidating.agni.model.profile.Images
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("address")
    var address: String,
    @SerializedName("bio")
    var bio: String,
    @SerializedName("birth_date")
    var birthDate: String,
    @SerializedName("code")
    var code: String,
    @SerializedName("country_code")
    var countryCode: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("deviceid")
    var deviceid: String,
    @SerializedName("devicetoken")
    var devicetoken: String,
    @SerializedName("devicetype")
    var devicetype: String,
    @SerializedName("type")
    var type: String?,
    @SerializedName("email")
    var email: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("int_gender")
    var intGender: String?,
    @SerializedName("int_latitude")
    var intLatitude: String?,
    @SerializedName("int_location")
    var intLocation: String?,
    @SerializedName("int_longitude")
    var intLongitude: String?,
    @SerializedName("is_complete")
    var isComplete: String,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("locale")
    var locale: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("max_age")
    var maxAge: String?,
    @SerializedName("max_distance")
    var maxDistance: String?,
    @SerializedName("min_age")
    var minAge: String?,
    @SerializedName("min_distance")
    var minDistance: String?,
    @SerializedName("name")
    var name: String,
    @SerializedName("user_msg")
    var userMsg: String?="",
    @SerializedName("phone")
    var phone: String,
    @SerializedName("phone_otp")
    var phoneOtp: String,
    @SerializedName("flowers")
    var flowers: String?,
    @SerializedName("phone_verification")
    var phoneVerification: String,
    @SerializedName("profileImg")
    var profileImg: List<Images>?,
    @SerializedName("source_user_id")
    var sourceId: String?="",
    @SerializedName("target_user_id")
    var targetId: String?="",
    @SerializedName("height")
    var height: String,
    @SerializedName("religion")
    var religion: String,
    @SerializedName("community")
    var community: String,
    @SerializedName("education")
    var education: String,
    @SerializedName("occupation")
    var occupation: String,
    @SerializedName("matchStatus")
    var matchStatus: Int?=0,
    @SerializedName("show")
    var show: Int?=0
) : Parcelable