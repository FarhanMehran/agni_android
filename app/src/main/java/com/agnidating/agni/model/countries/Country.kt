package com.agnidating.agni.model.countries

import com.google.gson.annotations.SerializedName


data class Country(
    @SerializedName("english_name")
    var englishName: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("name_code")
    var nameCode: String,
    @SerializedName("phone_code")
    var phoneCode: String,
    var selected:Boolean=false
)