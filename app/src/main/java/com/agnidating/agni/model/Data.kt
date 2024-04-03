package com.agnidating.agni.model

import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("address")
    var address: String,
    @SerializedName("bio")
    var bio: String,
    @SerializedName("birth_date")
    var birthDate: String,
    @SerializedName("country_code")
    var countryCode: String,
    @SerializedName("message_id")
    var messageId: String,
    @SerializedName("total_flowers")
    var totalFlowers: String,
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
    @SerializedName("interested")
    var interested: String,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("locale")
    var locale: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("phone_otp")
    var phoneOtp: String,
    @SerializedName("phone_verification")
    var phoneVerification: String,
    @SerializedName("statecode")
    var statecode: String,
    @SerializedName("temp_phn")
    var tempPhn: String,
    @SerializedName("subscribed")
    var subscribed: String,
    @SerializedName("is_complete")
    var isComplete: Int
)