package com.agnidating.agni.model.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
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
    @SerializedName("email")
    var email: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("community")
    var community: String,
    @SerializedName("education")
    var education: String,
    @SerializedName("religion")
    var religion: String,
    @SerializedName("occupation")
    var occupation: String,
    @SerializedName("int_gender")
    var intGender: String,
    @SerializedName("int_latitude")
    var intLatitude: String,
    @SerializedName("int_location")
    var intLocation: String,
    @SerializedName("int_longitude")
    var intLongitude: String,
    @SerializedName("is_complete")
    var isComplete: String,
    @SerializedName("is_hide")
    var isHide: String,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("locale")
    var locale: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("max_age")
    var maxAge: String,
    @SerializedName("max_distance")
    var maxDistance: String,
    @SerializedName("min_age")
    var minAge: String,
    @SerializedName("min_distance")
    var minDistance: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("phone_otp")
    var phoneOtp: String,
    @SerializedName("phone_verification")
    var phoneVerification: String,
    @SerializedName("temp_phn")
    var tempPhn: String,
    @SerializedName("profileImg")
    var profileImg: List<Images>
) : Parcelable