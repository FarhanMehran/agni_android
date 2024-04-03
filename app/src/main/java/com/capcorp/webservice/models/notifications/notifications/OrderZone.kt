package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class OrderZone(

    @field:SerializedName("coordinates")
    val coordinates: List<List<Double?>?>? = null,

    @field:SerializedName("type")
    val type: String? = null
)