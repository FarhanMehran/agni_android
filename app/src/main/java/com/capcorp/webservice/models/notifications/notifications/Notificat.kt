package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class Notificat(

    @field:SerializedName("data")
    val data: List<DataItem?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)