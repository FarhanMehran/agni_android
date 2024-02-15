package com.agnidating.agni.model.countries

import com.google.gson.annotations.SerializedName


data class CountryResponse(
    @SerializedName("country")
    var country: List<Country>
)